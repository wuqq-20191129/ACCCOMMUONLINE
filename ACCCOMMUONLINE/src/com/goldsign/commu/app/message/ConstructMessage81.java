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
 * 支付二维码状态查询请求响应（81）
 * @author lindq
 */
class ConstructMessage81 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage81.class
            .getName());

    public ConstructMessage81() {
    }

    byte[] constructMessage(QrPayOrderVo qrPayOrderVo) {
        qrPayOrderVo.setMessageId("81");
        try {
            initMessage();
            AddStringToMessage(qrPayOrderVo.getMessageId(), 2);
            AddBcdToMessage(qrPayOrderVo.getMsgGenTime(), 7);
            AddStringToMessage(qrPayOrderVo.getTerminalNo(), 9);//终端编号
            AddLongToMessage(qrPayOrderVo.getTerminaSeq(), 4);//HCE处理流水号
            AddStringToMessage(qrPayOrderVo.getPhoneNo(), 11);//手机号
            AddStringToMessage(qrPayOrderVo.getImsi(), 15);//手机用户标识
            AddStringToMessage(qrPayOrderVo.getImei(), 15);//手机设备标识
            AddStringToMessage(qrPayOrderVo.getAppCode(), 2);//app渠道
            AddStringToMessage(qrPayOrderVo.getQrpayId(), 10);//支付标识
            AddStringToMessage(qrPayOrderVo.getQrpayData(), 34);//支付二维码信息
            AddStringToMessage(qrPayOrderVo.getOrderNo(), 14);//订单号
            AddBcdToMessage(qrPayOrderVo.getOrderDate(), 7);//订单生成时间
            AddBcdToMessage(qrPayOrderVo.getCardTypeTotal(), 2);//发售票卡类型
            AddLongToMessage(qrPayOrderVo.getSaleFeeTotal(), 4);//发售单程票单价
            AddLongToMessage(qrPayOrderVo.getSaleTimesTotal(), 4);//发售单程票数量
            AddLongToMessage(qrPayOrderVo.getDealFeeTotal(), 4);//发售单程票总价
            AddStringToMessage(qrPayOrderVo.getStatus(), 2);//订单状态
            AddBcdToMessage(qrPayOrderVo.getCardType(), 2);//出票票卡类型
            AddLongToMessage(qrPayOrderVo.getSaleFee(), 4);//出票单程票单价
            AddLongToMessage(qrPayOrderVo.getSaleTimes(), 4);//出票单程票数量
            AddLongToMessage(qrPayOrderVo.getDealFee(), 4);//出票单程票总价
            AddBcdToMessage(qrPayOrderVo.getValidTime(), 7);//订单有效截止时间

            byte[] msg = trimMessage();
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("81["+ Arrays.toString(msg)+"]");
            return msg;
        } catch (Exception e) {
            logger.error("Construct message 81 error! " + e);
            return new byte[0];
        }
    }
}
