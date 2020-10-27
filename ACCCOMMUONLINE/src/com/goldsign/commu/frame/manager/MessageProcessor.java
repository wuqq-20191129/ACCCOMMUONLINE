/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.manager;

import com.goldsign.commu.app.dao.ExceptionLogDao;
import com.goldsign.commu.app.message.ConstructEceptionLog;
import com.goldsign.commu.frame.buffer.CommuExceptionBuffer;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.exception.JmsException;
import com.goldsign.commu.frame.message.MessageBase;
import com.goldsign.commu.frame.util.LogUtil;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.commu.frame.vo.BridgeBetweenConnectionAndMessage;
import com.goldsign.lib.db.util.DbcpHelper;
import java.util.Date;
import java.util.Vector;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author hejj
 */
public class MessageProcessor {

    private Vector v;
    private String ip;
    private String threadNum;// 线程号

    private BridgeBetweenConnectionAndMessage bridge;// 连接与消息处理器间的桥

    private static Logger logger = Logger.getLogger(MessageProcessor.class
            .getName());
    private static final String CLASS_NAME = MessageProcessor.class.getName();

    public MessageProcessor(String aId, Vector aV) {
        this.ip = aId;
        this.v = aV;
    }

    public MessageProcessor(String aId, Vector aV, String threadNum,
            BridgeBetweenConnectionAndMessage bridge) {
        this.ip = aId;
        this.v = aV;
        this.threadNum = threadNum;
        this.bridge = bridge;

    }

    public void run() {
        String messageId = null;
        String messageSequ = null;
        MessageBase msg = null;
        byte[] data = null;

        DbcpHelper st_dbcpHelper = FrameDBConstant.ST_DBCPHELPER;
        DbcpHelper ol_dbcpHelper = FrameDBConstant.OL_DBCPHELPER;
        try {
            data = (byte[]) (v.get(1));
            messageId = "" + (char) data[0] + (char) data[1];
            messageSequ = PubUtil.getMessageSequ();

//            String messageClass = (String) FrameCodeConstant.MESSAGE_CLASSES
//                    .get(messageId);
//            msg = (MessageBase) Class.forName(
//                    FrameCodeConstant.MessageClassPrefix + messageClass)
//                    .newInstance();
            msg = (MessageBase) Class.forName(
                    FrameCodeConstant.MessageClassPrefix + messageId)
                    .newInstance();
            msg.init(ip, messageSequ, data, st_dbcpHelper, ol_dbcpHelper,
                    this.threadNum, this.bridge, messageId);
            msg.run();
            
            LogUtil.writeRecvSendLog(ip, "0", messageId, messageSequ, data, "0");
            // 统计正常处理的消息包
            // this.addHandledCount();
        } catch (JmsException e) {
            try {
                LogUtil.writeRecvSendLog(ip, "0", messageId, messageSequ, data,
                        "1");
            } catch (Exception ex) {
            }
        } catch (SQLException e) {
            try {
                LogUtil.writeRecvSendLog(ip, "0", messageId, messageSequ, data,
                        "1");
                // 连接数据库异常

            } catch (Exception ex) {
                logger.error(e);
            }

            if (e.getErrorCode() == 0) {
                logger.error("捕获连接数据库异常:" + "错误代码 " + e.getErrorCode() + " 消息"
                        + e.getMessage() + "消息将放入缓存恢复连接时处理");
                CommuExceptionBuffer.setSqlUnHandleMsgs(this.ip, data);
                // 统计异常处理的消息包
                // this.addExHandledCount();
            } else {
                try {
                    LogUtil.writeRecvSendLog(ip, "0", messageId, messageSequ,
                            data, "1");
                } catch (Exception ex) {
                    logger.error(e);
                }
            }
        } catch (Exception e) {
            try {
                LogUtil.writeRecvSendLog(ip, "0", messageId, messageSequ, data,
                        "1");
                logger.error("Invoke message(id=" + messageId
                        + ") error! Message sequ:" + messageSequ + "." + e);
                logger.error("Invoke message(id=" + messageId
                        + ") error! Message sequ:" + messageSequ + "." + e);
            } catch (Exception ex) {
            }
        } finally {
            if (null != msg) {
                msg.release();
            }
        }
    }

    public void runMsg(Vector msgInProcess) {
        String messageId = null;
        String messageSequ = null;
        MessageBase msg = null;
        byte[] data = null;
        DbcpHelper st_dbcpHelper = FrameDBConstant.ST_DBCPHELPER;
        DbcpHelper ol_dbcpHelper = FrameDBConstant.OL_DBCPHELPER;
        try {
            data = (byte[]) (v.get(1));
            messageId = "" + (char) data[0] + (char) data[1];
            messageSequ = PubUtil.getMessageSequ();

//            String messageClass = (String) FrameCodeConstant.MESSAGE_CLASSES
//                    .get(messageId);
//            msg = (MessageBase) Class.forName(
//                    FrameCodeConstant.MessageClassPrefix + messageClass)
//                    .newInstance();
            msg = (MessageBase) Class.forName(
                    FrameCodeConstant.MessageClassPrefix + messageId)
                    .newInstance();
            msg.init(ip, messageSequ, data, st_dbcpHelper,
                    ol_dbcpHelper, this.threadNum,
                    this.bridge, messageId);
            // 返回正在处理的消息，以便是否手工释放连接资源
            msgInProcess.clear();
            msgInProcess.add(msg);

            msg.run();
            LogUtil.writeRecvSendLog(new Date(System.currentTimeMillis()), ip,
                    "0", messageId, messageSequ, data, "0",
                    msg.getOLDbHelper());//记录成功（0：成功1：失败）接收（0:请求消息1：响应消息）消息

            // 统计正常处理的消息包
            // this.addHandledCount();
        } catch (SQLException e) {
            // 连接数据库异常

            if (e.getErrorCode() == 0) {
                logger.info("捕获连接数据库异常:" + "错误代码 " + e.getErrorCode() + " 消息"
                        + e.getMessage() + "消息将放入缓存恢复连接时处理");
                e.printStackTrace();
                CommuExceptionBuffer.setSqlUnHandleMsgs(this.ip, data);
                // 统计异常处理的消息包
                // this.addExHandledCount();
            } else {
                dealException(messageId, messageSequ, e, msg, data);
            }
        } catch (Exception e) {
            dealException(messageId, messageSequ, e, msg, data);
        } finally {
            if (null != msg) {
                msg.release();
            }

        }

    }

    /**
     * 发生异常时记录交易信息到数据库
     * @param messageId 消息类型
     * @param messageSequ 序列号
     * @param e 异常消息
     * @param msg 消息处理类
     * @param data 接收到的数据
     */
    private void dealException(String messageId, String messageSequ,
            Throwable e, MessageBase msg, byte[] data) {
        logger.error("Invoke message(id=" + messageId
                + ") error! Message sequ:" + messageSequ + "." + e.getMessage());
        ExceptionLogDao.insert(ConstructEceptionLog.constructLog(ip,
                CLASS_NAME,
                e.getMessage() == null ? "exception" : e.getMessage()));
        try {
            if (null != msg) {
                LogUtil.writeRecvSendLog(new Date(System.currentTimeMillis()),
                        ip, "0", messageId, messageSequ, data, "1",
                        msg.getOLDbHelper());
            }
        } catch (Exception ex) {
            logger.error("记录交易信息到数据库发生异常：" + ex.getMessage());
        }
    }
}
