/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.util;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.vo.SynchronizedControl;
import java.util.Date;

import org.apache.log4j.Logger;

import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;

/**
 *
 * @author hejj
 */
public class LogUtil {

    private static Logger logger = Logger.getLogger(LogUtil.class.getName());
    public static SynchronizedControl SYNCONTROL_RECEIVE_SEND = new SynchronizedControl();

    private final static String insertLogConLable = "insert into " + FrameDBConstant.COM_OL_P + "ol_log_connect(id,connect_datetime,connect_ip,connect_result,remark) values("
            + FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_log_connect.nextval,sysdate,?,?,?)";

    private final static String insertLogRecvSendLable = "insert into " + FrameDBConstant.COM_OL_P + "ol_log_recv_send(id,datetime_rec,ip,type,message_code,message_sequ,message,result) values("
            + FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_log_recv_send.nextval,sysdate,?,?,?,?,?,?)";

    public static void writeConnectLog(java.util.Date time, String ip,
            String result, String remark) {
        // Framelogger.info(FrameDateHelper.datetimeToString(new
        // Date())+"  增加记录com_connect_log");
        DbHelper dbHelper = null;

        Object[] values = {ip, result, remark};
        try {
            dbHelper = new DbHelper("LogUtil",
                    FrameDBConstant.OL_DBCPHELPER.getConnection());
            dbHelper.executeUpdate(insertLogConLable, values);
        } catch (Exception e) {
            logger.error(DateHelper.datetimeToString(new Date())
                    + "  Update " + FrameDBConstant.TABLE_PREFIX + "ol_log_connect error! " + e);
        } finally {
            if (dbHelper != null) {
                try {
                    dbHelper.closeConnection();
                } catch (Exception e) {
                    logger.error("Close connection error! " + e);
                }
            }
        }
    }

    public static void writeRecvSendLog(String ip, String type,
            String messageCode, String messageSequ, byte[] message,
            String result) throws Exception {
        // Framelogger.info(FrameDateHelper.datetimeToString(new
        // Date())+"  增加记录com_recv_send_log");
        DbHelper dbHelper = null;
        try {
            dbHelper = new DbHelper("LogUtil",
                    FrameDBConstant.OL_DBCPHELPER.getConnection());
            Object[] values = {ip, type, messageCode, messageSequ, message,
                result};
            dbHelper.executeUpdate(insertLogRecvSendLable, values);
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            logger.error(DateHelper.datetimeToString(new Date())
                    + "  Insert " + FrameDBConstant.TABLE_PREFIX + "ol_log_recv_send error! " + e);
        } finally {
            if (dbHelper != null) {
                try {
                    dbHelper.closeConnection();
                } catch (Exception e) {
                    logger.error("Close connection error! " + e);
                }
            }
        }
    }

    public static void writeRecvSendLog(java.util.Date time, String ip,
            String type, String messageCode, String messageSequ,
            byte[] message, String result, DbHelper dbHelper) throws Exception {
        Object[] values = {ip, type, messageCode, messageSequ, message, result};
        // synchronized (SYNCONTROL_RECEIVE_SEND) {
        dbHelper.executeUpdate(insertLogRecvSendLable, values);
        // }

    }
}
