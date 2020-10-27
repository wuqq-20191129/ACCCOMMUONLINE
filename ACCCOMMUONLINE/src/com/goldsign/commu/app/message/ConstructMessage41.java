/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.message;

import com.goldsign.commu.app.vo.AirSaleCfmVo;
import com.goldsign.commu.frame.message.ConstructMessageBase;
import com.goldsign.commu.frame.util.CharUtil;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * 空中发售申请响应（41 ）
 * @author lindq
 */
class ConstructMessage41 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage41.class
            .getName());

    public ConstructMessage41() {
    }

    byte[] constructMessage(AirSaleCfmVo airSaleCfmVo) {
        airSaleCfmVo.setMessageId("41");
        
        try {
            initMessage();
            AddStringToMessage(airSaleCfmVo.getMessageId(), 2);
            AddBcdToMessage(airSaleCfmVo.getMsgGenTime(), 7);
            AddLongToMessage(airSaleCfmVo.getWaterNo(), 4);//系统参照号
            AddStringToMessage(airSaleCfmVo.getTerminaNo(), 9);//终端编号
            AddStringToMessage(airSaleCfmVo.getSamLogicalId(), 16);
            AddLongToMessage(airSaleCfmVo.getTerminaSeq(), 4);//终端处理流水号
            AddStringToMessage(CharUtil.addCharsAfter(airSaleCfmVo.getCardLogicalId(),20," "), 20);//票卡逻辑卡号
            AddStringToMessage(CharUtil.addCharsAfter(airSaleCfmVo.getCardPhyId(),20," "), 20);//票卡物理卡号
            AddStringToMessage(airSaleCfmVo.getIsTestFlag(), 1);//测试标记 0正常，1测试
            AddStringToMessage(airSaleCfmVo.getAppCode(), 2);//app渠道
            AddStringToMessage(airSaleCfmVo.getReturnCode(), 2);//响应码
            AddStringToMessage(airSaleCfmVo.getErrCode(), 2);//错误码

            byte[] msg = trimMessage();
//            logger.info("Construct message 41 success! ");
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("41["+ Arrays.toString(msg) +"]");
            return msg;
        } catch (Exception e) {
            logger.error("Construct message 41 error! " + e);
            return new byte[0];
        }
    }
}
