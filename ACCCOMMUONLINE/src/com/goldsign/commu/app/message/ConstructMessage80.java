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
 * 支付二维码订单上传响应（80）
 * @author lindq
 */
class ConstructMessage80 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage80.class
            .getName());

    public ConstructMessage80() {
    }

    byte[] constructMessage(QrPayOrderVo qrPayOrderVo) {
        qrPayOrderVo.setMessageId("80");
        try {
            initMessage();
            AddStringToMessage(qrPayOrderVo.getMessageId(), 2);
            AddBcdToMessage(qrPayOrderVo.getMsgGenTime(), 7);
            AddStringToMessage(qrPayOrderVo.getTerminalNo(), 9);//终端编号
            AddStringToMessage(qrPayOrderVo.getSamLogicalId(), 16);
            AddLongToMessage(qrPayOrderVo.getTerminaSeq(), 4);//终端处理流水号
            AddLongToMessage(qrPayOrderVo.getAccSeq(), 4);//中心处理流水号
            AddStringToMessage(qrPayOrderVo.getOrderNo(), 14);//订单号
            AddStringToMessage(qrPayOrderVo.getQrpayId(), 10);//支付标识
            AddStringToMessage(qrPayOrderVo.getQrpayData(), 34);//支付信息
            AddStringToMessage(qrPayOrderVo.getReturnCode(), 2);//响应码

            byte[] msg = trimMessage();
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("80["+ Arrays.toString(msg)+"]");
            return msg;
        } catch (Exception e) {
            logger.error("Construct message 80 error! " + e);
            return new byte[0];
        }
    }
}
