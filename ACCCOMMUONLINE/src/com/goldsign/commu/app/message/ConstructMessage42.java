/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.message;

import com.goldsign.commu.app.vo.AirChargeVo;
import com.goldsign.commu.frame.message.ConstructMessageBase;
import com.goldsign.commu.frame.util.CharUtil;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * 空中充值申请响应（42 ）
 * @author lindq
 */
class ConstructMessage42 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage42.class
            .getName());

    public ConstructMessage42() {
    }

    byte[] constructMessage(AirChargeVo airChargeVo) {
        airChargeVo.setMessageId("42");
        
        try {
            initMessage();
            AddStringToMessage(airChargeVo.getMessageId(), 2);
            AddBcdToMessage(airChargeVo.getMsgGenTime(), 7);
            AddLongToMessage(airChargeVo.getWaterNo(), 4);//系统参照号
            AddStringToMessage(airChargeVo.getTerminaNo(), 9);//终端编号
            AddStringToMessage(airChargeVo.getSamLogicalId(), 16);
            AddLongToMessage(airChargeVo.getTerminaSeq(), 4);//终端处理流水号
            AddStringToMessage(CharUtil.addCharsAfter(airChargeVo.getCardLogicalId(),20," "), 20);//票卡逻辑卡号
            AddStringToMessage(airChargeVo.getMac2(), 8);//mac2
            AddBcdToMessage(airChargeVo.getDealTime(), 7);//系统时间
            AddStringToMessage(airChargeVo.getReturnCode(), 2);//响应码
            AddStringToMessage(airChargeVo.getErrCode(), 2);//错误码

            byte[] msg = trimMessage();
//            logger.info("Construct message 42 success! ");
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("42["+ Arrays.toString(msg) +"]");
            return msg;
        } catch (Exception e) {
            logger.error("Construct message 42 error! " + e);
            return new byte[0];
        }
    }
}
