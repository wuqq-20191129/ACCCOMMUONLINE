/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.message;

import com.goldsign.commu.app.vo.Message65Vo;
import com.goldsign.commu.frame.message.ConstructMessageBase;
import org.apache.log4j.Logger;

/**
 *
 * @author zhangjh
 */
public class ConstructMessage65 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage65.class
            .getName());

    public ConstructMessage65() {
    }

    public byte[] constructMessage(Message65Vo msg65vo) {
        try {
            // logger.info("ConstructMessage65 Begin.");
            initMessage();
            // 消息类型
            AddStringToMessage(msg65vo.getMessageId(), 2);
            // 消息生成时间
            AddBcdToMessage(msg65vo.getMsgGenTime(), 7);
            // 系统参照号

            AddLongToMessage(msg65vo.getSysRefNo(), 4);
            // 终端编号
            AddStringToMessage(msg65vo.getTerminalNo(), 9);
            // Sam卡逻辑卡号
            AddStringToMessage(msg65vo.getSamLogicalId(), 16);
            // MAC
            AddStringToMessage(msg65vo.getMac(), 16);
            AddStringToMessage(msg65vo.getReturnCode(), 2);
            AddStringToMessage(msg65vo.getErrCode(), 2);

            byte[] msg = trimMessage();
            logger.info("Construct message 65 success! ");
            // logger.info("ConstructMessage17 OK.");
            return msg;
        } catch (Exception e) {
            logger.error("Construct message 65 error! " + e);
            return new byte[0];
        }

    }
}
