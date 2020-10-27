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
 * 二维码订单取消请求响应（48 ）
 * @author lindq
 */
class ConstructMessage48 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage48.class
            .getName());

    public ConstructMessage48() {
    }

    byte[] constructMessage(QrCodeVo qrCodeVo) {
        qrCodeVo.setMessageId("48");
        try {
            initMessage();
            AddStringToMessage(qrCodeVo.getMessageId(), 2);
            AddBcdToMessage(qrCodeVo.getMsgGenTime(), 7);
            AddStringToMessage(qrCodeVo.getTerminalNo(), 9);//终端编号
            AddStringToMessage(qrCodeVo.getSamLogicalId(), 16);
            AddLongToMessage(qrCodeVo.getTerminaSeq(), 4);//终端处理流水号
            AddLongToMessage(qrCodeVo.getAccSeq(), 4);//中心处理流水号
            
            AddStringToMessage(qrCodeVo.getTkStatus(), 2);//订单状态
            AddStringToMessage(qrCodeVo.getOrderNo(), 14);//订单号
            AddStringToMessage(qrCodeVo.getReturnCode(), 2);//响应码
            AddStringToMessage(qrCodeVo.getErrCode(), 2);//错误码

            byte[] msg = trimMessage();
//            logger.info("Construct message 48 success! ");
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("48["+ Arrays.toString(msg)+"]");
            return msg;
        } catch (Exception e) {
            logger.error("Construct message 48 error! " + e);
            return new byte[0];
        }
    }
}
