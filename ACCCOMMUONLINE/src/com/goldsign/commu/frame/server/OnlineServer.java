package com.goldsign.commu.frame.server;

import com.goldsign.commu.app.dao.ExceptionLogDao;
import com.goldsign.commu.app.message.ConstructEceptionLog;
import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.constant.*;
import com.goldsign.commu.frame.dao.DeviceDao;
import com.goldsign.commu.frame.dao.PubDao;
import com.goldsign.commu.frame.dao.TicketDataSelectDao;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.exception.ConfigFileException;
import com.goldsign.commu.frame.listener.CommuConnectionPoolListener;
import com.goldsign.commu.frame.manager.CommuThreadManager;
import com.goldsign.commu.frame.thread.*;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.LogDbUtil;
import com.goldsign.commu.frame.util.LogUtil;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.commu.frame.xml.CommuXMLHandler;
import com.goldsign.lib.db.util.DbcpHelper;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * @author hejj
 */
public class OnlineServer {

    private final String LOGFILE = "Log4jConfig.xml";
    private final String CONFIGFILE = "CommuConfig.xml";
    private final String BackUpLog_TAG = "BackUpLog";
    private final String LogFileName = "commu.log";
    private final String Port_TAG = "Port";
    private static final String ServerIp_TAG = "ServerIp";
    private static final String ServerPort_TAG = "ServerPort";
    private static final String SendMsgToJMJFlag_TAG = "SendMsgToJMJFlag";
    private final String ReadOneByteTimeOut_TAG = "ReadOneByteTimeOut";
    private final String ReadOneMessageTimeOut_TAG = "ReadOneMessageTimeOut";
    private final String MessageClassPrefix_TAG = "MessageClassPrefix";
    private final String SendQueryWait_TAG = "SendQueryWait";
    private final String MonitorRefresh_TAG = "MonitorRefresh";
    private final String ReadWithThread_TAG = "ReadWithThread";
    public static ServerSocket serverSocket;
    @SuppressWarnings("rawtypes")
    public static HashMap connectedSockets = new HashMap();
    public static int connectID = 0;
    private boolean stoped = false;
    private static Logger logger = Logger.getLogger(OnlineServer.class
            .getName());
    private CommuThreadManager ctm = new CommuThreadManager();
    /**
     * 日志记录使用
     */
    private long hdlStartTime; // 处理的起始时间
    private long hdlEndTime;// 处理的结束时间
    private static final String CLASS_NAME = OnlineServer.class.getName();

    public static void main(String[] args) {
        OnlineServer commuServer = new OnlineServer();
        Runtime.getRuntime().addShutdownHook(new CommuStopHook(commuServer));
        commuServer.start();
    }

    public void start() {
        try {
            this.hdlStartTime = System.currentTimeMillis();

            logger.info("通讯服务器正在启动 ......");
            // 从配置文件及数据库取得数据放入ApplicationConstant对应缓存变量中.
            initConfig();

            // 检查配置是否设置合理
            this.checkConfigure();

            // 启动消息处理线程
            ctm.startHandleThreads();

            // 启动连接池监听线程
            if (FrameCodeConstant.StartConnectionPoolListener) {
                this.startConnectionPoolListener();
                logger.info("连接池监听正在启动 ...");
            }

            // 启动线程池监控 (会出现线程挂起状态，考虑先屏蔽）
            this.startThreadMonitor();
            // 启动线程消息队列处理
            this.startMessageQueueThread();
            // 初始化缓存数据
            this.initDB();
            // 初始化设备信息，并定时更新缓存息
            this.startRefreshCacheThread();
            // 日志级别设置
            this.startLogLevelMonitor();

            this.hdlEndTime = System.currentTimeMillis();
            //add by zhongzq 20190827
            if (FrameCodeConstant.CONTROL_USED==FrameCodeConstant.PARA_QR_BUFFER_MOVE_CONTROL) {
                new Thread(new BufferDataMoveThread(), "BufferDataMoveThread").start();
                logger.info("BufferDataMoveThread启动");
            }
            // 记录日志
            LogDbUtil
                    .logForDbDetail(FrameLogConstant.MESSAGE_ID_START, "",
                            this.hdlStartTime, this.hdlEndTime,
                            FrameLogConstant.RESULT_HDL_SUCESS, Thread
                                    .currentThread().getName(),
                            FrameLogConstant.LOG_LEVEL_INFO, "通讯启动");

            // 启动线路消息接收SOCKET服务器
            if (FrameCodeConstant.StartSocketListener) {
                startSocketListener();
            }

        } catch (ConfigFileException e) {
            logger.warn("配置文件读入失败 - " + e);
        } catch (BindException e) {
            logger.warn("端口 " + FrameCodeConstant.Port + "　已经被服务占用！");
            // 记录日志
            this.hdlEndTime = System.currentTimeMillis();
            LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_START, "",
                    this.hdlStartTime, this.hdlEndTime,
                    FrameLogConstant.RESULT_HDL_FAIL, Thread.currentThread()
                            .getName(), FrameLogConstant.LOG_LEVEL_ERROR_SYS, e
                            .getMessage());

        } catch (Exception e) {
            logger.error("Error - ", e);
            this.hdlEndTime = System.currentTimeMillis();
            LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_START, "",
                    this.hdlStartTime, this.hdlEndTime,
                    FrameLogConstant.RESULT_HDL_FAIL, Thread.currentThread()
                            .getName(), FrameLogConstant.LOG_LEVEL_ERROR_SYS, e
                            .getMessage());

        } finally {
            // 如是连接池问题引起socket关闭,则无需关闭程序
            if (!CommuConnectionPoolListener.isSqlConExceptionClose
                    .booleanValue()) {
                logger.info("程序结束处理 ......");
                stop();
            }
        }
    }

    /*
    初始化缓存数据
    */
    private void initDB() {
        //同步合法设备到缓存
        DeviceDao.update();
        //HCE二维码有效天数
        FrameMessageCodeConstant.HCE_QRCODE_VALID_TIME = PubDao.getOlPubFlagInt("2");
        //HCE发票测试标志 0正常,1测试
        FrameMessageCodeConstant.IS_TEST_FLAG = PubDao.getOlPubFlagString("3");
        //add by zhongzq 20190114 ITM二维码有效期 小时
        FrameCodeConstant.PARA_QR_CODE_PAY_VALID_TIME = PubDao.getOlPubFlagInt("4") * FrameCodeConstant.MINUTE_UNIT;
        //add by zhongzq 20190827 迁移时间
        FrameCodeConstant.PARA_QR_BUFFER_MOVE_DAY = Math.abs(PubDao.getOlPubFlagInt("5")) * -1;
        FrameCodeConstant.PARA_QR_BUFFER_MOVE_CLOCK = PubDao.getOlPubFlagInt("6");
        FrameCodeConstant.PARA_QR_BUFFER_MOVE_CONTROL = PubDao.getOlPubFlagInt("7");
        FrameCodeConstant.PARA_CHANGE_ALLOW_TICKET_TYPE = PubDao.getOlPubFlgList("8");
        FrameCodeConstant.PARA_QR_BUFFER_MOVE_TIME_NO_LIMIT = PubDao.getOlPubFlagInt("9");
        FrameCodeConstant.PARA_QR_BUFFER_MOVE_THREAD_SLEEP_TIME = PubDao.getOlPubFlagInt("10")*1000;
        //add by zhongzq 20190910 票卡子类型逻辑卡号对照关系
        FrameMessageCodeConstant.HCE_CARD_LOGIC_NO_MAPPING = TicketDataSelectDao.getTkCardTypeLogicNoMapping("3");
        //add by zhongzq 20191008
        FrameMessageCodeConstant.TICKET_ATTRIBUTE =TicketDataSelectDao.getTicketAttribute();
        //日志级别设置
        CommuLogLevelThread th = new CommuLogLevelThread();
        th.setCurrentLogLevel();
    }

    /**
     * 初始化设备信息，并定时刷新缓存信息
     */
    private void startRefreshCacheThread() {
        RefreshCacheThread thread = new RefreshCacheThread();
        new Thread(thread).start();
    }

    /*
    日志级别设置线程
    */
    private void startLogLevelMonitor() {
        CommuLogLevelThread t = new CommuLogLevelThread();
        t.start();
    }

    private void checkConfigure() {
        if (FrameCodeConstant.ReadOneMessageTimeOut != 3000) {
            LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_CONFIG, "",
                    System.currentTimeMillis(), System.currentTimeMillis(),
                    FrameLogConstant.RESULT_HDL_WARN, Thread.currentThread()
                            .getName(), FrameLogConstant.LOG_LEVEL_WARN,
                    "接口规范定义读消息超时为3秒");
        }

        if (FrameCodeConstant.MaxThreadNumber > 30) {
            LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_CONFIG, "",
                    System.currentTimeMillis(), System.currentTimeMillis(),
                    FrameLogConstant.RESULT_HDL_WARN, Thread.currentThread()
                            .getName(), FrameLogConstant.LOG_LEVEL_WARN,
                    "线程总数量大于30，可能会影响性能");
        }

    }

    public void stop() {
        if (!stoped) {
            stoped = !stoped;
            logger.info("通讯服务器将在1分钟后停止......");
            try {
                serverSocket.close();
                Thread.sleep(60000);
            } catch (Exception e) {
            }
            try {
                logger.info("关闭连接池ST_DBCPHELPER......");
                FrameDBConstant.ST_DBCPHELPER.close();
            } catch (Exception e) {
            }
            try {
                logger.info("关闭连接池OL_DBCPHELPER......");
                FrameDBConstant.OL_DBCPHELPER.close();
            } catch (Exception e) {
            }

            logger.info("处理线程池所有线程......");
            CommuThreadManager.stopHandleThread();

            logger.info("通讯服务器已停止！");
            try {
                Thread.sleep(2000);
            } catch (Exception e) {
            }
        }
    }

    private void startConnectionPoolListener() {
        Hashtable ht;
        CommuConnectionPoolListener ccpl;

        ht = (Hashtable) FrameCodeConstant.commuConfig
                .get(CommuXMLHandler.STConnectionPool_TAG);
        ccpl = new CommuConnectionPoolListener(ht, "st",
                FrameDBConstant.FLAG_ST);
        ccpl.start();

        ht = (Hashtable) FrameCodeConstant.commuConfig
                .get(CommuXMLHandler.OLConnectionPool_TAG);
        ccpl = new CommuConnectionPoolListener(ht, "online",
                FrameDBConstant.FLAG_OL);
        ccpl.start();
    }

    // private void startJmsListener() {
    // JmsListener jl = new JmsListener();
    // Thread t = new Thread(jl, "JmsListener");
    // t.start();
    // FrameCodeConstant.JmsListenerThread = t;
    // }
    private void startMessageQueueThread() {
        MessageQueueThread t = new MessageQueueThread();
        t.start();
        logger.info("消息队列管理线程已启动 ......");
    }

    private void startThreadMonitor() {
        CommuThreadPoolMonitor t = new CommuThreadPoolMonitor();
        t.start();
        logger.info("线程池监控已启动 ...");
    }

    // init config from xml file
    private void initConfig() throws ConfigFileException {
        Hashtable commuConfig;

        // set SystemStartDateTime;
        Date ssdt = new Date(System.currentTimeMillis());
        PubUtil.SystemStartDateTime = DateHelper.dateToString(ssdt);
        try {
            CommuXMLHandler commuXMLHandler = new CommuXMLHandler();
            commuConfig = commuXMLHandler.parseConfigFile(CONFIGFILE);
            FrameCodeConstant.commuConfig = commuConfig;

            // 备份日志
            this.initForBackFile(commuConfig);

            // init log4j
            DOMConfigurator.configureAndWatch(LOGFILE);
            logger.info("日志配置设置成功");

            // 初始化socket配置
            this.initSocket(commuConfig);
            // 线程监控配置
            this.initThreadMonitor(commuConfig);
            // 初始化应用相关配置
            this.initApp(commuConfig);

            // 初始化数据库连接池
            this.initFromConnectionPool(commuConfig);

            // 初始化线程池配置
            this.initThreadPool(commuConfig);

            // 初始化线程池监控
            this.initThreadPoolMonitor(commuConfig);

            // 初始化启动选项控制
            this.initFromStartOption(commuConfig);

            // Initialize messages
//            FrameCodeConstant.MESSAGE_CLASSES = (Hashtable) commuConfig
//                    .get(CommuXMLHandler.Messages_TAG);

            initStatusOfReadMsg();
        } catch (Exception e) {
            logger.error("初始化 " + CONFIGFILE + " 错误! " + e);
            throw new ConfigFileException("初始化 " + CONFIGFILE + " 错误! " + e);
        }

    }

    private void initForBackFile(Hashtable commuConfig)
            throws ConfigFileException {
        String value = (String) commuConfig.get(BackUpLog_TAG);
        if (value == null || value.length() == 0) {
            throw new ConfigFileException("需配置Tag <" + BackUpLog_TAG
                    + "> 日志备份文件!");
        } else {
            String oldLog = DateHelper.datetimeToString(new Date()) + ".log";
            File oldLogFile = new File(value, oldLog);
            File logFile = new File(LogFileName);
            if (logFile.exists()) {
                logFile.renameTo(oldLogFile);
                logger.info("日志备份");
            }

        }
    }

    //线程监控配置
    private void initThreadMonitor(Hashtable commuConfig)
            throws ConfigFileException {
        String value = (String) commuConfig
                .get(CommuXMLHandler.ThreadReleaseResourceWaitCount_TAG);
        if (value != null && value.length() != 0) {
            FrameThreadPoolConstant.ThreadReleaseResourceWaitCount = Integer
                    .parseInt(value);
        }
    }

    // Socket config
    private void initSocket(Hashtable commuConfig) throws ConfigFileException {
        String value = (String) commuConfig.get(ServerIp_TAG);
        if (value != null && value.length() != 0 && !"".equals(value.trim())) {
            FrameCodeConstant.ServerIp = value;
            logger.info("加密机ip:" + FrameCodeConstant.ServerIp);
        } else {
            logger.error("加密机ip没有配置");
            throw new ConfigFileException("加密机ip没有配置");
        }

        value = (String) commuConfig.get(ServerPort_TAG);
        if (value != null && value.length() != 0 && !"".equals(value.trim())) {
            FrameCodeConstant.ServerPort = Integer.parseInt(value);
            logger.info("加密机端口:" + FrameCodeConstant.ServerPort);
        } else {
            logger.error("加密机端口没有配置");
            throw new ConfigFileException("加密机端口没有配置");
        }

        value = (String) commuConfig.get(Port_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.Port = Integer.parseInt(value);
        } else {
            logger.error("前置机端口没有配置");
            throw new ConfigFileException("前置机端口没有配置");
        }

        value = (String) commuConfig.get(ReadOneByteTimeOut_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.ReadOneByteTimeOut = Integer.parseInt(value);
        }

        value = (String) commuConfig.get(ReadOneMessageTimeOut_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.ReadOneMessageTimeOut = Integer.parseInt(value);
        }

        value = (String) commuConfig
                .get(CommuXMLHandler.MessageQueueThreadSleepTime_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.MessageQueueThreadSleepTime = Integer
                    .parseInt(value);
        }
        value = (String) commuConfig
                .get(CommuXMLHandler.ConsolePrintFrequency_TAG);
        if (value != null && value.length() != 0 && !value.equals("0")) {
            FrameCodeConstant.ConsolePrintFrequency = Integer.parseInt(value);
        }
    }

    // 初始化应用相关配置
    private void initApp(Hashtable commuConfig) throws ConfigFileException {
        String value = (String) commuConfig.get(MessageClassPrefix_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.MessageClassPrefix = value;
        }

        value = (String) commuConfig.get(SendQueryWait_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.SendQueryWait = Integer.parseInt(value);
        }

        value = (String) commuConfig.get(MonitorRefresh_TAG);
        if (value != null && value.length() != 0) {
            FrameCodeConstant.MonitorRefresh = Integer.parseInt(value);
        }

        value = (String) commuConfig.get(ReadWithThread_TAG);
        if (value != null && value.length() != 0) {
            if (value.equals("false")) {
                FrameCodeConstant.ReadWithThread = false;
            } else {
                FrameCodeConstant.ReadWithThread = true;
            }
        } else {
            FrameCodeConstant.ReadWithThread = true;
        }
        //有效期内
        FrameCodeConstant.EffectiveDate = Integer.parseInt((String) commuConfig
                .get("EffectiveDate"));

        FrameCodeConstant.UpdateDevInfoTime = (String) commuConfig
                .get("UpdateDevInfoTime");
        // 密钥版本
        FrameCodeConstant.KeyVersion = Integer.parseInt((String) commuConfig.get("KeyVersion"), 16);
        logger.info("读取到的密钥版本：" + FrameCodeConstant.KeyVersion);
        //根据加密机版本号更新加密机参数索引
        FrameMessageCodeConstant.updateVersionEncryParam();
    }

    private void initThreadPoolMonitor(Hashtable commuConfig) {
        Hashtable ht = (Hashtable) commuConfig
                .get(CommuXMLHandler.ThreadPoolMonitor_TAG);
        FrameThreadPoolConstant.TPMonitorThreadSleepTime = Integer
                .parseInt((String) ht.get("TPMonitorThreadSleepTime"));
        FrameThreadPoolConstant.ThreadMsgAnalyzeClassPrefix = (String) ht
                .get("ThreadMsgAnalyzeClassPrefix");
        FrameThreadPoolConstant.ThreadMsgHandUpMaxNumberAllow = Integer
                .parseInt((String) ht.get("ThreadMsgHandUpMaxNumberAllow"));

    }

    private void initThreadPool(Hashtable commuConfig) {
        // 获得线程池的配置属性

        Hashtable ht = (Hashtable) commuConfig
                .get(CommuXMLHandler.CommunicationThreadPool_TAG);
        FrameCodeConstant.MaxThreadNumber = Integer.parseInt((String) ht
                .get("MaxThreadNumber"));
        FrameCodeConstant.MaxSearchNum = Integer.parseInt((String) ht
                .get("MaxSearchNum"));
        FrameCodeConstant.ThreadSleepTime = Integer.parseInt((String) ht
                .get("ThreadSleepTime"));
        FrameCodeConstant.ThreadPriority = Thread.NORM_PRIORITY
                + Integer.parseInt((String) ht.get("ThreadPriorityAdd"));
        FrameCodeConstant.UnHanledMsgLogDir = (String) ht
                .get("UnHanledMsgLogDir");

        FrameCodeConstant.ThreadBufferCapacity = Integer.parseInt((String) ht
                .get("ThreadBufferCapacity"));
        FrameCodeConstant.ThreadBufferIncrement = Integer.parseInt((String) ht
                .get("ThreadBufferIncrement"));
        FrameCodeConstant.PriorityThreadBufferCapacity = Integer
                .parseInt((String) ht.get("PriorityThreadBufferCapacity"));
        FrameCodeConstant.PriorityThreadBufferIncrement = Integer
                .parseInt((String) ht.get("PriorityThreadBufferIncrement"));

        FrameCodeConstant.ReadThreadPriorityAdd = Integer.parseInt((String) ht
                .get("ReadThreadPriorityAdd"));

        FrameCodeConstant.ConsolePrint = Boolean.valueOf(
                (String) commuConfig.get("ConsolePrint")).booleanValue();
        FrameCodeConstant.GetMessageFrequency = Integer
                .parseInt((String) commuConfig.get("GetMessageFrequency"));

        // 获得连接池监听线程配置属性
        ht = (Hashtable) commuConfig
                .get(CommuXMLHandler.ConnectionPoolListenerThread_TAG);
        FrameDBConstant.CPListenerThreadSleepTime = Integer
                .parseInt((String) ht
                        .get(CommuXMLHandler.CPListenerThreadSleepTime_TAG));
        FrameDBConstant.TestSql = (String) ht
                .get(CommuXMLHandler.TestSql_TAG);

        FrameDBConstant.SqlMsgHandleSleepTime = Integer.parseInt((String) ht
                .get(CommuXMLHandler.SqlMsghandleSleepTime_TAG));
    }

    private void initFromStartOption(Hashtable commuConfig)
            throws ConfigFileException {
        // 获得控制启动组件配置属性

        String value = (String) commuConfig
                .get(CommuXMLHandler.StartSocketListener_TAG);
        FrameCodeConstant.StartSocketListener = Boolean.valueOf(value)
                .booleanValue();
        value = (String) commuConfig
                .get(CommuXMLHandler.StartConnectionPoolListener_TAG);
        FrameCodeConstant.StartConnectionPoolListener = Boolean.valueOf(value)
                .booleanValue();

    }

    private void initFromConnectionPool(Hashtable commuConfig)
            throws ConfigFileException {

        try {
            // 初始化数据库online连接池
            Hashtable ht = (Hashtable) commuConfig
                    .get(CommuXMLHandler.OLConnectionPool_TAG);
            FrameDBConstant.OL_DBCPHELPER = new DbcpHelper(
                    (String) ht.get("Driver"), (String) ht.get("URL"),
                    (String) ht.get("Database"), (String) ht.get("User"),
                    (String) ht.get("Password"), Integer.parseInt((String) ht
                    .get("MaxActive")), Integer.parseInt((String) ht
                    .get("MaxIdle")), Integer.parseInt((String) ht
                    .get("MaxWait")));

            logger.info("数据连接池OLConnectionPool创建!");

            // 初始化数据库st连接池
            ht = (Hashtable) commuConfig
                    .get(CommuXMLHandler.STConnectionPool_TAG);
            FrameDBConstant.ST_DBCPHELPER = new DbcpHelper(
                    (String) ht.get("Driver"), (String) ht.get("URL"),
                    (String) ht.get("Database"), (String) ht.get("User"),
                    (String) ht.get("Password"), Integer.parseInt((String) ht
                    .get("MaxActive")), Integer.parseInt((String) ht
                    .get("MaxIdle")), Integer.parseInt((String) ht
                    .get("MaxWait")));

            logger.info("数据连接池STConnectionPool创建!");

        } catch (ClassNotFoundException ex) {
            throw new ConfigFileException(ex.getMessage());
        }

    }

    public void startSocketListener() throws BindException, IOException {
        serverSocket = new ServerSocket(FrameCodeConstant.Port);
        OnlineServer.connectID = 0;

        logger.info("服务器正常启动！正在监听端口 : " + FrameCodeConstant.Port);
        String ip;
        while (true) {
            this.hdlStartTime = System.currentTimeMillis();
            OnlineServer.connectID++;

            Socket socket = serverSocket.accept();

            ip = socket.getInetAddress().getHostAddress();
            int port = socket.getPort();
            logger.info("接受新的连接请求，来自 " + ip + ":" + port);
            try {
                int checkResult = checkConnection(ip);
                if (checkResult == 0) {
                    // 从消息队列及缓存删除未处理非及时退款消息
//                     this.removeNonInstantMsgFromQueue(ip);
                    FrameCodeConstant.all_connecting_ip.put(ip, "1");
//                    String id = ip;//删除多余变量 by zhongziqi 20190301
                    logger.info("新连接处理中");
                    CommuConnection commuConnection = new CommuConnection(
                            socket, ip, OnlineServer.connectID);

                    OnlineServer.addConnection(OnlineServer.connectID,
                            commuConnection);
                    // this.addConnection(ip, commuConnection);

                    Thread t = new Thread(commuConnection, ip);
                    t.setPriority(Thread.NORM_PRIORITY
                            + FrameCodeConstant.ReadThreadPriorityAdd);
                    t.start();
                    LogUtil.writeConnectLog(
                            new Date(System.currentTimeMillis()), ip, "0",
                            "创建连接成功");
                } else {
                    if (checkResult == 1) {
                        logger.error("连接不合法！地址：" + ip);
                        throw new CommuException("连接不合法！地址：" + ip);
                    }
                    if (checkResult == 2) {
                        logger.error("连接已经存在！地址：" + ip);
                        throw new CommuException("连接已经存在！地址：" + ip);
                    }
                }
            } catch (CommuException e) {
                LogUtil.writeConnectLog(new Date(System.currentTimeMillis()),
                        ip, "1", "创建连接失败");
                logger.error("创建连接失败 - " + e);
                ExceptionLogDao.insert(ConstructEceptionLog.constructLog(ip,
                        CLASS_NAME, "创建连接失败 - " + e.getMessage()));
                this.hdlEndTime = System.currentTimeMillis();
                // 记录日志
                LogDbUtil.logForDbDetail(
                        FrameLogConstant.MESSAGE_ID_CONNECTION, "",
                        this.hdlStartTime, this.hdlEndTime,
                        FrameLogConstant.RESULT_HDL_FAIL, Thread
                                .currentThread().getName(),
                        FrameLogConstant.LOG_LEVEL_ERROR, e.getMessage());
            }
        }
    }

    public static void removeConnection(int connID) {
        Integer iConnID = new Integer(connID);
        synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_CONNECTION) {
            if (connectedSockets.containsKey(iConnID)) {
                connectedSockets.remove(iConnID);
            }
        }
    }

    public static void addConnection(int connID, CommuConnection con) {
        Integer iConnID = new Integer(connID);
        synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_CONNECTION) {
            if (connectedSockets.containsKey(iConnID)) {
                connectedSockets.remove(iConnID);
            }
            connectedSockets.put(iConnID, con);
        }
    }

    public static void addConnection(String ip, CommuConnection con) {

        synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_CONNECTION) {
            if (connectedSockets.containsKey(ip)) {
                connectedSockets.remove(ip);
            }
            connectedSockets.put(ip, con);
        }
    }

    public static void closeConnectedSockets() {
        synchronized (FrameSynchronizeConstant.CONTROL_SOCKET_CONNECTION) {
            if (connectedSockets.isEmpty()) {
                return;
            }
            Set keys = connectedSockets.keySet();
            Iterator it = keys.iterator();
            Integer key;
            CommuConnection con;

            while (it.hasNext()) {
                key = (Integer) it.next();
                con = (CommuConnection) connectedSockets.get(key);
                con.closeConnection();
            }
            connectedSockets.clear();

        }
    }

    private int checkConnection(String ip) {

        if (FrameCodeConstant.ALL_DEV.containsValue(ip) || FrameCodeConstant.ALL_LINE.containsValue(ip)
//                || FrameCodeConstant.ALL_DEV_ACC.containsValue(ip)
        ) {
            return 0;
        }
        logger.info("设备ip非法:" + ip);
        return 1;
    }

    private void initStatusOfReadMsg() {
        FrameCodeConstant.statusOfReadMsg.put("1002", "消息有误，读取不到数据，或者发生IO 异常.");
        FrameCodeConstant.statusOfReadMsg.put("1101", "消息头开始标识不正确.");
        FrameCodeConstant.statusOfReadMsg.put("1102", "读取的数据类型不正确.");
        FrameCodeConstant.statusOfReadMsg.put("1103", "读取的序列号不正确.");
        FrameCodeConstant.statusOfReadMsg.put("1104", "读取的消息结束标识不正确.");
        FrameCodeConstant.statusOfReadMsg.put("0101", "读取的信息不还有数据区，为查询数据包或者没数据消息包结构.");
        FrameCodeConstant.statusOfReadMsg.put("0100", "读取的信息中包含数据区.");
        FrameCodeConstant.statusOfReadMsg.put("1201", "读取的实际数据长度与数据包中传入长度不一致.");
        FrameCodeConstant.statusOfReadMsg.put("1105", "读取信息被终止");
    }
}
