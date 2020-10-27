/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.message;

import com.goldsign.commu.app.vo.QrCodeVo;
import com.goldsign.commu.frame.message.ConstructMessageBase;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * 二维码支付执行结果请求响应（67 ）
 * @author lindq
 */
class ConstructMessage67 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage67.class
            .getName());

    public ConstructMessage67() {
    }

    byte[] constructMessage(QrCodeVo qrCodeVo) {
        qrCodeVo.setMessageId("67");
        try {
            initMessage();
            AddStringToMessage(qrCodeVo.getMessageId(), 2);
            AddBcdToMessage(qrCodeVo.getMsgGenTime(), 7);
            AddStringToMessage(qrCodeVo.getTerminalNo(), 9);//终端编号
            AddStringToMessage(qrCodeVo.getSamLogicalId(), 16);
            
            AddLongToMessage(qrCodeVo.getTerminaSeq(), 4);//终端处理流水号
            AddLongToMessage(qrCodeVo.getAccSeq(), 4);//中心处理流水号
            AddStringToMessage(qrCodeVo.getReturnCode(), 2);//响应码

            byte[] msg = trimMessage();
//            logger.info("Construct message 67 success! ");
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("67["+ Arrays.toString(msg)+"]");
            return msg;
        } catch (Exception e) {
            logger.error("Construct message 67 error! " + e);
            return new byte[0];
        }
    }
}
