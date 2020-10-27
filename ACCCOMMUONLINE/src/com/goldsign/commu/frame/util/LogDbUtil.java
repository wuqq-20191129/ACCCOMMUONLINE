/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.util;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.vo.LogCommuVo;
import com.goldsign.commu.frame.vo.SynchronizedControl;

import com.goldsign.lib.db.util.DbHelper;

import java.util.Date;

import org.apache.log4j.Logger;

/**
 *
 * @author hejj
 */
public class LogDbUtil {

    private static Logger logger = Logger.getLogger(LogDbUtil.class.getName());
    private static SynchronizedControl SYN_CONTROL = new SynchronizedControl();
    private final static String[] messageIds = {
        FrameLogConstant.MESSAGE_ID_NON_RETURN,
        FrameLogConstant.MESSAGE_ID_START,
        FrameLogConstant.MESSAGE_ID_CONFIG,
        FrameLogConstant.MESSAGE_ID_PUSH_QUEUE,
        FrameLogConstant.MESSAGE_ID_PARAM_COMMU_QUEUE,
        FrameLogConstant.MESSAGE_ID_CONNECTION,
        FrameLogConstant.MESSAGE_ID_SOCKET_EXCHAGE,
        FrameLogConstant.MESSAGE_ID_FTP};
    private final static String[] messageNames = {
        FrameLogConstant.MESSAGE_ID_NON_RETURN_NAME,
        FrameLogConstant.MESSAGE_ID_START_NAME,
        FrameLogConstant.MESSAGE_ID_CONFIG_NAME,
        FrameLogConstant.MESSAGE_ID_PUSH_QUEUE_NAME,
        FrameLogConstant.MESSAGE_ID_PARAM_COMMU_QUEUE_NAME,
        FrameLogConstant.MESSAGE_ID_CONNECTION_NAME,
        FrameLogConstant.MESSAGE_ID_SOCKET_EXCHAGE_NAME,
        FrameLogConstant.MESSAGE_ID_FTP_NAME};
    private final static String[] levelTexts = {
        FrameLogConstant.LOG_LEVEL_INFO_TEXT,
        FrameLogConstant.LOG_LEVEL_WARN_TEXT,
        FrameLogConstant.LOG_LEVEL_ERROR_TEXT,
        FrameLogConstant.LOG_LEVEL_ERROR_SYS_TEXT,};
    private final static String[] levels = {FrameLogConstant.LOG_LEVEL_INFO,
        FrameLogConstant.LOG_LEVEL_WARN, FrameLogConstant.LOG_LEVEL_ERROR,
        FrameLogConstant.LOG_LEVEL_ERROR_SYS};

    public LogDbUtil() {
        super();
        // TODO Auto-generated constructor stub
    }

    // 根据级别控制明细输出，如级别>=当前设定的级别，当前级别是警告，而级别是信息的，其不输出
    public static int logForDbDetail(LogCommuVo vo, String level)
            throws Exception {
        if (level.compareTo(FrameLogConstant.LOG_LEVEL_CURRENT) >= 0) {
            return logForDbDetail(vo);
        }
        return 0;

    }

    public static int logForDbDetail(LogCommuVo vo, String level,
            DbHelper dbHelper) throws Exception {
        if (level.compareTo(FrameLogConstant.LOG_LEVEL_CURRENT) >= 0) {
            return logForDbDetail(vo, dbHelper);
        }
        return 0;

    }

    public static int logForDbDetail(String messageId, String messageFrom,
            long startTime, long endTime, String result, String hdlThread,
            String level) throws Exception {
        LogCommuVo vo = getLogCommuDetailVo(messageId, messageFrom, startTime,
                endTime, result, hdlThread, level);
        int n = 0;
        synchronized (SYN_CONTROL) {
            n = logForDbDetail(vo, level);
        }
        return n;

    }

    public static int logForDbDetail(String messageId, String messageFrom,
            long startTime, long endTime, String result, String hdlThread,
            String level, DbHelper dbHelper) throws Exception {
        LogCommuVo vo = getLogCommuDetailVo(messageId, messageFrom, startTime,
                endTime, result, hdlThread, level);
        int n = 0;
        synchronized (SYN_CONTROL) {
            n = logForDbDetail(vo, level, dbHelper);
        }
        return n;

    }

    public static int logForDbDetail(String messageId, String messageFrom,
            long startTime, long endTime, String result, String hdlThread,
            String level, String remark) {
        int n = 0;
        try {
            LogCommuVo vo = getLogCommuDetailVo(messageId, messageFrom,
                    startTime, endTime, result, hdlThread, level, remark);

            synchronized (SYN_CONTROL) {
                n = logForDbDetail(vo, level);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return n;

    }

    public static int logForDbDetail(String messageId, String messageFrom,
            long startTime, long endTime, String result, String hdlThread,
            String level, String remark, DbHelper dbHelper) {
        int n = 0;
        try {
            LogCommuVo vo = getLogCommuDetailVo(messageId, messageFrom,
                    startTime, endTime, result, hdlThread, level, remark);

            synchronized (SYN_CONTROL) {
                n = logForDbDetail(vo, level, dbHelper);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return n;

    }

    public static int logForDbDetailByVo(LogCommuVo vo) throws Exception {

        int n = 0;
        synchronized (SYN_CONTROL) {
            n = logForDbDetail(vo, vo.getSysLevel());
        }
        return n;

    }

    public static int logForDbDetailByVo(LogCommuVo vo, DbHelper dbHelper)
            throws Exception {

        int n = 0;
        synchronized (SYN_CONTROL) {
            n = logForDbDetail(vo, vo.getSysLevel(), dbHelper);
        }
        return n;

    }

    public static int logForDbDetail(LogCommuVo vo) throws Exception {
        return insertDetail(vo);

    }

    public static int logForDbDetail(LogCommuVo vo, DbHelper dbHelper)
            throws Exception {
        return insertDetail(vo, dbHelper);

    }

    public static String getRemark(LogCommuVo vo) {
        if (vo.getRemark() == null || vo.getRemark().length() == 0) {
            return "";
        }
        return vo.getRemark();
    }

    public static int insertDetail(LogCommuVo vo) throws Exception {

        DbHelper dbHelper = null;
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_log_commu(id,message_id,message_name,message_from,start_time,"
                + "end_time,use_time,result,hdl_thread,sys_level,remark,insert_date) "
                + " values("+FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_log_commu.nextval,?,?,?,?,?,?,?,?,?,?,sysdate) ";

        int result = 0;
        Object[] values = {vo.getMessageId(), vo.getMessageName(),
            vo.getMessageFrom(), vo.getStartTime(), vo.getEndTime(),
            NumberUtil.getIntegerValue(vo.getUseTime(), 0), vo.getResult(),
            vo.getHdlThread(), vo.getSysLevel(), getRemark(vo)};

        try {

            dbHelper = new DbHelper("LogDbUtil",
                    FrameDBConstant.OL_DBCPHELPER.getConnection());
            if (!dbHelper.isAvailableForConn()) {
                return -1;
            }
            result = dbHelper.executeUpdate(sql, values);

        } catch (Exception e) {
            PubUtil.handleException(e, logger);
        } finally {
            PubUtil.finalProcess(dbHelper);
        }
        return result;
    }

    public static int insertDetail(LogCommuVo vo, DbHelper dbHelper)
            throws Exception {
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_log_commu(id,message_id,message_name,message_from,start_time,"
                + "end_time,use_time,result,hdl_thread,sys_level,remark,insert_date) "
                + " values("+FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_log_commu.nextval,?,?,?,?,?,?,?,?,?,?,sysdate) ";
        int result = 0;
        Object[] values = {vo.getMessageId(), vo.getMessageName(),
            vo.getMessageFrom(), vo.getStartTime(), vo.getEndTime(),
            NumberUtil.getIntegerValue(vo.getUseTime(), 0), vo.getResult(),
            vo.getHdlThread(), vo.getSysLevel(), getRemark(vo)};

        if (!dbHelper.isAvailableForConn()) {
            return -1;
        }
        result = dbHelper.executeUpdate(sql, values);

        return result;
    }

    public static LogCommuVo getLogCommuDetailVo(String messageId,
            String messageFrom, long startTime, long endTime, String result,
            String hdlThread, String level) {
        LogCommuVo vo = new LogCommuVo();

        String messageName = getMessageName(messageId);
        vo.setMessageId(messageId);
        vo.setMessageName(messageName);
        vo.setMessageFrom(messageFrom);
        vo.setHdlThread(hdlThread);

        vo.setStartTime(DateHelper.datetimeToString(new Date(startTime)));
        vo.setEndTime(DateHelper.datetimeToString(new Date(endTime)));
        vo.setUseTime(getUseTime(startTime, endTime));

        vo.setResult(result);

        vo.setSysLevel(level);
        return vo;

    }

    public static LogCommuVo getLogCommuDetailVo(String messageId,
            String messageFrom, long startTime, long endTime, String result,
            String hdlThread, String level, String remark) {
        LogCommuVo vo = new LogCommuVo();

        String messageName = getMessageName(messageId);
        vo.setMessageId(messageId);
        vo.setMessageName(messageName);
        vo.setMessageFrom(messageFrom);
        vo.setHdlThread(hdlThread);

        vo.setStartTime(DateHelper.datetimeToString(new Date(startTime)));
        vo.setEndTime(DateHelper.datetimeToString(new Date(endTime)));
        vo.setUseTime(getUseTime(startTime, endTime));

        vo.setResult(result);
        vo.setRemark(remark);

        vo.setSysLevel(level);
        return vo;

    }

    public static int getReportSize(int reportSize) {
        int n = reportSize / 1024;
        if (n == 0) {
            return 1;
        }
        return n;
    }

    public static String getUseTime(long genStartTime, long genEndTime) {
        long useTime = genEndTime - genStartTime;
        useTime = useTime / 1000;
        return new Long(useTime).toString();
    }

    private static String getMessageName(String messageId) {

        for (int i = 0; i < messageIds.length; i++) {
            if (messageId.equals(messageIds[i])) {
                return messageNames[i];
            }
        }
        return "";
    }

    public static String getLevelText(String level) {

        for (int i = 0; i < levels.length; i++) {
            if (level.equals(levels[i])) {
                return levelTexts[i];
            }
        }
        return "";
    }
}
