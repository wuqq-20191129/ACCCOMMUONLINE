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
 * 二维码认证响应（66 ）
 * @author lindq
 */
class ConstructMessage66 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage66.class
            .getName());

    public ConstructMessage66() {
    }

    byte[] constructMessage(QrCodeVo qrCodeVo) {
        qrCodeVo.setMessageId("66");
        try {
            initMessage();
            AddStringToMessage(qrCodeVo.getMessageId(), 2);
            AddBcdToMessage(qrCodeVo.getMsgGenTime(), 7);
            AddStringToMessage(qrCodeVo.getTerminalNo(), 9);//终端编号
            AddStringToMessage(qrCodeVo.getSamLogicalId(), 16);
            
            AddLongToMessage(qrCodeVo.getTerminaSeq(), 4);//终端处理流水号
            AddLongToMessage(qrCodeVo.getAccSeq(), 4);//中心处理流水号
            AddStringToMessage(qrCodeVo.getReturnCode(), 2);//响应码
            AddStringToMessage(qrCodeVo.getOrderNo(), 14);//订单号
            AddStringToMessage(qrCodeVo.getPhoneNo(), 11);//手机号
            
            AddLongToMessage(qrCodeVo.getSaleFee(), 4);//发售单价
            AddLongToMessage(qrCodeVo.getSaleTimes(), 4);//发售数量
            AddLongToMessage(qrCodeVo.getDealFee(), 4);//发售总价
            AddLongToMessage(qrCodeVo.getHasTakeNum(), 4);//已取数量

            byte[] msg = trimMessage();
//            logger.info("Construct message 66 success! ");
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("66["+ Arrays.toString(msg)+"]");
            return msg;
        } catch (Exception e) {
            logger.error("Construct message 66 error! " + e);
            return new byte[0];
        }
    }
}
