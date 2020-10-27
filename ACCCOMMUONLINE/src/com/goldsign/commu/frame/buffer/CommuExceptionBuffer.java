/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.buffer;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.vo.CommuHandledMessage;

/**
 *
 * @author hejj
 */
public class CommuExceptionBuffer {

    private static Vector jmsUnHandleMsgs = new Vector(3000, 100);
    private static Vector sqlUnHandleMsgs = new Vector(3000, 100);
    private static final String retCode = "0100";
    private static Logger logger = Logger.getLogger(CommuExceptionBuffer.class);

    public CommuExceptionBuffer() {
    }

    public static void setJmsUnHandleMsgs(String id, byte[] data) {

        logger.error("JMS异常消息.消息发送方:" + id + " 消息数据长度:" + data.length);

        if (data == null || data.length == 0) {
            return;
        }
        Vector readResult = new Vector();
        readResult.add(retCode);
        readResult.add(data);
        CommuHandledMessage chm = new CommuHandledMessage(id, readResult);
        synchronized (jmsUnHandleMsgs) {
            jmsUnHandleMsgs.add(chm);
            logger.error("JMS异常消息缓存目前有记录:" + jmsUnHandleMsgs.size() + "条");
        }
    }

    public static Vector getJmsUnHandleMsgs() {
        Vector ret = new Vector(3000, 100);
        synchronized (jmsUnHandleMsgs) {
            ret.addAll(jmsUnHandleMsgs);
            jmsUnHandleMsgs.clear();
            return ret;
        }
    }

    public static void setSqlUnHandleMsgs(String id, byte[] data) {

        logger.error("sql异常消息.消息发送方:" + id + " 消息数据长度:" + data.length);

        if (data == null || data.length == 0) {
            return;
        }
        Vector readResult = new Vector();
        readResult.add(retCode);
        readResult.add(data);
        CommuHandledMessage chm = new CommuHandledMessage(id, readResult);
        synchronized (sqlUnHandleMsgs) {
            sqlUnHandleMsgs.add(chm);
            logger.error("sql异常消息缓存目前有记录:" + sqlUnHandleMsgs.size() + "条");
        }
    }

    public static Vector getSqlUnHandleMsgs() {
        Vector ret = new Vector(3000, 100);
        synchronized (sqlUnHandleMsgs) {
            ret.addAll(sqlUnHandleMsgs);
            sqlUnHandleMsgs.clear();
            return ret;
        }
    }

}
