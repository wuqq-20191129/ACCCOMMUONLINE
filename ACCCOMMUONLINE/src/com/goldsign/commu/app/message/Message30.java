/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.message;

import com.goldsign.commu.app.dao.AirDao;
import com.goldsign.commu.app.dao.HceIcDao;
import com.goldsign.commu.app.dao.SeqDao;
import com.goldsign.commu.app.util.EncryptorJMJUtil;
import com.goldsign.commu.app.vo.AirSaleVo;
import com.goldsign.commu.app.vo.EncryptorVo;
import com.goldsign.commu.app.vo.TicketAttributeVo;
import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.constant.FrameMessageCodeConstant;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.LogUtil;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import com.goldsign.commu.frame.vo.MessageQueue;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * @datetime 2017-12-6
 * @author lind
 * 空中发售申请
 */
public class Message30 extends MessageValidate{
    
    private final static byte[] LOCK = new byte[0];
    private static Logger logger = Logger.getLogger(Message30.class.getName());
    //序列标签
    private static final String SEQ_LABLE = FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_air_sale";

    @Override
    public void run() throws Exception {
        //数据区字节长度
        fix_recv_data_length = 99;
        process();
    }
    
    public void process() throws Exception {
        synchronized (LOCK) {
            processMessage();
        }
    }

    private void processMessage() throws Exception {
        
        AirSaleVo airSaleVo = new AirSaleVo();
        try {
//            logger.info("-- 解析空中发售申请消息30 --");
            //汇总日志为1条输出 20190911 zhongzq
            ArrayList logList = new ArrayList();
            logList.add("空中发售申请业务30消息");
            ThreadLocalUtil.LOG_THREAD_LOCAL.set(logList);
            // 校验数据区长度
            validateDataLen();
            
            //从消息中获取信息
            getDataVo(airSaleVo);
            
            // 入库
            AirDao airDao = new AirDao();
            airDao.insertAirSale(airSaleVo, getOLDbHelper());
            
            //整合返回消息
            constructMessageVo(airSaleVo);
//            throw new SQLException("Text sqlExecption");
            //入库和发送响应消息40
            //modiy by zhongziqi 20190809 优化逻辑 如果发送后下面数据库异常则会发送两条报文
//            writeBackMsg(airSaleVo);
            airDao.insertAirSaleResp(airSaleVo, getOLDbHelper());
            writeBackMsg(airSaleVo);
//            logger.info("--空中发售请求响应结束--");
            logger.info(new ThreadLocalUtil().ArrayToString(logList, true));
        } catch (Exception e) {
//            logger.error("处理30消息出现异常", e);
            // 出现异常返回响应的错误信息给终端
            dealException(airSaleVo, e);
            logger.error(new ThreadLocalUtil().ArrayToString(ThreadLocalUtil.LOG_THREAD_LOCAL.get(), false) + "处理30消息出现异常", e);

            throw e;
        } finally {
            if (null != getOLDbHelper()) {
                getOLDbHelper().close();
            }
        }

    }
    
    private void dealException(AirSaleVo airSaleVo, Exception e) {
        String errCode = doException(e);
        airSaleVo.setReturnCode("01");
        airSaleVo.setErrCode(errCode);
        //消息生成时间
        airSaleVo.setMsgGenTime(DateHelper.currentTodToString());

        writeBackMsg(airSaleVo);
    }

    private void getDataVo(AirSaleVo airSaleVo) throws Exception  {
        // 消息类型
        String messageId = getCharString(0, 2);
        // 消息生成时间
        String msgGenTime = getBcdString(2, 7);
        // 终端编号
        String terminaNo = getCharString(9, 9);
        // Sam卡逻辑卡号
        String samLogicalId = getCharString(18, 16);
        // 终端处理流水号
        long terminaSeq = getLong2(34);
        // 网点编码
        String branchesCode = getCharString(38, 16);
        // 票卡类型
        String cardType = getBcdString(54, 2);
        // 手机号
        String phoneNo = getCharString(56, 11);
        // 手机用户标识
        String imsi = getCharString(67, 15);
        // 手机设备标识
        String imei = getCharString(82, 15);
        // app渠道
        String appCode = getCharString(97, 2);
        
        // acc流水号
        long waterNo = SeqDao.getInstance().selectNextSeq(SEQ_LABLE, getOLDbHelper());
        airSaleVo.setWaterNo(waterNo);
        airSaleVo.setMessageId(messageId);
        airSaleVo.setMsgGenTime(msgGenTime);
        airSaleVo.setTerminaNo(terminaNo);
        airSaleVo.setSamLogicalId(samLogicalId);
        airSaleVo.setTerminaSeq(terminaSeq);
        airSaleVo.setBranchesCode(branchesCode);
        airSaleVo.setCardType(cardType);
        airSaleVo.setPhoneNo(phoneNo);
        airSaleVo.setImsi(imsi);
        airSaleVo.setImei(imei);
        airSaleVo.setSysRefNo(waterNo);
        airSaleVo.setAppCode(appCode);
        //add by zhongzq 20190919
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add(airSaleVo.toString());
    }


    private void constructMessageVo(AirSaleVo airSaleVo) throws SQLException {
        String nowDay = DateHelper.dateOnlyToString(new Date());
        String dealDay = nowDay;
        String cardDay = nowDay;
        String cardAppDay = nowDay;

        airSaleVo.setProductCode(FrameMessageCodeConstant.HCE_PRODUCT_CODE);
        airSaleVo.setCityCode(FrameMessageCodeConstant.CITY_CODE);
        airSaleVo.setBusinessCode(FrameMessageCodeConstant.BUSINESS_CODE);
        airSaleVo.setDealDay(dealDay);
//        airSaleVo.setDealDevCode(FrameMessageCodeConstant.HCE_DEAL_DEV_CODE);
        airSaleVo.setCardVer(FrameMessageCodeConstant.HCE_CAR_VER);
        airSaleVo.setCardDay(cardDay);
        airSaleVo.setCardAppDay(cardAppDay);
        airSaleVo.setCardAppVer(FrameMessageCodeConstant.HCE_CARD_APP_VER);
        airSaleVo.setIsTestFlag(FrameMessageCodeConstant.IS_TEST_FLAG);//测试标志0:正式,1:测试
//        airSaleVo.setSaleActFlag(FrameMessageCodeConstant.HCE_SALE_ACT_FLAG);//发售激活标志0:不激活,1:激活
        TicketAttributeVo ticketAttributeVo = FrameMessageCodeConstant.TICKET_ATTRIBUTE.get(airSaleVo.getCardType());
        //add by zhongzq
        if(ticketAttributeVo !=null){
            airSaleVo.setFaceValue(ticketAttributeVo.getFaceValue());
            airSaleVo.setDepositFee(ticketAttributeVo.getDepositFee());
            String appExpiryStart = getVaildStartDate((int) ticketAttributeVo.getTicketUsedPreValidDays());
            airSaleVo.setAppExpiryStart(appExpiryStart);//乘次票应用有效期开始时间
            airSaleVo.setAppExpiryDay(Long.toString(ticketAttributeVo.getTicketUsedValidDays()));//乘次票使用有效期
            airSaleVo.setSaleActFlag(ticketAttributeVo.getSaleActivateFlag());
            airSaleVo.setChargeLimit(ticketAttributeVo.getChargeMaxLimit());
        }else {
            airSaleVo.setSaleActFlag(FrameMessageCodeConstant.HCE_SALE_ACT_FLAG);//发售激活标志0:不激活,1:激活

        }
        EncryptorVo encryptorVo = new EncryptorVo();
        boolean result = false;
        // 查询空发申请确认记录(存在手机号码重复使用，不作检验)
//        AirDao airDao = new AirDao();
//        result = airDao.checkAirSaleCfm(airSaleVo.getPhoneNo(), getOLDbHelper());
        
        //如果不存在记空发申请，取逻辑卡号
//        if(!result){
            //取票卡逻辑卡号
            //先查有空发申请，但没有确认的记录
        //add by zhongzq  20190911 增加判断手机票才需要做重复校验 次票月票校验由hce控制 多元支付平台主要负责返回密钥
          if("0800".equals(airSaleVo.getCardType())) {
              result = HceIcDao.getInstance().checkAirSaleTK(airSaleVo, getOLDbHelper());
          }
            if(!result){
                //取逻辑卡号段最小逻辑卡号
                result = HceIcDao.getInstance().getCardLogicalTK(airSaleVo, getOLDbHelper());
            }
            if(result){
                //取17个密钥
                encryptorVo.setCardLogicalId(airSaleVo.getCardLogicalId());
                try {
                    EncryptorJMJUtil.getCardKey(encryptorVo);
                }catch (Exception ex) {
                    ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("加密机异常："+ex.getMessage());
                    logger.error("加密机异常："+ex.getMessage());
                    encryptorVo.setReturnCode("01");
                    encryptorVo.setErrCode("14");//加密机系统异常
                }
                airSaleVo.setCardMac(encryptorVo.getMac());
                airSaleVo.setReturnCode(encryptorVo.getReturnCode());//响应码
                airSaleVo.setErrCode(encryptorVo.getErrCode());//错误码
            //取不到逻辑卡号时
            }else{
                airSaleVo.setReturnCode("01");//响应码
                airSaleVo.setErrCode("63");//无法查询逻辑卡号段
            }
//        }else{
//            airSaleVo.setReturnCode("01");//响应码
//            airSaleVo.setErrCode("62");//重复空发
//        }

        //消息生成时间
        airSaleVo.setMsgGenTime(DateHelper.currentTodToString());
    }

    private String getVaildStartDate(int ticketUsedPreValidDays) {
        if(ticketUsedPreValidDays ==-1){
            return "00000000";
        }else {
            Date date = DateHelper.addDay(new Date(),ticketUsedPreValidDays);
            return DateHelper.dateOnlyToString(date);
        }
    }

    private void writeBackMsg(AirSaleVo airSaleVo) {
        // 返回消息
        MessageQueue backMesg = getMessageQueue(airSaleVo);
//        try {
//            LogUtil.writeRecvSendLog(null, "localhost", "1",//0:请求消息、1：响应消息
//                    airSaleVo.getMessageId(), messageSequ, backMesg.getMessage(),
//                    airSaleVo.getReturnCode().equals("00")?"0":"1", getOLDbHelper());//0：成功1：失败
//        } catch (Exception ex) {
//            logger.error("记录日志发生异常：", ex);
//        }
        sendMsgToConnectionQueue(backMesg);
    }
    
    private MessageQueue getMessageQueue(AirSaleVo airSaleVo) {
        byte[] msgVo = new ConstructMessage40().constructMessage(airSaleVo);
        MessageQueue messageQueue = new MessageQueue();
        messageQueue.setMessageTime(new Date(System.currentTimeMillis()));
        messageQueue.setIpAddress(this.messageFrom);
        messageQueue.setMessage(msgVo);
        messageQueue.setIsParaInformMsg("0");
        messageQueue.setParaInformWaterNo(0);
        messageQueue.setLineId("");
        messageQueue.setStationId("");
        messageQueue.setMessageSequ(messageSequ);
        return messageQueue;
    }

    private void sendMsgToConnectionQueue(MessageQueue mq) {
        CommuConnection con = this.bridge.getConnection();
        con.setMessageInConnectionQueue(mq);
    }
}
