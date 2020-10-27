/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.dao;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.vo.MessageQueue;
import java.sql.SQLException;
import org.apache.log4j.Logger;

import com.goldsign.lib.db.util.DbHelper;
import java.util.Vector;

;

/**
 *
 * @author hejj
 */
public class ComMessageQueueDao {

    private DbHelper dbHelper;

    private static Logger logger = Logger.getLogger(ComMessageQueueDao.class
            .getName());

    // default,the queue is on the communication database
    public ComMessageQueueDao() {
        // this.dbHelper = ApplicationConstant.COMMU_DBHELPER;
        try {
            this.dbHelper = new DbHelper("ComMessageQueueDao",
                    FrameDBConstant.OL_DBCPHELPER.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ComMessageQueueDao(boolean isUseDefaultDb) {

    }

    public ComMessageQueueDao(DbHelper db) {
        this.dbHelper = db;
    }

    public void pushQueue(MessageQueue message) {
        logger.info("Push message to queue start!");
        boolean result = false;
        String sqlStr = "insert into " + FrameDBConstant.COM_OL_P + "OL_QUE_MESSAGE(message_id, message_time,ip_address,message,process_flag,is_para_inform_msg,para_inform_water_no,line_id,station_id)"
                + " values("+FrameDBConstant.COM_OL_P+"seq_"+FrameDBConstant.TABLE_PREFIX+"ol_que_message.nextval,?,?,?,?,?,?,?,?)";
        Object[] values = new Object[8];
        values[0] = DateHelper.utilDateToSqlTimestamp(message.getMessageTime());
        values[1] = message.getIpAddress();
        logger.info("Message will be send to " + message.getIpAddress());
        values[2] = message.getMessage();
        values[3] = "0";
        values[4] = message.getIsParaInformMsg();
        values[5] = new Integer(message.getParaInformWaterNo());
        values[6] = message.getLineId();
        values[7] = message.getStationId();

        try {
            dbHelper.executeUpdate(sqlStr, values);
            // logger.info("Push message to queue end!");
        } catch (SQLException e) {
            logger.error("Access table " + FrameDBConstant.COM_OL_P + "OL_QUE_MESSAGE error! " + e);
            logger.info("Push message to queue error! " + e);
        } finally {
            try {
                if (this.dbHelper != null) {
                    dbHelper.closeConnection();
                }
            } catch (SQLException e) {

            }
        }

    }

    public void updateFlagQueue(long id) {
        // logger.info("Update message start!");
        boolean result = false;
        String sqlStr = "update " + FrameDBConstant.COM_OL_P + "OL_QUE_MESSAGE set process_flag='1' where message_id=?";
        Object values[] = new Object[1];
        values[0] = new Long(id);
        try {
            dbHelper.executeUpdate(sqlStr, values);
            // logger.info("Update message end!");
        } catch (SQLException e) {
            logger.error("Access table " + FrameDBConstant.COM_OL_P + "OL_QUE_MESSAGE error! " + e);
            logger.info("Update message error! " + e);
        } finally {
            try {
                if (this.dbHelper != null) {
                    dbHelper.closeConnection();
                }
            } catch (SQLException e) {

            }
        }

    }

    public long getMinMessageID(String ip) throws SQLException {
        boolean result = false;
        DbHelper helper = null;
        String sql = "select min(message_id) message_id from " + FrameDBConstant.COM_OL_P + "OL_QUE_MESSAGE where process_flag='0' and ip_address="
                + "'" + ip + "'";
        long messageID = -1;
        try {
            helper = new DbHelper("",
                    FrameDBConstant.ST_DBCPHELPER.getConnection());
            result = helper.getFirstDocument(sql);
            if (result) {
                messageID = helper.getItemLongValue("message_id");
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (helper != null) {
                    helper.closeConnection();
                }
            } catch (SQLException e) {

            }
        }
        return messageID;

    }

    public MessageQueue pullQueue(String ip) {
        // logger.info("Pull message from queue start!");
        MessageQueue mq = null;
        boolean result = false;
        byte[] data = null;
        StringBuffer sb = new StringBuffer();
        long messageID = -1;

        sb.append("select message_id,message_time,line_id,station_id,ip_address,message,process_flag,is_para_inform_msg,para_inform_water_no from " + FrameDBConstant.COM_OL_P + "OL_QUE_MESSAGE");
        sb.append(" where process_flag='0' and ip_address=?");
        // sb.append(" and message_id=(select min(message_id) from " + FrameDBConstant.COM_OL_P + "OL_QUE_MESSAGE");
        // sb.append(" where process_flag='0' and ip_address=?)");
        // Object[] values = {
        // ip, ip};
        Object[] values = {ip};
        try {
            messageID = this.getMinMessageID(ip);
            sb.append(" and message_id=" + messageID);
            result = dbHelper.getFirstDocument(sb.toString(), values);
            if (result) {
                // logger.info("Message : "
                // +dbHelper.getItemLongValue("message_id")
                // +" got from message queue!");
                mq = new MessageQueue();
                mq.setMessageId(dbHelper.getItemLongValue("message_id"));
                mq.setMessageTime(dbHelper.getItemDateTimeValue("message_time"));
                mq.setLineId(dbHelper.getItemValue("line_id"));
                mq.setStationId(dbHelper.getItemValue("station_id"));
                mq.setIpAddress(dbHelper.getItemValue("ip_address"));
                mq.setMessage(dbHelper.getItemBytesValue("message"));
                mq.setProcessFlag(dbHelper.getItemValue("process_flag"));
                mq.setIsParaInformMsg(dbHelper
                        .getItemValue("is_para_inform_msg"));
                mq.setParaInformWaterNo(dbHelper
                        .getItemIntValue("para_inform_water_no"));
            }
            // logger.info("Pull message from queue end!");
        } catch (SQLException e) {
            logger.error("Pull message from queue error! " + e);
        } finally {
            // logger.error("program executt finally statement " );
            try {
                if (this.dbHelper != null) {
                    dbHelper.closeConnection();
                }
            } catch (SQLException e) {

            }
        }

        return mq;
    }

    public Vector pullQueuesForThread() throws Exception {

        MessageQueue mq = null;
        boolean result = false;
        byte[] data = null;
        StringBuffer sb = new StringBuffer();
        Vector mqs = new Vector();

        sb.append("select message_id,message_time,line_id,station_id,ip_address,message,process_flag,is_para_inform_msg,para_inform_water_no from " + FrameDBConstant.COM_OL_P + "OL_QUE_MESSAGE");
        sb.append(" where process_flag='0' order by ip_address,message_id");

        try {
            if (!dbHelper.isAvailableForConn()) {
                return mqs;
            }
            result = dbHelper.getFirstDocument(sb.toString());
            while (result) {
                // logger.info("Message : "
                // +dbHelper.getItemLongValue("message_id")
                // +" got from message queue!");
                mq = new MessageQueue();
                mq.setMessageId(dbHelper.getItemLongValue("message_id"));
                mq.setMessageTime(dbHelper.getItemDateTimeValue("message_time"));
                mq.setLineId(dbHelper.getItemValue("line_id"));
                mq.setStationId(dbHelper.getItemValue("station_id"));
                mq.setIpAddress(dbHelper.getItemValue("ip_address"));
                mq.setMessage(dbHelper.getItemBytesValue("message"));
                mq.setProcessFlag(dbHelper.getItemValue("process_flag"));
                mq.setIsParaInformMsg(dbHelper
                        .getItemValue("is_para_inform_msg"));
                mq.setParaInformWaterNo(dbHelper
                        .getItemIntValue("para_inform_water_no"));
                mqs.add(mq);
                result = dbHelper.getNextDocument();
            }
            // logger.info("test Pull messages from queue ! msqs size="+mqs.size());
            // logger.info("Pull message from queue end!");
        } catch (SQLException e) {
            logger.error("Pull messages from queue error! " + e);
            throw e;
        } catch (Exception e) {
            logger.error("Pull messages from queue error! " + e);
            throw e;
        } finally {
            // logger.error("program executt finally statement " );
            try {
                if (this.dbHelper != null) {
                    dbHelper.closeConnection();
                }
            } catch (SQLException e) {

            }
        }

        return mqs;
    }

    public void removeMsgFromQueue(String ip, String messageType) {
        Vector mqs = this.pullQueues(ip);
        if (mqs.isEmpty()) {
            return;
        }
        MessageQueue mq = null;
        Vector buffer = new Vector();
        for (int i = 0; i < mqs.size(); i++) {
            mq = (MessageQueue) mqs.get(i);
            if (this.isMessageForType(mq, messageType)) {
                buffer.add(mq);
            }
        }
        if (!buffer.isEmpty()) {
            for (int j = 0; j < buffer.size(); j++) {
                mq = (MessageQueue) buffer.get(j);
                logger.info("数据库消息队列清除消息" + mq.getMessageId());
                this.updateFlagQueue(mq);
            }

        }

    }

    public void updateFlagQueue(MessageQueue mq) {
        // logger.info("Update message start!");
        String sqlStr = "update " + FrameDBConstant.COM_OL_P + "OL_QUE_MESSAGE set process_flag='1' where message_id=?";
        Object values[] = new Object[1];
        values[0] = new Long(mq.getMessageId());
        DbHelper helper = null;
        try {
            helper = new DbHelper("ComMessageQueueDAO",
                    FrameDBConstant.ST_DBCPHELPER.getConnection());
            helper.executeUpdate(sqlStr, values);
            // logger.info("Update message end!");
        } catch (SQLException e) {
            logger.error("Access table " + FrameDBConstant.COM_OL_P + "OL_QUE_MESSAGE error! " + e);
            logger.info("Update message error! " + e);
        } finally {
            try {
                if (helper != null) {
                    helper.closeConnection();
                }
            } catch (SQLException e) {

            }
        }

    }

    private boolean isMessageForType(MessageQueue msg, String messageType) {
        if (msg == null) {
            return false;
        }
        byte[] bMsg = msg.getMessage();
        String sMsg = new String(bMsg);
        if (sMsg.startsWith(messageType)) {
            return true;
        }
        return false;
    }

    public Vector pullQueues() {

        MessageQueue mq = null;
        boolean result = false;
        byte[] data = null;
        StringBuffer sb = new StringBuffer();
        Vector mqs = new Vector();

        sb.append("select message_id,message_time,line_id,station_id,ip_address,message,process_flag,is_para_inform_msg,para_inform_water_no from " + FrameDBConstant.COM_OL_P + "OL_QUE_MESSAGE");
        sb.append(" where process_flag='0' order by ip_address,message_id");

        try {

            result = dbHelper.getFirstDocument(sb.toString());
            while (result) {
                // logger.info("Message : "
                // +dbHelper.getItemLongValue("message_id")
                // +" got from message queue!");
                mq = new MessageQueue();
                mq.setMessageId(dbHelper.getItemLongValue("message_id"));
                mq.setMessageTime(dbHelper.getItemDateTimeValue("message_time"));
                mq.setLineId(dbHelper.getItemValue("line_id"));
                mq.setStationId(dbHelper.getItemValue("station_id"));
                mq.setIpAddress(dbHelper.getItemValue("ip_address"));
                mq.setMessage(dbHelper.getItemBytesValue("message"));
                mq.setProcessFlag(dbHelper.getItemValue("process_flag"));
                mq.setIsParaInformMsg(dbHelper
                        .getItemValue("is_para_inform_msg"));
                mq.setParaInformWaterNo(dbHelper
                        .getItemIntValue("para_inform_water_no"));
                mqs.add(mq);
                result = dbHelper.getNextDocument();
            }
            // logger.info("test Pull messages from queue ! msqs size="+mqs.size());
            // logger.info("Pull message from queue end!");
        } catch (SQLException e) {
            logger.error("Pull messages from queue error! " + e);
        } finally {
            // logger.error("program executt finally statement " );
            try {
                if (this.dbHelper != null) {
                    dbHelper.closeConnection();
                }
            } catch (SQLException e) {

            }
        }

        return mqs;
    }

    public Vector pullQueues(String ip) {

        MessageQueue mq = null;
        boolean result = false;
        StringBuffer sb = new StringBuffer();
        Vector mqs = new Vector();
        String[] values = {ip};

        sb.append("select message_id,message_time,line_id,station_id,ip_address,message,process_flag,is_para_inform_msg,para_inform_water_no from " + FrameDBConstant.COM_OL_P + "OL_QUE_MESSAGE");
        sb.append(" where process_flag='0' and ip_address=? order by ip_address,message_id");
        DbHelper helper = null;

        try {
            helper = new DbHelper("ComMessageQueueDAO",
                    FrameDBConstant.ST_DBCPHELPER.getConnection());
            result = helper.getFirstDocument(sb.toString(), values);
            while (result) {
                // logger.info("Message : "
                // +dbHelper.getItemLongValue("message_id")
                // +" got from message queue!");
                mq = new MessageQueue();
                mq.setMessageId(helper.getItemLongValue("message_id"));
                mq.setMessageTime(helper.getItemDateTimeValue("message_time"));
                mq.setLineId(helper.getItemValue("line_id"));
                mq.setStationId(helper.getItemValue("station_id"));
                mq.setIpAddress(helper.getItemValue("ip_address"));
                mq.setMessage(helper.getItemBytesValue("message"));
                mq.setProcessFlag(helper.getItemValue("process_flag"));
                mq.setIsParaInformMsg(helper.getItemValue("is_para_inform_msg"));
                mq.setParaInformWaterNo(helper
                        .getItemIntValue("para_inform_water_no"));
                mqs.add(mq);
                result = helper.getNextDocument();
            }
            // logger.info("test Pull messages from queue ! msqs size="+mqs.size());
            // logger.info("Pull message from queue end!");
        } catch (SQLException e) {
            logger.error("Pull messages from queue error! " + e);
        } finally {
            // logger.error("program executt finally statement " );
            try {
                if (helper != null) {
                    helper.closeConnection();
                }
            } catch (SQLException e) {

            }
        }

        return mqs;
    }

}
