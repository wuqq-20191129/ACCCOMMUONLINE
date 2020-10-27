package com.goldsign.commu.app.message;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Date;
import org.apache.log4j.Logger;

import com.goldsign.commu.app.dao.MessageValidateDao;
import com.goldsign.commu.app.vo.Message50Vo;
import com.goldsign.commu.app.vo.Message60Vo;
import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.LogUtil;
import com.goldsign.commu.frame.vo.MessageQueue;

/**
 *
 * @author zhangjh
 */
public class Message50 extends MessageValidate {

    private static Logger logger = Logger.getLogger(Message50.class.getName());

    // private final static byte[] LOCK = new byte[0];
    @Override
    public void run() throws Exception {
        // 充值申请规定的数据区字节长度
        fix_recv_data_length = 34;
        process();
    }

    public void process() throws Exception {
        processMessage();
    }

    public void processMessage() throws Exception {
        // 写数据库
        String returnCode = "00";
        String errCode = "00";
        logger.info("--心跳请求开始--");
        Socket clientSocket = null;
        try {
            // 校验数据区长度
            validateDataLen();

            // 获取客户端的消息
            Message50Vo msg50Vo = getMessage50Vo();
            // 设备相关异常
            MessageValidateDao.isLegalDevice(msg50Vo);
            // 测试加密机的连接
            clientSocket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(
                    FrameCodeConstant.ServerIp, FrameCodeConstant.ServerPort);
            clientSocket.connect(socketAddress,
                    FrameCodeConstant.ReadOneByteTimeOutFromEncryptor);
            Message60Vo msg60Vo = constructMessage60Vo(msg50Vo, returnCode,
                    errCode);
            writeBackMsg(msg60Vo);
            logger.info("--心跳请求响应结束--");
        } catch (Exception e) {
//			logger.error("心跳请求报错：", e);
            // 出现异常返回响应的错误信息给终端
            dealException(e);
            throw e;
        } finally {
            if (null != clientSocket) {
                clientSocket.close();
            }
        }
    }

    private MessageQueue getMessageQueue(Message60Vo msg60Vo) {
        byte[] msg61 = new ConstructMessage60().constructMessage(msg60Vo);
        MessageQueue messageQueue = new MessageQueue();
        messageQueue.setMessageTime(new Date(System.currentTimeMillis()));
        messageQueue.setIpAddress(this.messageFrom);
        messageQueue.setMessage(msg61);
        messageQueue.setIsParaInformMsg("0");
        messageQueue.setParaInformWaterNo(0);
        messageQueue.setLineId("");
        messageQueue.setStationId("");
        messageQueue.setMessageSequ(messageSequ);
        return messageQueue;
    }

    /**
     * 根据客户端穿过来的心跳请求数据，转换成Message50Vo
     *
     * @return Message50Vo对象
     */
    private Message50Vo getMessage50Vo() throws Exception {
        Message50Vo vo = new Message50Vo();
        String messageId = getCharString(0, 2);
        String msgGenTime = getBcdString(2, 7);
        String terminalNo = getCharString(9, 9);
        String samLogicalId = getBcdString(18, 16);
        vo.setMessageId(messageId);
        vo.setMsgGenTime(msgGenTime);
        vo.setTerminalNo(terminalNo);
        vo.setSamLogicalId(samLogicalId);
        return vo;

    }

    private Message60Vo constructMessage60Vo(Message50Vo msg50Vo,
            String returnCode, String errCode) {
        Message60Vo msg60Vo = new Message60Vo();
        msg60Vo.setMessageId("60");
        msg60Vo.setMsgGenTime(DateHelper.dateToString(new Date()));
        msg60Vo.setReturnCode(returnCode);
        msg60Vo.setErrCode(errCode);
        if (null != msg50Vo) {
            msg60Vo.setSamLogicalId(msg50Vo.getSamLogicalId());
            msg60Vo.setTerminalNo(msg50Vo.getTerminalNo());
        }
        return msg60Vo;
    }

    private void sendMsgToConnectionQueue(MessageQueue mq) {
        CommuConnection con = this.bridge.getConnection();
        con.setMessageInConnectionQueue(mq);
    }

    private void writeBackMsg(Message60Vo msg60Vo) {
        // 返回消息
        MessageQueue backMesg = this.getMessageQueue(msg60Vo);
//        try {
//            LogUtil.writeRecvSendLog(null, "localhost", "1",
//                    msg60Vo.getMessageId(), messageSequ, backMesg.getMessage(),
//                    "0", getOLDbHelper());
//        } catch (Exception ex) {
//            logger.error("记录日志发生异常：" + ex);
//        }
        sendMsgToConnectionQueue(backMesg);
    }

    private void dealException(Exception e) {
        String errCode = doException(e);

        Message60Vo msg60Vo = constructMessage60Vo(null, "01", errCode);
        writeBackMsg(msg60Vo);
    }
}
