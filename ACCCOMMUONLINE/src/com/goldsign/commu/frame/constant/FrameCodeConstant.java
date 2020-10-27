/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.constant;

import java.util.*;

/**
 * @author hejj
 */
public class FrameCodeConstant {


    public static boolean ConsolePrint = true;
    /**
     * 线程是否启动控制***********************************************************
     */
    public static int ConsolePrintFrequency = 1;
    public static boolean StartSocketListener = true;
    public static boolean StartConnectionPoolListener = true;
    /**
     * ***********************************************************
     */
    public static String MessageClassPrefix = "com.goldsign.commu.app.message.";
    public static int SendQueryWait = 1000;
    public static int MonitorRefresh = 10000;
    public static int GetMessageFrequency = 200;
    public static boolean ReadWithThread = false;
    /**
     * Socket config***********************************************************
     */
    public static String ServerIp = "10.99.10.50";
    public static int ServerPort = 8;
    public static int ReadOneByteTimeOutFromEncryptor = 500;
    public static int Port = 6001;
    public static int ReadOneByteTimeOut = 3000;
    public static int ReadOneMessageTimeOut = 3000;
    public static int ThreadSleepTime = 100;
    public static int ThreadPriority = 5;
    public static int MaxThreadNumber = 5;
    public static int MaxSearchNum = 2;
    public static int ReadThreadPriorityAdd = 0;
    public static String UnHanledMsgLogDir = "";
    public static int ThreadBufferCapacity = 1000;
    public static int ThreadBufferIncrement = 1000;
    public static int PriorityThreadBufferCapacity = 1000;
    public static int PriorityThreadBufferIncrement = 1000;
    /**
     * 消息的队列类型***********************************************************
     */
    public static String ThreadMsgQueueTypePri = "1";// 优先队列
    public static String ThreadMsgQueueTypeOrd = "2";// 普通队列
    /**
     * 缓存的基本数据***********************************************************
     */
    public static Map<String, String> CHARGE_DEV_TYPE =
            new HashMap<String, String>() {{
                put("01", "ISM");
                put("02", "TVM");
                put("03", "BOM");
            }};
    public static Map<String, String> ALL_DEV;
    public static Map<String, String> ALL_LINE;
    public static Map<String, String> ALL_DEV_ACC;
    public static Hashtable<String, String> all_connecting_ip = new Hashtable<String, String>();
    public static Hashtable MESSAGE_CLASSES;
    /**
     * ***********************************************************
     */
    public static Hashtable commuConfig = null;
    public static int MessageQueueThreadSleepTime = 60000;
    /**
     * config***********************************************************
     */
    public static long BUFFERCLEAR_SLEEPTIME = 300000;// 300000;//缓存清理的循环时间间隔
    public static String BUFFERCLEAR_RUN_TIME = "0300";// 缓存清理的时间,多个时间点使用＃号分隔
    public static String SQUAD_TIME = "0230";// 运营开始时间
    /**
     * 版本标志***********************************************************
     */
    public static String RECORD_FLAG_CURRENT = "0";// 当前版本
    public static String RECORD_FLAG_FUTURE = "1";// 未来版本

    /**
     * 系统标识***********************************************************
     */
    public static String SYSTEM_COMMU = "2";// 充值在线系统

    // 读取消息的状态信息
    public static Map<String, String> statusOfReadMsg = new HashMap<String, String>();
    // 有效期：单位分钟
    public static int EffectiveDate = 1;

    // 有效期：更新设备消息时间
    public static String UpdateDevInfoTime = "0300";

    // 密钥版本：0正式版本；2测试版本
    public static int KeyVersion = 0x02;// 默认测试版本
    //add by zhongziqi 20181228  单位分
    public static long MINUTE_UNIT = 60 * 1000;
    //add by zhongziqi 20181228 二维码支付订单有效时间参数  默认一小时
    public static long PARA_QR_CODE_PAY_VALID_TIME = 60 * MINUTE_UNIT;
    //订单表状态 20190102 zhongzq
    //订单未支付
    public static final String ORDER_STATUS_NON_PAYMENT = "0";
    //订单已支付
    public static final String ORDER_STATUS_PAID = "1";
    //订单取消
    public static final String ORDER_STATUS_CANCEL = "4";
    //订单支付失败
    public static final String ORDER_STATUS_PAY_FAILURE = "5";
    //订单已退款
    public static final String ORDER_STATUS_REFUNDED = "6";
    //二维码取票业务流水类型
    public static final String BUESINESS_NO_TYPE_QRCODE = "0";
    //二维码支付业务流水类型
    public static final String BUESINESS_NO_TYPE_QRPAY = "1";
    //二维码缓存数据迁移
    public static int PARA_QR_BUFFER_MOVE_DAY = 7;

    public static int PARA_QR_BUFFER_MOVE_CLOCK = 3;

    public static int PARA_QR_BUFFER_MOVE_CONTROL = 0;

    public static int PARA_QR_BUFFER_MOVE_TIME_NO_LIMIT = 1;

    public static final int CONTROL_USED = 1;

    public static int PARA_QR_BUFFER_MOVE_THREAD_SLEEP_TIME = 60 * 60 * 1000;

    public static List<String> PARA_CHANGE_ALLOW_TICKET_TYPE = new ArrayList<>();


}
