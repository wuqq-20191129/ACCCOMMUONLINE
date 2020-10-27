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
 * 二维码取票码请求响应（46 ）
 * @author lindq
 */
class ConstructMessage46 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage46.class
            .getName());

    public ConstructMessage46() {
    }

    byte[] constructMessage(QrCodeVo qrCodeVo) {
        qrCodeVo.setMessageId("46");
        try {
            initMessage();
            AddStringToMessage(qrCodeVo.getMessageId(), 2);
            AddBcdToMessage(qrCodeVo.getMsgGenTime(), 7);
            AddStringToMessage(qrCodeVo.getTerminalNo(), 9);//终端编号
            AddStringToMessage(qrCodeVo.getSamLogicalId(), 16);
            
            AddLongToMessage(qrCodeVo.getTerminaSeq(), 4);//终端处理流水号
            AddLongToMessage(qrCodeVo.getAccSeq(), 4);//中心处理流水号
            
            AddStringToMessage(qrCodeVo.getQrcode(), 64);//二维码
            AddStringToMessage(qrCodeVo.getOrderNo(), 14);//订单号
            AddStringToMessage(qrCodeVo.getPhoneNo(), 11);//手机号
            
            AddBcdToMessage(qrCodeVo.getValidTime(), 7);//有效时间
            AddStringToMessage(qrCodeVo.getReturnCode(), 2);//响应码
            AddStringToMessage(qrCodeVo.getErrCode(), 2);//错误码

            byte[] msg = trimMessage();
//            logger.info("Construct message 46 success! ");
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("46["+ Arrays.toString(msg) +"]");
            return msg;
        } catch (Exception e) {
            logger.error("Construct message 46 error! " + e);
            return new byte[0];
        }
    }
}
