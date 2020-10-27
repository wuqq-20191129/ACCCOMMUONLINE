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
 * 二维码状态请求响应（47 ）
 * @author lindq
 */
class ConstructMessage47 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage47.class
            .getName());

    public ConstructMessage47() {
    }

    byte[] constructMessage(QrCodeVo qrCodeVo) {
        qrCodeVo.setMessageId("47");
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
            AddBcdToMessage(qrCodeVo.getValidTime(), 7);//有效时间
            
            AddLongToMessage(qrCodeVo.getSaleTimes(), 4);//发售单程票总数量
            AddLongToMessage(qrCodeVo.getTakeTimes(), 4);//已取单程票数量
            AddLongToMessage(qrCodeVo.getSaleFee(), 4);//发售单程票单价
            AddBcdToMessage(qrCodeVo.getDealTime(), 7);//取票时间
            AddStringToMessage(qrCodeVo.getReturnCode(), 2);//响应码
            AddStringToMessage(qrCodeVo.getErrCode(), 2);//错误码

            byte[] msg = trimMessage();
//            logger.info("Construct message 47 success! ");
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("47["+ Arrays.toString(msg)+"]");
            return msg;
        } catch (Exception e) {
            logger.error("Construct message 47 error! " + e);
            return new byte[0];
        }
    }
}
