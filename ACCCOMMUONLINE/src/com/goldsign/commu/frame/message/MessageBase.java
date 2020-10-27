/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.message;

import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.vo.BridgeBetweenConnectionAndMessage;
import com.goldsign.lib.db.util.DbHelper;
import com.goldsign.lib.db.util.DbcpHelper;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author hejj
 */
public abstract class MessageBase {

    private static Logger logger = Logger
            .getLogger(MessageBase.class.getName());
    protected String messageFrom;
    protected String messageSequ;
    protected String thisClassName = this.getClass().getName();
    protected byte[] data;
    protected DbHelper STDbHelper;
    protected DbHelper OLDbHelper;
    protected long hdlStartTime; // 处理的起始时间
    protected long hdlEndTime;// 处理的结束时间
    protected String threadNum;// 处理线程号

    protected String level = FrameLogConstant.LOG_LEVEL_INFO;// 日志级别
    protected String remark = "";// 备注
    protected BridgeBetweenConnectionAndMessage bridge;

    public void init(String ip, String sequ, byte[] b, DbcpHelper dbcpST,
            DbcpHelper dbcpOL, String threadNum,
            BridgeBetweenConnectionAndMessage bridge, String messageId)
            throws Exception {
        messageFrom = ip;
        messageSequ = sequ;
        data = b;
        thisClassName = thisClassName.substring(
                thisClassName.lastIndexOf(".") + 1, thisClassName.length());
        this.threadNum = threadNum;
        this.bridge = bridge;
        this.bridge.setMessageProcessor(this);
        this.bridge.setMsgType(messageId);
        try {
            if (dbcpST != null) {
                this.STDbHelper = new DbHelper(thisClassName,
                        dbcpST.getConnection());
            }
            if (dbcpOL != null) {
                this.OLDbHelper = new DbHelper(thisClassName,
                        dbcpOL.getConnection());
            }
        } catch (SQLException e) {
            throw e;
        }

        // logger.info(thisClassName+":  "+messageSequ+"  init ended!");
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public abstract void run() throws Exception;

    public void release() {
        try {
            if (STDbHelper != null) {
                STDbHelper.closeConnection();
            }
            if (OLDbHelper != null) {
                OLDbHelper.closeConnection();
            }
        } catch (Exception e) {
        }
    }

    public void releaseForMonitor() {
        try {
            this.releaseConnection(STDbHelper, "STDbHelper");
            this.releaseConnection(OLDbHelper, "OLDbHelper");
        } catch (Exception e) {
            logger.error("释放数据库连接失败");
        }
    }

    private boolean isStatusOk(DbHelper dbHelper) {
        if (dbHelper == null) {
            return true;
        }
        boolean isClosed;
        try {
            isClosed = dbHelper.isConClosed();
        } catch (Exception e) {
            return true;
        }
        return isClosed;

    }

    public boolean checkClose() {
        boolean isCloseAll;
        boolean isCloseForFirst;
        boolean isCloseForTwo;
        isCloseForFirst = this.isStatusOk(this.STDbHelper);
        isCloseForTwo = this.isStatusOk(this.OLDbHelper);
        isCloseAll = isCloseForFirst && isCloseForTwo;
        return isCloseAll;

    }

    private void releaseConnection(DbHelper dbHelper, String key)
            throws Exception {
        if (dbHelper != null) {
            /*
			 * if (!dbHelper.getAutoCommit()) { logger.error(key +
			 * "连接的事务提交方式为手工,将回滚事务及恢复自动方式."); dbHelper.rollbackTran();
			 * dbHelper.setAutoCommit(true); }
             */
            dbHelper.closeConnectionForException();
            logger.error(key + "关闭连接，释放资源");
        }
    }

    // when use in.read() get an int(byte) for example 152(0x98),run this method
    // to get "98";
    public String byte1ToBcd2(int i) {
        return (new Integer(i / 16)).toString()
                + (new Integer(i % 16)).toString();
    }

    // when transform one byte for example (byte)0x98,run this method to get
    // "98";
    public String byte1ToBcd2(byte b) {
        int i;
        if (b < 0) {
            i = 256 + b;
        } else {
            i = b;
        }
        return (new Integer(i / 16)).toString()
                + (new Integer(i % 16)).toString();
    }

    public String getBcdString(int offset, int length) throws CommuException {
        StringBuilder sb = new StringBuilder();
        try {
            for (int i = 0; i < length; i++) {
                sb.append(byte1ToBcd2(data[offset + i]));
            }
        } catch (Exception e) {
            throw new CommuException(" " + e);
        }
        return sb.toString();
    }

    public char byteToChar(byte b) {
        return (char) b;
    }

    public String getCharString(int offset, int length) throws CommuException {
        StringBuilder sb = new StringBuilder();
        try {
            for (int i = 0; i < length; i++) {
                sb.append(byteToChar(data[offset + i]));
            }
        } catch (Exception e) {
            throw new CommuException(" " + e);
        }
        return sb.toString();
    }

    // when transform one byte for example (byte)0x98,run this method to get
    // 104;
    public int byteToInt(byte b) {
        int i;
        if (b < 0) {
            i = 256 + b;
        } else {
            i = b;
        }
        return i;
    }

    public int getInt(int offset) {
        return byteToInt(data[offset]);
    }

    // when transform one short(two bytes) for example 0x12(low),0x34(high),run
    // this method to get 13330
    public int getShort(int offset) {
        int low = byteToInt(data[offset]);
        int high = byteToInt(data[offset + 1]);
        return high * 16 * 16 + low;
    }

    // when transform one long(two shorts) for example 0x12,0x34,0x56,0x78
    public int getLong(int offset) {
        int low = getShort(offset);
        int high = getShort(offset + 2);
        return high * 16 * 16 * 16 * 16 + low;
    }

    public long getLong2(int offset) {
        long number1 = byteToInt(data[offset]);
        int intValIndex2 = byteToInt(data[offset + 1]);
        long number2 = (intValIndex2 / 16) * 16 * 16 * 16 + (intValIndex2 % 16)
                * 16 * 16;
        int intValIndex3 = byteToInt(data[offset + 2]);
        long number3 = (intValIndex3 / 16) * 16 * 16 * 16 * 16 * 16
                + (intValIndex3 % 16) * 16 * 16 * 16 * 16;
        int intValIndex4 = byteToInt(data[offset + 3]);
        long number4 = (intValIndex4 / 16) * 16 * 16 * 16 * 16 * 16 * 16 * 16
                + (intValIndex4 % 16) * 16 * 16 * 16 * 16 * 16 * 16;

        return number1 + number2 + number3 + number4;
    }

    public java.util.Date dateStrToUtilDate(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        ParsePosition pos = new ParsePosition(0);
        return format.parse(dateStr, pos);
    }

    public java.sql.Timestamp dateStrToSqlTimestamp(String dateStr) {
        java.util.Date utilDate = dateStrToUtilDate(dateStr);
        return new java.sql.Timestamp(utilDate.getTime());
    }

    public DbHelper getSTDbHelper() {
        return STDbHelper;
    }

    public DbHelper getOLDbHelper() {
        return this.OLDbHelper;
    }
}
