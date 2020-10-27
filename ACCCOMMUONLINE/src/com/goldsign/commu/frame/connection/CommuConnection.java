package com.goldsign.commu.frame.connection;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.dao.ExceptionLogDao;
import com.goldsign.commu.app.message.ConstructEceptionLog;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.dao.ComMessageQueueDao;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.manager.CommuThreadManager;
import com.goldsign.commu.frame.manager.MessageProcessor;
import com.goldsign.commu.frame.manager.MessageQueueManager;
import com.goldsign.commu.frame.server.OnlineServer;
import com.goldsign.commu.frame.thread.CommuMessageHandleThread;
import com.goldsign.commu.frame.util.CommuThreadUtil;
import com.goldsign.commu.frame.util.LogDbUtil;
import com.goldsign.commu.frame.util.LogUtil;
import com.goldsign.commu.frame.vo.BridgeBetweenConnectionAndMessage;
import com.goldsign.commu.frame.vo.CommuConnectionControl;
import com.goldsign.commu.frame.vo.CommuHandledMessage;
import com.goldsign.commu.frame.vo.MessageQueue;
import com.goldsign.commu.frame.vo.SynchronizedControl;

/**
 *
 * @author hejj
 */
public class CommuConnection implements Runnable {

    private Socket socket;
    private String id;
    private BufferedInputStream in;
    private OutputStream out;
    private boolean readWithThread;
    // private static ReadTotalCount totalReadedCount = new ReadTotalCount();
    private int frequency = 1;
    private boolean connecting = true;
    private boolean isWriteDB = true;
    private CommuConnectionControl control = new CommuConnectionControl();
    private boolean isData = true;
    private String resultCode = "";
    private final String SEND = "1";
    private final byte STX_B = (byte) 0xEB;
    private final byte ETX = 0x03;
    private final byte QRY = 0x01;
    private final byte NDT = 0x02;
    private final byte DTA = 0x03;
    private final byte[] HEADER = {STX_B, DTA, 0};
    private final byte[] QUERY = {STX_B, QRY, 0, 0, 0, ETX};
    private static Logger logger = Logger.getLogger(CommuConnection.class
            .getName());
    private int connID = -1;
    private static final String CLASS_NAME = CommuConnection.class.getName();
    /**
     * 日志记录使用
     */
    private long hdlStartTime; // 处理的起始时间

    private long hdlEndTime;// 处理的结束时间

    /*
	 * 连接中的消息队列
     */
    private Vector MSG_QUEUE = new Vector();
    private final static SynchronizedControl SYNCONTROL = new SynchronizedControl();
    /*
	 * 非即时退款使用

     */
    private BridgeBetweenConnectionAndMessage bridge = new BridgeBetweenConnectionAndMessage();
    private int dbId = -1;
    private boolean isNeedUnlock = false;

    public void setIsConnnection(boolean isConnected) {
        this.connecting = isConnected;
    }

    public boolean getIsConnnection() {
        return this.connecting;
    }

    public void setDbId(int id) {
        this.dbId = id;
    }

    public int getDbId() {
        return this.dbId;
    }

    public CommuConnection() {
    }

    public MessageQueue getMessageFromConnectionQueue() {

        synchronized (SYNCONTROL) {
            if (MSG_QUEUE.isEmpty()) {
                return null;
            }

            return (MessageQueue) MSG_QUEUE.remove(0);
        }

    }

    public void setMessageInConnectionQueue(MessageQueue mq) {
        synchronized (SYNCONTROL) {
            MSG_QUEUE.add(mq);
        }

    }

    public CommuConnection(Socket aSocket, String aId, int connID)
            throws CommuException {
        socket = aSocket;
        id = aId;

        this.connID = connID;

        frequency = FrameCodeConstant.GetMessageFrequency;
        readWithThread = FrameCodeConstant.ReadWithThread;

        try {
            socket.setSoTimeout(FrameCodeConstant.ReadOneMessageTimeOut);
            in = new BufferedInputStream(socket.getInputStream());
            out = socket.getOutputStream();
        } catch (SocketException e) {
            logger.error("Socket error - " + e);
            throw new CommuException(e.getMessage());
        } catch (IOException e) {
            logger.error("I/O error - " + e);
            throw new CommuException(e.getMessage());
        }
    }

    @Override
    public void run() {
        int serialNo = -1;
        int serialNo2 = 0;
        MessageQueue message = null;
        long endTime;
        long tStartTime;
        Vector fromReader;
        boolean isMessageFromSysQueue = false;
        try {
            while (connecting) {
                this.hdlStartTime = System.currentTimeMillis();

                tStartTime = System.currentTimeMillis();
                message = null;

                serialNo = serialNo + 1;
                if (serialNo > 255) {
                    serialNo = 0; // reset serialNo
                }

                // 从本地连接队列中取消息
                message = getMessageFromConnectionQueue();
                isMessageFromSysQueue = false;

                // 如果本地消息队列没有消息再定时从系统的消息队列中取消息
                if (serialNo2 == 0 && message == null) {
                    message = getMessageFromQueue();
                    if (message != null) {
                        isMessageFromSysQueue = true;
                    }
                }

                serialNo2 = serialNo2 + 1;
                if (serialNo2 > frequency - 1) {
                    serialNo2 = 0; // reset serialNo2
                }

                if (message != null) {
                    message = checkMessage(message);
                    serialNo2 = 0; // reset 0 if last message not null
                }

                // 发送消息
                sendMsg(message, serialNo);

                // 读取消息
                fromReader = readMsg(serialNo);

                // 设置连接及处理消息间的对象值
                setBridgeAttribute();

                // 由后台线程处理读取到的消息
                processReaderResult(id, fromReader, bridge);

                dealConResult(message, isMessageFromSysQueue);

                synchronized (this.control) {
                    if (this.control.getClosed()) {
                        break;
                    }
                }
                endTime = System.currentTimeMillis();
                if ((endTime - tStartTime) >= 3000) {
                    logger.error(id + "-接收消息时间超过3秒为：" + (endTime - tStartTime));
                }
            }
        } catch (ClassNotFoundException e) {
            logger.error(id + " - Error,connection will be closed!", e);
            // 记录日志
            logDB(e);
        } catch (InterruptedIOException e) {
            logger.error(id + " - Time out,connection will be closed!", e);
            // 记录日志
            logDB(e);
        } catch (IOException e) {
            logger.error(id, e);
            logDB(e);
        } catch (Exception e) {
            logger.error(id + " - Error,connection will be closed!", e);
            logDB(e);
        } finally {
            logger.info(id + "- 连接关闭!");
            // 设置连接关闭标志
            this.setIsConnnection(false);

            // 最后处理的消息发送不成功而连接中断，从正在处理消息队列中删除该消息
            if (message != null && isMessageFromSysQueue) {
                MessageQueueManager.removeMessageFromQueueDoing(message);
            }

            try {
                closeResource(message, isMessageFromSysQueue);
            } catch (IOException e) {
                logger.error(id + " - close rror! - " + e);
            } catch (Exception e) {
                logger.error(id + " - close rror! - " + e);
            }
        }
    }

    private void setBridgeAttribute() {

        this.bridge.setConnection(this);

    }

    private void postHandle(MessageQueue message, String id) {
        try {
            if (message != null) {
                updateMessageFlag(message);
                String messageCode = "" + (char) message.getMessage()[0] + (char) message.getMessage()[1];
                LogUtil.writeRecvSendLog(id, SEND, messageCode, message.getMessageSequ(),
                        message.getMessage(), "0");
            }
        } catch (Exception e) {
            logger.error(id + " - " + e);
        }

    }

    /**
     * 将终端请求信息存到线程池中某一个线程的消息队列中
     *
     *
     * @param id 消息来源ip
     * @param aReaderResult 消息
     * @param brigde
     */
    public void processReaderResult(String id, Vector aReaderResult,
            BridgeBetweenConnectionAndMessage brigde) {
        CommuHandledMessage msg;

        CommuMessageHandleThread handleThread;
        resultCode = (String) (aReaderResult.get(0));
        // by zjh
        logger.debug(id + " - resultCode is : " + resultCode);
        if (resultCode.substring(0, 1).equals("0")) {
            connecting = true;
            if (resultCode.equals("0100")) {
                isData = true;
                msg = new CommuHandledMessage(id, aReaderResult, bridge);
                // 取出一个空闲线程
                handleThread = getHandleThread(msg);
                // logger.info("当前消息处理线程名称:" +
                // handleThread.getName());
                handleThread.setHandlingMsg(msg);

                // this.addReadedCount();
            } else {
                isData = false;
            }
        } else {
            connecting = false;
            logger.warn(id + " - 读写终端数据时发生错误，错误id是[ " + resultCode + "]，错误信息是["
                    + FrameCodeConstant.statusOfReadMsg.get(resultCode)
                    + "]!连接将关闭");
        }
    }

    public void processReaderResult(String id, Vector aReaderResult) {
        CommuHandledMessage msg;
        CommuThreadManager manager;
        CommuMessageHandleThread handleThread;
        CommuThreadUtil cmht = new CommuThreadUtil();

        resultCode = (String) (aReaderResult.get(0));
        if (resultCode.substring(0, 1).equals("0")) {
            connecting = true;
            if (resultCode.equals("0100")) {
                isData = true;

                msg = new CommuHandledMessage(id, aReaderResult, bridge);
                manager = new CommuThreadManager();

                if (cmht.isDegradeMode(msg)) {
                    handleThread = manager.getDegradeMsgHandleThread();
                } else {
                    handleThread = manager.getNextMsgHandleThread();
                }

                // logger.info("当前消息处理线程名称:" +
                // handleThread.getName());
                handleThread.setHandlingMsg(msg);

                // this.addReadedCount();
            } else {
                isData = false;
            }
        } else {
            connecting = false;
            logger.warn(id + " - 读写终端数据时发生错误，错误id是[ " + resultCode + "]，错误信息是["
                    + FrameCodeConstant.statusOfReadMsg.get(resultCode)
                    + "]!连接将关闭");
        }
    }

    public void processReaderResultDirectly(String id, Vector aReaderResult,
            BridgeBetweenConnectionAndMessage brigde) {
        MessageProcessor mp = new MessageProcessor(id, aReaderResult, "-1",
                this.bridge);
        mp.run();
    }

    public MessageQueue getMessageFromQueue() {
//        logger.info(id + " - getDataFromQueue.");
        logger.debug(id + " - getDataFromQueue.");
        return MessageQueueManager.getMessageQueue(id);
        /*
		 * ComMessageQueueDao queueDAO = new ComMessageQueueDao(); return
		 * queueDAO.pullQueue(id);
         */
    }

    public void closeConnection() {
        synchronized (this.control) {
            this.control.setClosed(true);
        }
        this.isWriteDB = false;
    }

    private MessageQueue checkMessage(MessageQueue mq) {
        byte[] data = mq.getMessage();
        try {
            if (data != null) {
                if (data.length > 65535) {
                    logger.warn("Message length longer than 65536 bytes.");
//                    updateMessageFlag(mq);
                    LogUtil.writeRecvSendLog(id, SEND, mq.getMessageType(), "", mq.getMessage(),
                            "2");
                    mq = null;
                }
                if (data.length == 0) {
                    logger.warn("Message length is 0.");
                    if (null != mq) {
//                        this.updateMessageFlag(mq);
                        LogUtil.writeRecvSendLog(id, SEND, mq.getMessageType(), "",
                                mq.getMessage(), "2");
                        mq = null;
                    }
                }
            } else {
                logger.warn("Message is null.");
//                updateMessageFlag(mq);
                LogUtil.writeRecvSendLog(id, SEND, mq.getMessageType(), "", null, "2");
                mq = null;
            }
        } catch (Exception e) {
            return null;
        }
        return mq;
    }

    public void updateMessageFlag(MessageQueue mq) {
        // logger.info(id +
        // " - updateFlagQueue which message id is : " +
        // mq.getMessageId());
        ComMessageQueueDao queueDAO = new ComMessageQueueDao();
        queueDAO.updateFlagQueue(mq.getMessageId());
    }

    /*
	 * public void DeleteUnSendMessageQueue_Test(MessageQueue mq) throws
	 * Exception{ ComMessageQueueDao queueDAO = new ComMessageQueueDao();
	 * queueDAO.DeleteUnSendMessageQueue_Test(mq); }
     */
    private void sendData(byte[] b, int aSerialNo) throws IOException {
        HEADER[2] = (byte) aSerialNo;
        out.write(HEADER);
        out.write((byte) ((b.length) % 256));
        out.write((byte) ((b.length) / 256));
        out.write(b);
        out.write(ETX);
        //add by zhongzq
        logger.debug("完整数据[HEAD"+Arrays.toString(HEADER)+",Len["+(byte) ((b.length) % 256)+"],serial["+(byte) ((b.length) / 256)+"],body"+Arrays.toString(b)+",end["+ETX+"]");
    }

    private void sendQuery(int aSerialNo) throws IOException {
        QUERY[2] = (byte) aSerialNo;
        out.write(QUERY);
    }

    /**
     * @return the isNeedUnlock
     */
    public boolean isIsNeedUnlock() {
        return isNeedUnlock;
    }

    /**
     * @param isNeedUnlock the isNeedUnlock to set
     */
    public void setIsNeedUnlock(boolean isNeedUnlock) {
        this.isNeedUnlock = isNeedUnlock;
    }

    /**
     * 从线程池中取出一个线程
     *
     * @param msg
     * @param msg
     * @return 线程对象
     */
    private CommuMessageHandleThread getHandleThread(CommuHandledMessage msg) {
        CommuThreadUtil cmht = new CommuThreadUtil();
        CommuThreadManager manager = new CommuThreadManager();
        // 降级模式，客流信作单线程处理（主要考虑数据库性能低下时，影响整体数据库查询速度，如数据库性能较好，客流采用多线称处理较好）

        CommuMessageHandleThread handleThread;
        // if(cmht.isDegradeMode(msg)||cmht.isTraffic(msg))
        if (cmht.isDegradeMode(msg)) {
            handleThread = manager.getDegradeMsgHandleThread();
        } else {
            handleThread = manager.getNextMsgHandleThread();
        }
        return handleThread;
    }

    private void dealConResult(MessageQueue message,
            boolean isMessageFromSysQueue) throws Exception {
        // 发送消息成功并且连接正常
        if (connecting) {
            if (message != null) {
                // 消息已发送完毕，从正在处理消息队列中删除该消息
                if (isMessageFromSysQueue) {
                    // 更新消息队列中消息的处理标志
                    this.postHandle(message, id);
                    MessageQueueManager.removeMessageFromQueueDoing(message);
                } else {
                    String messageCode = "" + (char) message.getMessage()[0]
                            + (char) message.getMessage()[1];
                    LogUtil.writeRecvSendLog(id, SEND, messageCode, message.getMessageSequ(),
                            message.getMessage(), "0");
                }
            }
        } else {
            if (message != null) {
                // modify by hejj 2011-06-22 由于非即退消息改由连接队列处理，不再更新系统队列

                // this.postHandleForNonInstanceReturn(message);
                // 写发送接收消息日志
                String messageCode = "" + (char) message.getMessage()[0]
                        + (char) message.getMessage()[1];
                LogUtil.writeRecvSendLog(id, SEND, messageCode, message.getMessageSequ(),
                        message.getMessage(), "1");
                // System.out.println("writeRecvSendLog 运行时间："+(endTime-startTime)+" ms");
            }
            logger.error(id + " 对方消息错误，连接将断开!");
        }
    }

    /**
     * 发送消息到终端 当没有取到待发送的消息，发送查询消息 当取到待发送的消息时，发送该消息
     *
     * @param message 待发送的消息
     * @param serialNo 序列号
     *
     * @throws InterruptedException
     * @throws IOException
     */
    private void sendMsg(MessageQueue message, int serialNo)
            throws InterruptedException, IOException {
        byte[] qData;
        if (message != null) {
            // Thread.sleep(ApplicationConstant.SendQueryWait);
            qData = message.getMessage();
            logger.info(id + "- 发送的序列号：[" + serialNo + "] - 发送结果数据到终端"
                    + Arrays.toString(qData));
            sendData(qData, serialNo);
        } else {
            // 暂时屏蔽测试连接问题
            if (!this.isData) {
                Thread.sleep(FrameCodeConstant.SendQueryWait);
            }
            logger.debug(id + "- 发送的序列号[" + serialNo + "] - 发送查询消息到终端");
            sendQuery(serialNo);

        }
    }

    /**
     * 读取终端消息
     *
     * @param serialNo 序列号
     *
     * @return 消息
     * @throws InterruptedException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private Vector readMsg(int serialNo) throws InterruptedException,
            IOException, ClassNotFoundException {
        Vector fromReader;
        if (readWithThread) {
            PipedOutputStream pipeout = new PipedOutputStream();
            PipedInputStream pipein = new PipedInputStream(pipeout);
            ConnectionReader cr = new ConnectionReader(in, pipeout, serialNo);
            cr.setPriority(Thread.NORM_PRIORITY
                    + FrameCodeConstant.ReadThreadPriorityAdd);
            cr.start();
            cr.join(FrameCodeConstant.ReadOneMessageTimeOut);
            cr.stopReader();
            ObjectInputStream oin = new ObjectInputStream(pipein);
            fromReader = (Vector) (oin.readObject());
        } else {
            ConnectionReaderNormal cr2 = new ConnectionReaderNormal(in,
                    serialNo);
            fromReader = cr2.read(id);
        }
        return fromReader;
    }

    private void logDB(Throwable e) {
        this.hdlEndTime = System.currentTimeMillis();
        ExceptionLogDao.insert(ConstructEceptionLog.constructLog(id,
                CLASS_NAME, e.getMessage()));
        // 记录日志
        // by zjh
        LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_SOCKET_EXCHAGE,
                id, this.hdlStartTime, this.hdlEndTime,
                FrameLogConstant.RESULT_HDL_FAIL, Thread.currentThread()
                        .getName(), FrameLogConstant.LOG_LEVEL_ERROR, e
                        .getMessage());
    }

    /**
     * 关闭相关资源
     *
     * @param message
     * @param isMessageFromSysQueue
     * @throws IOException
     */
    private void closeResource(MessageQueue message,
            boolean isMessageFromSysQueue) throws IOException {
        FrameCodeConstant.all_connecting_ip.put(id, "0");
        if (this.isWriteDB) {
            // 写连接关闭日志

            LogUtil.writeConnectLog(new Date(System.currentTimeMillis()), id,
                    "2", "连接关闭");
            // 处理最后一条消息异常，如果该消息是从系统队列中取出，需从当前消息队列中删除及更新消息的处理标志
            // 如果系统在连接时已更新，由于无法判断，中断点，再更新一遍

            if (message != null && isMessageFromSysQueue) {
                MessageQueueManager.removeMessageFromQueueDoing(message);
                // 更新消息队列中消息的处理标志，如是参数下发消息，更新参数下发队列中的标志
                // this.postHandle(message, id);
            }

        }
        socket.setSoLinger(true, 1000);//Socket关闭后，底层Socket延迟3600秒再关闭
//        socket.setSoLinger(true, 0);//Socket关闭后，底层Socket立即关闭
        // 关闭客户端连接的socket
        socket.close();
        
        // 记录非即时退款回复消息日志

        // 从缓存中删除连接对象
        OnlineServer.removeConnection(this.connID);
    }
}
