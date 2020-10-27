/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.message;

import com.goldsign.commu.app.vo.Message63Vo;
import com.goldsign.commu.frame.message.ConstructMessageBase;
import org.apache.log4j.Logger;

/**
 *
 * @author zhangjh
 */
class ConstructMessage63 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage63.class
            .getName());

    public ConstructMessage63() {
    }

    byte[] constructMessage(Message63Vo msg63Vo) {
        try {
            // logger.info("ConstructMessage61 Begin.");
            initMessage();
            AddStringToMessage("63", 2);
            AddBcdToMessage(msg63Vo.getMsgGenTime(), 7);
            AddLongToMessage(msg63Vo.getSysRefNo(), 4);
            AddStringToMessage(msg63Vo.getTerminalNo(), 9);
            AddStringToMessage(msg63Vo.getSamLogicalId(), 16);
            // 终端交易序列号���
            AddLongToMessage(msg63Vo.getTransationSeq(), 4);
            AddStringToMessage(msg63Vo.getTkLogicNo(), 20);
            // 系统时间
            AddBcdToMessage(msg63Vo.getSysdate(), 7);
            // Add penalty_reason
            AddStringToMessage(msg63Vo.getReturnCode(), 2);
            AddStringToMessage(msg63Vo.getErrCode(), 2);

            byte[] msg = trimMessage();
            logger.info("Construct message 63 success! ");
            // logger.info("ConstructMessage17 OK.");
            return msg;
        } catch (Exception e) {
            logger.error("Construct message 63 error! " + e);
            return new byte[0];
        }
    }
}
