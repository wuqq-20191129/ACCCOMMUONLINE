/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.message;

import com.goldsign.commu.app.vo.AirChargeCfmVo;
import com.goldsign.commu.frame.message.ConstructMessageBase;
import com.goldsign.commu.frame.util.CharUtil;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * 空中充值撤销确认响应（45 ）
 * @author lindq
 */
class ConstructMessage45 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage45.class
            .getName());

    public ConstructMessage45() {
    }

    byte[] constructMessage(AirChargeCfmVo airChargeCfmVo) {
        airChargeCfmVo.setMessageId("45");
        
        try {
            initMessage();
            AddStringToMessage(airChargeCfmVo.getMessageId(), 2);
            AddBcdToMessage(airChargeCfmVo.getMsgGenTime(), 7);
            AddLongToMessage(airChargeCfmVo.getWaterNo(), 4);//系统参照号
            AddStringToMessage(airChargeCfmVo.getTerminaNo(), 9);//终端编号
            AddStringToMessage(airChargeCfmVo.getSamLogicalId(), 16);
            AddLongToMessage(airChargeCfmVo.getTerminaSeq(), 4);//终端处理流水号
            AddBcdToMessage(airChargeCfmVo.getCardType(), 2);//票卡类型
            AddStringToMessage(CharUtil.addCharsAfter(airChargeCfmVo.getCardLogicalId(),20," "), 20);//票卡逻辑卡号
            AddLongToMessage(airChargeCfmVo.getSysRefNoChr(), 4);//空充系统参照号
            AddStringToMessage(airChargeCfmVo.getReturnCode(), 2);//响应码
            AddStringToMessage(airChargeCfmVo.getErrCode(), 2);//错误码

            byte[] msg = trimMessage();
//            logger.info("Construct message 45 success! ");
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("45["+ Arrays.toString(msg) +"]");
            return msg;
        } catch (Exception e) {
            logger.error("Construct message 45 error! " + e);
            return new byte[0];
        }
    }
}
