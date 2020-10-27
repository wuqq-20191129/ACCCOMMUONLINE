/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.manager;

import java.util.Date;
import java.util.logging.Level;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.message.ConstructMessage65;
import com.goldsign.commu.app.message.Message55;
import com.goldsign.commu.app.vo.Message55Vo;
import com.goldsign.commu.app.vo.Message65Vo;
import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.LogUtil;
import com.goldsign.commu.frame.vo.BridgeBetweenConnectionAndMessage;
import com.goldsign.commu.frame.vo.MessageQueue;
import com.goldsign.lib.db.util.DbHelper;

/**
 *
 * @author zhangjh
 */
public class ChgActProccessor {

    private static Logger logger = Logger.getLogger(ChgActProccessor.class
            .getName());
    private static final ChgActProccessor PROCCESSOR = new ChgActProccessor();
    /**
     * 密钥信息
     */
    private static final String KEY_MSG = "13101";
    /**
     * 发送给加密机的命令
     */
    private static final String SEND_COMMOND = "59";
    /**
     * 卡片秘钥的版本号
     */
    private static final String VERSION = "0";
    /**
     * 传给加密的随机数不足16位，需要在后面补0
     */
    private static final String ZERO_STR = "00000000";
    /**
     * 算法选择
     */
    private static final String OPTION = "1";
    /**
     * 收到的加密机命令
     */
    private static final String RECV_COMMOND = "5A";

    private ChgActProccessor() {
    }

    public static ChgActProccessor getInstance() {
        return PROCCESSOR;
    }


    public void dealException(Message55 mss, Exception e, String messageSequ,
            DbHelper dbHelper, BridgeBetweenConnectionAndMessage bridge) {
        String errCode = mss.doException(e);
        Message65Vo msg65Vo = buildMsg(errCode, "01", "00000000", null);
        writeBackMsg(msg65Vo, messageSequ, dbHelper, bridge);
    }

    public void writeBackMsg(Message65Vo msg65Vo, String messageSequ,
            DbHelper dbHelper, BridgeBetweenConnectionAndMessage bridge) {
        // 返回消息
        MessageQueue backMesg = getMessageQueue(msg65Vo, messageSequ);
//        try {
//            LogUtil.writeRecvSendLog(null, "localhost", "1",
//                    msg65Vo.getMessageId(), messageSequ, backMesg.getMessage(),
//                    "0", dbHelper);
//
//        } catch (Exception ex) {
//            java.util.logging.Logger.getLogger(Message55.class.getName()).log(
//                    Level.SEVERE, null, ex);
//        }
        sendMsgToConnectionQueue(backMesg, bridge);
    }

    //modify 20171116
    public static Message65Vo buildMsg(String errCode, String returnCode,
            String mac, Message55Vo msg55Vo) {
        Message65Vo msgVo = new Message65Vo();
        msgVo.setErrCode(errCode);
        msgVo.setReturnCode(returnCode);
        // MAC
        msgVo.setMac(mac);

        // 消息类型
        msgVo.setMessageId("65");
        // 消息生成时间
        msgVo.setMsgGenTime(DateHelper.dateToString(new Date()));
        if (null != msg55Vo) {
            msgVo.setWaterNo(msg55Vo.getWaterNo());
            // 系统参照号
            msgVo.setSysRefNo(msg55Vo.getWaterNo());
            // 终端编号
            msgVo.setTerminalNo(msg55Vo.getTerminalNo());
            // Sam卡逻辑卡号
            msgVo.setSamLogicalId(msg55Vo.getSamLogicalId());
        }
        return msgVo;
    }

    private MessageQueue getMessageQueue(Message65Vo msg65vo, String messageFrom) {
        byte[] msg65 = new ConstructMessage65().constructMessage(msg65vo);
        MessageQueue messageQueue = new MessageQueue();
        messageQueue.setMessageTime(new Date(System.currentTimeMillis()));
        messageQueue.setIpAddress(messageFrom);
        messageQueue.setMessage(msg65);
        messageQueue.setIsParaInformMsg("0");
        messageQueue.setParaInformWaterNo(0);
        messageQueue.setLineId("");
        messageQueue.setStationId("");
        messageQueue.setMessageSequ(messageFrom);
        return messageQueue;
    }

    private void sendMsgToConnectionQueue(MessageQueue mq,
            BridgeBetweenConnectionAndMessage bridge) {
        CommuConnection con = bridge.getConnection();
        con.setMessageInConnectionQueue(mq);
    }

}
