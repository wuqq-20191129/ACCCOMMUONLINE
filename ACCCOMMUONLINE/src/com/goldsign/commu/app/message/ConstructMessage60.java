/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.message;

import com.goldsign.commu.app.vo.Message60Vo;
import com.goldsign.commu.frame.message.ConstructMessageBase;
import org.apache.log4j.Logger;

/**
 *
 * @author zhangjh
 */
public class ConstructMessage60 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage60.class
            .getName());

    public ConstructMessage60() {
    }

    byte[] constructMessage(Message60Vo msg60Vo) {
        try {
            // logger.info("ConstructMessage61 Begin.");
            initMessage();
            AddStringToMessage("60", 2);
            AddBcdToMessage(msg60Vo.getMsgGenTime(), 7);
            AddStringToMessage(msg60Vo.getTerminalNo(), 9);
            AddStringToMessage(msg60Vo.getSamLogicalId(), 16);
            AddStringToMessage(msg60Vo.getReturnCode(), 2);
            AddStringToMessage(msg60Vo.getErrCode(), 2);
            byte[] msg = trimMessage();
            logger.info("Construct message 60 success! ");
            // logger.info("ConstructMessage17 OK.");
            return msg;
        } catch (Exception e) {
            logger.error("Construct message 60 error! " + e);
            return new byte[0];
        }
    }
}
