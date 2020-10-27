/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.message;

import com.goldsign.commu.app.vo.AirSaleVo;
import com.goldsign.commu.frame.message.ConstructMessageBase;
import com.goldsign.commu.frame.util.CharUtil;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 * 空中发售申请响应（40 ）
 * @author lindq
 */
class ConstructMessage40 extends ConstructMessageBase {

    private static Logger logger = Logger.getLogger(ConstructMessage40.class
            .getName());

    public ConstructMessage40() {
    }

    byte[] constructMessage(AirSaleVo airSaleVo) {
        airSaleVo.setMessageId("40");
        
        try {
            initMessage();
            AddStringToMessage(airSaleVo.getMessageId(), 2);
            AddBcdToMessage(airSaleVo.getMsgGenTime(), 7);
            AddLongToMessage(airSaleVo.getWaterNo(), 4);//系统参照号
            AddStringToMessage(airSaleVo.getTerminaNo(), 9);//终端编号
            AddStringToMessage(airSaleVo.getSamLogicalId(), 16);
            AddLongToMessage(airSaleVo.getTerminaSeq(), 4);//终端处理流水号
            AddStringToMessage(airSaleVo.getPhoneNo(), 11);//手机号
            AddStringToMessage(airSaleVo.getImsi(), 15);//手机用户标识
            AddStringToMessage(airSaleVo.getImei(), 15);//手机设备标识
            
            AddStringToMessage(airSaleVo.getProductCode(), 4);//发卡方代码
            AddStringToMessage(airSaleVo.getCityCode(), 4);//城市代码
            AddStringToMessage(airSaleVo.getBusinessCode(), 4);//行业代码
            AddStringToMessage(airSaleVo.getDealDay(), 8);//发行日期
            
            AddStringToMessage(airSaleVo.getDealDevCode(), 5);//发行设备代码
            AddStringToMessage(airSaleVo.getCardVer(), 2);//卡版本号
            AddStringToMessage(airSaleVo.getCardDay(), 8);//卡启用日期
            AddStringToMessage(airSaleVo.getCardAppDay(), 8);//卡应用启用日期
            
            AddStringToMessage(airSaleVo.getCardAppVer(), 2);//卡应用版本
            AddStringToMessage(CharUtil.addCharsAfter(airSaleVo.getCardLogicalId(),20," "), 20);//票卡逻辑卡号
            AddStringToMessage(CharUtil.addCharsAfter(airSaleVo.getCardPhyId(),20," "), 20);//票卡物理卡号
            AddStringToMessage(airSaleVo.getExpiryDate(), 8);//票卡有效期
            
            AddLongToMessage(airSaleVo.getFaceValue(), 4);//面值
            AddLongToMessage(airSaleVo.getDepositFee(), 4);//押金
            AddStringToMessage(airSaleVo.getAppExpiryStart(), 8);//乘次票应用有效期开始时间
            AddStringToMessage(CharUtil.addCharsBefore(airSaleVo.getAppExpiryDay(),5,"0"), 5);//乘次票使用有效期
            
            AddStringToMessage(airSaleVo.getSaleActFlag(), 1);//发售激活标志 0不激活，1激活
            AddStringToMessage(airSaleVo.getIsTestFlag(), 1);//测试标记 0正常，1测试
            AddLongToMessage(airSaleVo.getChargeLimit(), 4);//充值上限
            AddStringToMessage(airSaleVo.getLimitEntryStation(), 4);//限制进闸车站
            
            AddStringToMessage(airSaleVo.getLimitMode(), 3);//限制模式
            AddStringToMessage(airSaleVo.getLimitExitStation(), 4);//限制出闸车站
            AddStringToMessage(CharUtil.addCharsAfter(airSaleVo.getCardMac(),544," "), 544);//卡密钥
            AddStringToMessage(airSaleVo.getAppCode(), 2);//app渠道
            AddStringToMessage(airSaleVo.getReturnCode(), 2);//响应码
            AddStringToMessage(airSaleVo.getErrCode(), 2);//错误码

            byte[] msg = trimMessage();
//            logger.info("Construct message 40 success! ");
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("40["+ Arrays.toString(msg) +"]");
            return msg;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Construct message 40 error! " + e);
            return new byte[0];
        }
    }
}
