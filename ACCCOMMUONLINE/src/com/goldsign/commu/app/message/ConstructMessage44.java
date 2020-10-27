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
 * 空中充值撤销申请响应（44 ）
 * @author lindq
 */
class ConstructMessage44 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage44.class
            .getName());

    public ConstructMessage44() {
    }

    byte[] constructMessage(AirChargeVo airChargeVo) {
        airChargeVo.setMessageId("44");
        
        try {
            initMessage();
            AddStringToMessage(airChargeVo.getMessageId(), 2);
            AddBcdToMessage(airChargeVo.getMsgGenTime(), 7);
            AddLongToMessage(airChargeVo.getWaterNo(), 4);//系统参照号
            AddStringToMessage(airChargeVo.getTerminaNo(), 9);//终端编号
            AddStringToMessage(airChargeVo.getSamLogicalId(), 16);
            AddLongToMessage(airChargeVo.getTerminaSeq(), 4);//终端处理流水号
            AddBcdToMessage(airChargeVo.getCardType(), 2);//票卡类型
            AddStringToMessage(CharUtil.addCharsAfter(airChargeVo.getCardLogicalId(),20," "), 20);//票卡逻辑卡号
            AddStringToMessage(airChargeVo.getPhoneNo(), 11);//手机号
            AddLongToMessage(airChargeVo.getSysRefNoChr(), 4);//空充系统参照号
            AddBcdToMessage(airChargeVo.getDealTime(), 7);//系统时间
            AddStringToMessage(airChargeVo.getReturnCode(), 2);//响应码
            AddStringToMessage(airChargeVo.getErrCode(), 2);//错误码

            byte[] msg = trimMessage();
//            logger.info("Construct message 44 success! ");
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("44["+ Arrays.toString(msg) +"]");
            return msg;
        } catch (Exception e) {
            logger.error("Construct message 44 error! " + e);
            return new byte[0];
        }
    }
}
