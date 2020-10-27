/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.util;

import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author hejj
 */
public class PubUtil {

    private static Logger logger = Logger.getLogger(PubUtil.class.getName());
    private static final byte[] SYN_CONTROL = new byte[0];
    public static String SystemStartDateTime = "";
    private static int messageSequ = 0;

    public PubUtil() {
        super();
        // TODO Auto-generated constructor stub
    }

    public static void handleException(Exception e, Logger lg) throws Exception {
        lg.error("错误:", e);
        throw e;
    }

    public static void handleExceptionNoThrow(Exception e, Logger lg) {
        lg.error("错误:", e);

    }

    public static void handleExceptionForTran(Exception e, Logger lg,
            DbHelper dbHelper) throws Exception {
        if (dbHelper != null) {
            dbHelper.rollbackTran();
        }
        lg.error("错误:", e);
        throw e;
    }

    public static void finalProcess(DbHelper dbHelper) {
        try {
            if (dbHelper != null) {
                dbHelper.closeConnection();
            }
        } catch (SQLException e) {
            logger.error("Fail to close connection", e);
        }
    }

    public static void finalProcessForTran(DbHelper dbHelper) {
        try {
            /*
			 * if (dbHelper != null && !dbHelper.isConClosed() &&
			 * !dbHelper.getAutoCommit()) dbHelper.setAutoCommit(true);
             */
            if (dbHelper != null) {
                dbHelper.closeConnection();
            }

        } catch (SQLException e) {
            logger.error("Fail to close connection", e);
        }
    }
    public static boolean validAllowChangeTicketType(String ticketType) {
        return FrameCodeConstant.PARA_CHANGE_ALLOW_TICKET_TYPE.contains(ticketType);
    }
    public static String getMessageSequ() {
        synchronized (SYN_CONTROL) {
            Date ssdt = new Date(System.currentTimeMillis());
            SystemStartDateTime = DateHelper.dateToString(ssdt);
            messageSequ = messageSequ + 1;
            String timeStr = SystemStartDateTime + "0000000000";
            String sequStr = Integer.toString(messageSequ);
            return timeStr.substring(2, 24 - (sequStr.length())) + sequStr;
        } // return for example yyMMddHHmmss0000000001

    }
}
