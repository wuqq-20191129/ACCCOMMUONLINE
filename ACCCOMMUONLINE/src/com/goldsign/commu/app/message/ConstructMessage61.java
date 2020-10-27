/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.message;

import com.goldsign.commu.app.vo.Message61Vo;
import com.goldsign.commu.frame.message.ConstructMessageBase;
import org.apache.log4j.Logger;

/**
 *
 * @author zhangjh
 */
class ConstructMessage61 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage61.class
            .getName());

    public ConstructMessage61() {
    }

    byte[] constructMessage(Message61Vo msg61vo) {
        try {
            // logger.info("ConstructMessage61 Begin.");
            initMessage();
            AddStringToMessage("61", 2);
            AddBcdToMessage(msg61vo.getMsgGenTime(), 7);
            AddLongToMessage(msg61vo.getSysRefNo(), 4);
            AddStringToMessage(msg61vo.getTerminalNo(), 9);
            AddStringToMessage(msg61vo.getSamLogicalId(), 16);
            // 
            AddLongToMessage(msg61vo.getTransationSeq(), 4);
            AddStringToMessage(msg61vo.getTkLogicNo(), 20);
            AddStringToMessage(msg61vo.getMac2(), 8);
            // Add penalty_reason
            AddBcdToMessage(msg61vo.getSysdate(), 7);
            AddStringToMessage(msg61vo.getReturnCode(), 2);
            AddStringToMessage(msg61vo.getErrCode(), 2);

            byte[] msg = trimMessage();
            logger.info("Construct message 61 success! ");
            // logger.info("ConstructMessage17 OK.");
            return msg;
        } catch (Exception e) {
            logger.error("Construct message 61 error! " + e);
            return new byte[0];
        }
    }
}
