/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.frame.message;

import com.goldsign.commu.frame.constant.FrameLogConstant;
import com.goldsign.commu.frame.dao.ComMessageQueueDao;
import com.goldsign.commu.frame.exception.MessageException;
import com.goldsign.commu.frame.util.LogDbUtil;
import com.goldsign.commu.frame.vo.MessageQueue;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 *
 * @author hejj
 */
public class ConstructMessageBase {

    /**
    * 日志记录使用
    */
    private long hdlStartTime; // 处理的起始时间
    private long hdlEndTime;// 处理的结束时间
    private final int maxLength = 65536;
    private final String NotParaInformMsg = "0";
    private byte[] message;
    private int pointer;
    protected String queueName;
    private static Logger logger = Logger.getLogger(ConstructMessageBase.class
            .getName());

    protected ConstructMessageBase() {
    }

    public void pushQueue(String ip, byte[] message, String isParaInformMsg,
            int paraInformWaterNo, String lineId, String stationId) {
        try {
            if (message == null) {
                logger.error("发送消息到IP:" + ip + "失败, 消息内容为空 ");
            } else {
                MessageQueue messageQueue = new MessageQueue();
                messageQueue.setMessageTime(new Date(System.currentTimeMillis()));
                messageQueue.setIpAddress(ip);
                messageQueue.setMessage(message);
                messageQueue.setIsParaInformMsg(isParaInformMsg);
                messageQueue.setParaInformWaterNo(paraInformWaterNo);
                messageQueue.setLineId(lineId);
                messageQueue.setStationId(stationId);

                try {
			this.hdlStartTime = System.currentTimeMillis();

			ComMessageQueueDao dao = new ComMessageQueueDao();
			dao.pushQueue(messageQueue);
			message = null;
			this.hdlEndTime = System.currentTimeMillis();
			// 记录日志
			LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_PUSH_QUEUE, "",
					this.hdlStartTime, this.hdlEndTime,
					FrameLogConstant.RESULT_HDL_SUCESS, Thread.currentThread()
							.getName(), FrameLogConstant.LOG_LEVEL_INFO,
					"消息入库待发送");

		} catch (Exception e) {
			logger.info("onMessage error! ", e);

			this.hdlEndTime = System.currentTimeMillis();
			// 记录日志
			LogDbUtil.logForDbDetail(FrameLogConstant.MESSAGE_ID_PUSH_QUEUE, "",
					this.hdlStartTime, this.hdlEndTime,
					FrameLogConstant.RESULT_HDL_FAIL, Thread.currentThread()
							.getName(), FrameLogConstant.LOG_LEVEL_ERROR,
					"onMessage error! ");
		}

                logger.info("成功发送消息到IP:" + ip + ".");
            }
        } catch (Exception e) {
            logger.error("发送消息出错!", e);
        }
    }

    public void sendToJms(String ip, byte[] message) {
        pushQueue(ip, message, NotParaInformMsg, 0, "", "");
    }

    public void sendToJms(String ip, byte[] message, String lineId,
            String stationId) {
        pushQueue(ip, message, NotParaInformMsg, 0, lineId, stationId);
    }

    public void sendToJms(String ip, byte[] message, String flag) {
        pushQueue(ip, message, flag, 0, "", "");
    }

    public void sendToJms(Vector ipV, byte[] message) {
        String ip;
        Iterator it = ipV.iterator();
        while (it.hasNext()) {
            ip = (String) it.next();
            sendToJms(ip, message);
        }
    }

    /**
     * len is the bytes length after transform
     * 不足20位时左对齐，右补空格。默认值：全为0
     */
    protected void AddStringToMessage(String str, int len) {
        if (str.length() >= len) {
            str = str.substring(0, len);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            for (int i = 0; i < len - str.length(); i++) {
                sb.append(" ");
            }
            str = sb.toString();
        }
        
        byte[] b = str.getBytes();
        AddByteArrayToMessage(b, len);
    }

    /**
     * len is the bytes length after transform
     */
    protected void AddLongToMessage(long input, int len)
            throws MessageException {
        String des = Integer.toHexString((int) input);
        if (des.length() >= 8) {
            //modify by zhongzq 20180808 截取应该为8位 数值过大会引起异常
//            des = des.substring(0, 7);
            des = des.substring(0, 8);
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len * 2 - des.length(); i++) {
                sb.append("0");
            }
            sb.append(des);
            des = sb.toString();
        }
        byte[] b = longStringToByteArray(des);
        if (b == null) {
            throw new MessageException("Long transform error!");
        }
        AddByteArrayToMessage(b, len);
    }

    /**
     * len is the bytes length after transform
     */
    protected void AddBcdToMessage(String str, int len) throws MessageException {
        byte[] b = bcdStringToByteArray(str);
        if (b == null) {
            throw new MessageException("BCD transform error!");
        }
        AddByteArrayToMessage(b, len);
    }

    /**
     * len is the bytes length after transform
     */
    protected void AddIntToMessage(int i, int len) throws MessageException {
        if (i > 256) {
            throw new MessageException("Message repeat count > 256! ");
        }
        byte[] b = {(byte) i};
        AddByteArrayToMessage(b, 1);
    }

    private void AddByteArrayToMessage(byte[] b, int len) {
        if (len > b.length) {
            System.arraycopy(b, 0, message, pointer, b.length);
            pointer = pointer + b.length;
            // char space = ' ';
            byte[] space = {0x20};
            for (int i = (len - b.length); i > 0; i--) {
                System.arraycopy(space, 0, message, pointer, 1);
                pointer = pointer + 1;
            }
        } else {
            System.arraycopy(b, 0, message, pointer, len);
            pointer = pointer + len;
        }
    }

    // delete unused byte[] in the latter
    protected byte[] trimMessage() {
        byte[] result = new byte[pointer];
        System.arraycopy(message, 0, result, 0, pointer);
        return result;
    }

    protected void initMessage() {
        this.message = new byte[maxLength];
        this.pointer = 0;
    }

    // when transform 2 decimal number for example 98,run this method to get
    // 0x98
    private byte bcd2ToByte1(int i) {
        return (byte) (i / 10 * 16 + i % 10);
    }

    // run this method example : transform "123456789" to
    // {0x01,0x23,0x45,0x67,0x89}
    private byte[] bcdStringToByteArray(String str) {
        try {
            int len = str.length();
            if (str.length() == 0) {
                return null;
            }
            if (len % 2 == 1) {
                str = "0" + str;
                len = len + 1;
            }
            if (len / 2 > maxLength) {
                throw new MessageException("Transform string to BCD length > "
                        + maxLength);
            }
            byte[] tmp = new byte[maxLength];
            int p = 0;
            for (int i = 0; i < len; i = i + 2) {
                int value = Integer.parseInt(str.substring(i, i + 2));
                byte[] b = {bcd2ToByte1(value)};
                System.arraycopy(b, 0, tmp, p, 1);
                p = p + 1;
            }

            byte[] bb = new byte[p];
            System.arraycopy(tmp, 0, bb, 0, p);
            return bb;
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }

    /*
	 * for test public static void main(String[] args){ ConstructMessageBase mc
	 * = new ConstructMessageBase(); byte[] b =
	 * mc.bcdStringToByteArray("0123456789"); for(int i=0;i<b.length;i++){
	 * logger.error("b:"+b[i]); } }
     */
    private byte[] changeLongToByteArr(long i) {
        // 余数
        int remainder = (int) i;
        byte[] result = {0, 0, 0, 0};
        // 商

        byte quotient = 0;
        //
        if (remainder > 16777216) {
            quotient = (byte) (i / 16777216);
            remainder = (int) (i % 16777216);
            result[3] = quotient;
        }
        if (remainder > 65536) {
            quotient = (byte) (remainder / 16777216);
            remainder = (int) (remainder % 16777216);
            result[2] = quotient;
        }
        if (remainder > 256) {
            quotient = (byte) (remainder / 256);
            remainder = (int) (remainder % 256);
            result[1] = quotient;
        }
        if (remainder > 0) {
            quotient = (byte) (remainder / 256);
            result[0] = quotient;
        }

        return result;
    }

    private byte[] longStringToByteArray(String des) {
        byte[] rst = new byte[4];
        rst[0] = (byte) (Integer.valueOf(des.substring(6), 16).intValue());
        rst[1] = (byte) (Integer.valueOf(des.substring(4, 6), 16).intValue());
        rst[2] = (byte) (Integer.valueOf(des.substring(2, 4), 16).intValue());
        rst[3] = (byte) (Integer.valueOf(des.substring(0, 2), 16).intValue());
        return rst;
    }
}
