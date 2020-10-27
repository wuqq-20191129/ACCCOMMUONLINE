/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.message;

import com.goldsign.commu.app.vo.QrPayOrderVo;
import com.goldsign.commu.frame.message.ConstructMessageBase;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * 支付二维码订单app支付结果请求响应（82）
 * @author lindq
 */
class ConstructMessage82 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage82.class
            .getName());

    public ConstructMessage82() {
    }

    byte[] constructMessage(QrPayOrderVo qrPayOrderVo) {
        qrPayOrderVo.setMessageId("82");
        try {
            initMessage();
            AddStringToMessage(qrPayOrderVo.getMessageId(), 2);
            AddBcdToMessage(qrPayOrderVo.getMsgGenTime(), 7);
            AddLongToMessage(qrPayOrderVo.getTerminaSeq(), 4);//HCE处理流水号
            AddLongToMessage(qrPayOrderVo.getAccSeq(), 4);//中心处理流水号
            AddStringToMessage(qrPayOrderVo.getQrpayId(), 10);//支付标识
            AddStringToMessage(qrPayOrderVo.getQrpayData(), 34);//支付二维码信息
            AddBcdToMessage(qrPayOrderVo.getMsgGenTime(), 7);//中心处理时间
            AddStringToMessage(qrPayOrderVo.getReturnCode(), 2);//返回响应码

            byte[] msg = trimMessage();
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("82["+ Arrays.toString(msg)+"]");
            return msg;
        } catch (Exception e) {
            logger.error("Construct message 82 error! " + e);
            return new byte[0];
        }
    }
}
