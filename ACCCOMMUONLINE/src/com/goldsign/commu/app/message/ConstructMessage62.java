/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.message;

import com.goldsign.commu.app.vo.Message62Vo;
import com.goldsign.commu.frame.message.ConstructMessageBase;
import org.apache.log4j.Logger;

/**
 *
 * @author zhangjh
 */
class ConstructMessage62 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage62.class
            .getName());

    public ConstructMessage62() {
    }

    byte[] constructMessage(Message62Vo msg62Vo) {
        try {
            // logger.info("ConstructMessage61 Begin.");
            initMessage();
            AddStringToMessage("62", 2);
            AddBcdToMessage(msg62Vo.getMsgGenTime(), 7);
            AddLongToMessage(msg62Vo.getSysRefNo(), 4);
            AddStringToMessage(msg62Vo.getTerminalNo(), 9);
            AddStringToMessage(msg62Vo.getSamLogicalId(), 16);
            // 
            AddLongToMessage(msg62Vo.getTransationSeq(), 4);
            AddStringToMessage(msg62Vo.getTkLogicNo(), 20);
            // Add penalty_reason
            AddStringToMessage(msg62Vo.getReturnCode(), 2);
            AddStringToMessage(msg62Vo.getErrCode(), 2);

            byte[] msg = trimMessage();
            logger.info("成功构造62消息! ");
            // logger.info("ConstructMessage17 OK.");
            return msg;
        } catch (Exception e) {
//			logger.error("Construct message 62 error! " + e);
            logger.info("构造62消息失败! ", e);
            return new byte[0];
        }
    }
}
