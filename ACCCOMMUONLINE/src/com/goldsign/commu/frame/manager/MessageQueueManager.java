/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.manager;

import com.goldsign.commu.frame.vo.MessageQueue;
import com.goldsign.commu.frame.vo.SynchronizedControl;
import java.util.Vector;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 *
 * @author hejj
 */
public class MessageQueueManager {

    public static HashMap MESSAG_QUEUES = new HashMap();
    public static Vector MESSAGE_QUEUES_DOING = new Vector();
    public final static SynchronizedControl SYN_CONTROL_DOING = new SynchronizedControl();
    public final static SynchronizedControl SYN_CONTROL = new SynchronizedControl();
    private static Logger logger = Logger.getLogger(MessageQueueManager.class
            .getName());

    public MessageQueueManager() {
    }

    public static void main(String[] args) {
        MessageQueueManager messageQueueManager1 = new MessageQueueManager();
    }

    public static MessageQueue getMessageQueue(String id) {
        MessageQueue mq = null;
        Vector mqs = null;
        synchronized (SYN_CONTROL) {
            if (MESSAG_QUEUES.isEmpty()) {
                return null;
            }
            if (!MESSAG_QUEUES.containsKey(id)) {
                return null;
            }
            mqs = (Vector) MESSAG_QUEUES.get(id);
            if (mqs.isEmpty()) {
                return null;
            }
            mq = (MessageQueue) mqs.get(0);
            mqs.remove(0);
            // 将要处理的消息放入正在处理的消息队列中
            MessageQueueManager.setMessageQueueDoing(mq);
            logger.info(id + " - 消息缓存队列剩余" + mqs.size() + "个消息未处理");
            return mq;
        }

    }

    public static void removeMessageQueueFromBuffer(String ip,
            String messageType) {
        synchronized (MESSAG_QUEUES) {
            if (MESSAG_QUEUES == null || MESSAG_QUEUES.isEmpty()) {
                return;
            }
            if (!MESSAG_QUEUES.containsKey(ip)) {
                return;
            }
            Vector msgs = (Vector) MESSAG_QUEUES.get(ip);
            MessageQueue msg = null;
            Vector buffer = new Vector();

            for (int i = 0; i < msgs.size(); i++) {
                msg = (MessageQueue) msgs.get(i);
                if (!isMessageForType(msg, messageType)) {// 保留非清除消息

                    buffer.add(msg);
                } else {
                    logger.info("消息缓存清除消息" + msg.getMessageId());
                }

            }
            msgs.clear();
            if (!buffer.isEmpty()) {// 将保留消息放回消息队列

                msgs.addAll(buffer);
            }

        }

    }

    private static boolean isMessageForType(MessageQueue msg, String messageType) {
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

    public static void setMessageQueueDoing(MessageQueue mq) {
        synchronized (SYN_CONTROL_DOING) {
            MESSAGE_QUEUES_DOING.add(mq);
        }

    }

    public static void removeMessageFromQueueDoing(MessageQueue mq) {
        synchronized (SYN_CONTROL_DOING) {
            if (MESSAGE_QUEUES_DOING.contains(mq)) {
                MESSAGE_QUEUES_DOING.remove(mq);
            }
        }

    }

    public static boolean isInMessageQueueDoing(MessageQueue mq) {

        if (MESSAGE_QUEUES_DOING == null || MESSAGE_QUEUES_DOING.isEmpty()) {
            return false;
        }
        MessageQueue vo;
        for (int i = 0; i < MESSAGE_QUEUES_DOING.size(); i++) {
            vo = (MessageQueue) MESSAGE_QUEUES_DOING.get(i);
            if (vo.getMessageId() == mq.getMessageId()) {
                return true;
            }

        }
        return false;
    }

    public static Vector removeMessageDoing(Vector mqs) {
        synchronized (SYN_CONTROL_DOING) {
            Vector v = new Vector();
            MessageQueue mq;
            if (mqs == null || mqs.isEmpty()) {
                return v;
            }

            for (int i = 0; i < mqs.size(); i++) {
                mq = (MessageQueue) mqs.get(i);
                if (!isInMessageQueueDoing(mq)) {
                    v.add(mq);
                }

            }
            return v;
        }
    }

    public static void setMessageQueue(Vector mqs) {
        Vector msgsLeft;
        synchronized (SYN_CONTROL) {
            if (mqs.isEmpty()) {
                return;
            }

            MessageQueue mq = null;
            String ip = null;
            Vector v = null;
            // 清除正在处理的消息，这些消息不能重新放入消息队列 modify by hejj 2011-06-22
            msgsLeft = MessageQueueManager.removeMessageDoing(mqs);
            // 清除旧的未处理队列

            MESSAG_QUEUES.clear();
            for (int i = 0; i < msgsLeft.size(); i++) {
                mq = (MessageQueue) msgsLeft.get(i);
                ip = mq.getIpAddress();
                if (ip == null || ip.length() == 0) {
                    continue;
                }
                if (!MESSAG_QUEUES.containsKey(ip)) {
                    v = new Vector();
                    v.add(mq);
                    MESSAG_QUEUES.put(ip, v);
                } else {
                    v = (Vector) MESSAG_QUEUES.get(ip);
                    v.add(mq);
                }
                logger.info(ip + " - 消息缓存队列现有" + v.size() + "个消息未处理");

            }

        }

    }

}
