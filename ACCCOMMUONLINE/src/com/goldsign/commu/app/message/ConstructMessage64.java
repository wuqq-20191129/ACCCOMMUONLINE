/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.message;

import com.goldsign.commu.app.vo.Message64Vo;
import com.goldsign.commu.frame.message.ConstructMessageBase;
import org.apache.log4j.Logger;

/**
 *
 * @author zhangjh
 */
class ConstructMessage64 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage64.class
            .getName());

    public ConstructMessage64() {
    }

    byte[] constructMessage(Message64Vo msg64Vo) {
        try {
            // logger.info("ConstructMessage61 Begin.");
            initMessage();
            AddStringToMessage("64", 2);
            AddBcdToMessage(msg64Vo.getMsgGenTime(), 7);
            AddLongToMessage(msg64Vo.getSysRefNo(), 4);
            AddStringToMessage(msg64Vo.getTerminalNo(), 9);
            AddStringToMessage(msg64Vo.getSamLogicalId(), 16);
            // ����
            AddLongToMessage(msg64Vo.getTransationSeq(), 4);
            AddStringToMessage(msg64Vo.getTkLogicNo(), 20);
            // Add penalty_reason
            AddStringToMessage(msg64Vo.getReturnCode(), 2);
            AddStringToMessage(msg64Vo.getErrCode(), 2);

            byte[] msg = trimMessage();
            logger.info("Construct message 64 success! ");
            // logger.info("ConstructMessage17 OK.");
            return msg;
        } catch (Exception e) {
            logger.error("Construct message 64 error! " + e);
            return new byte[0];
        }
    }
}
