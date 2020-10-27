/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.message;

import com.goldsign.commu.app.dao.AirDao;
import com.goldsign.commu.app.dao.HceIcDao;
import com.goldsign.commu.app.dao.SeqDao;
import com.goldsign.commu.app.vo.AirSaleCfmVo;
import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.dao.PubDao;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.LogUtil;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import com.goldsign.commu.frame.vo.MessageQueue;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * @datetime 2017-12-6
 * @author lind
 * 空中发售确认
 */
public class Message31 extends MessageValidate{
    
    private final static byte[] LOCK = new byte[0];
    private static Logger logger = Logger.getLogger(Message31.class.getName());
    //序列标签
    private static final String SEQ_LABLE = FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_air_sale_cfm";

    @Override
    public void run() throws Exception {
        //数据区字节长度
        fix_recv_data_length = 160;
        process();
    }
    
    public void process() throws Exception {
        synchronized (LOCK) {
            processMessage();
        }
    }

    private void processMessage() throws Exception {
        
        AirSaleCfmVo airSaleCfmVo = new AirSaleCfmVo();
        try {
            ArrayList logList = new ArrayList();
            logList.add("空中发售确认业务31消息");
            ThreadLocalUtil.LOG_THREAD_LOCAL.set(logList);
//            logger.info("-- 解析空中发售确认消息31 --");
            // 校验数据区长度
            validateDataLen();
            
            //从消息中获取信息
            getDataVo(airSaleCfmVo);
            
            // 入库
            AirDao airDao = new AirDao();
            airDao.insertAirSaleCfm(airSaleCfmVo, getOLDbHelper());
            
            //整合返回消息
            constructMessageVo(airSaleCfmVo);
            //入库和发送响应消息41
            //modiy by zhongziqi 20190809 优化逻辑 如果发送后下面数据库异常则会发送两条报文
//            writeBackMsg(airSaleCfmVo);
            airDao.insertAirSaleCfmResp(airSaleCfmVo, getOLDbHelper());
            // 更新统计表w_ol_sts_acc
//            if("00".equals(airSaleCfmVo.getReturnCode())){
            //发卡成功才更新 20191008 zhongzq
            if("00".equals(airSaleCfmVo.getReturnCode())&&"0".equals(airSaleCfmVo.getResultCode())){
                updateStsAcc(airSaleCfmVo);
                //add by zhongzq
                HceIcDao.getInstance().updateCardLogicalTK(airSaleCfmVo, getOLDbHelper());
            }
            //更新票库记录为确认状态
//            HceIcDao.getInstance().updateCardLogicalTK(airSaleCfmVo, getOLDbHelper());
            writeBackMsg(airSaleCfmVo);
//            logger.info("--空中发售确认响应结束--");
            logger.info(new ThreadLocalUtil().ArrayToString(logList, true));
        } catch (Exception e) {
//            logger.error("处理31消息出现异常", e);
            logger.error(new ThreadLocalUtil().ArrayToString(ThreadLocalUtil.LOG_THREAD_LOCAL.get(), false) + "处理31消息出现异常", e);
            // 出现异常返回响应的错误信息给终端
            dealException(airSaleCfmVo, e);
            throw e;
        } finally {
            if (null != getOLDbHelper()) {
                getOLDbHelper().close();
            }
        }

    }
    
    private void dealException(AirSaleCfmVo airSaleCfmVo, Exception e) {
        String errCode = doException(e);
        airSaleCfmVo.setReturnCode("01");
        airSaleCfmVo.setErrCode(errCode);
        //消息生成时间
        airSaleCfmVo.setMsgGenTime(DateHelper.currentTodToString());

        writeBackMsg(airSaleCfmVo);
    }

    private void getDataVo(AirSaleCfmVo airSaleCfmVo) throws Exception  {
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
        //发行者主编码
        String issMainCode = getCharString(54, 4);
        //发行者子编码
        String issSubCode = getCharString(58, 4);
        // 手机号
        String phoneNo = getCharString(62, 11);
        // 手机用户标识
        String imsi = getCharString(73, 15);
        // 手机设备标识
        String imei = getCharString(88, 15);
        // 票卡类型
        String cardType = getBcdString(103, 2);
        //票卡逻辑卡号
        String cardLogicalId = getCharString(105, 20);
        //票卡物理卡号
        String cardPhyId = getCharString(125, 20);
        // 是否测试卡
        String isTestFlag = getCharString(145, 1);
        // 发卡结果
        String resultCode = getCharString(146, 1);
        // 发卡时间
        String dealTime = getBcdString(147, 7);
        // 系统参照号
        long sysRefNo = getLong2(154);
        // app渠道
        String appCode = getCharString(158, 2);
        
        // acc流水号
        long waterNo = SeqDao.getInstance().selectNextSeq(SEQ_LABLE, getOLDbHelper());
        airSaleCfmVo.setWaterNo(waterNo);
        airSaleCfmVo.setMessageId(messageId);
        airSaleCfmVo.setMsgGenTime(msgGenTime);
        airSaleCfmVo.setTerminaNo(terminaNo);
        airSaleCfmVo.setSamLogicalId(samLogicalId);
        airSaleCfmVo.setTerminaSeq(terminaSeq);
        airSaleCfmVo.setBranchesCode(branchesCode);
        airSaleCfmVo.setIssMainCode(issMainCode);
        airSaleCfmVo.setIssSubCode(issSubCode);
        airSaleCfmVo.setPhoneNo(phoneNo);
        airSaleCfmVo.setImsi(imsi);
        airSaleCfmVo.setImei(imei);
        airSaleCfmVo.setCardType(cardType);
        airSaleCfmVo.setCardLogicalId(cardLogicalId);
        airSaleCfmVo.setCardPhyId(cardPhyId);
        airSaleCfmVo.setIsTestFlag(isTestFlag);
        airSaleCfmVo.setResultCode(resultCode);
        airSaleCfmVo.setDealTime(dealTime);
        airSaleCfmVo.setSysRefNo(sysRefNo);
        airSaleCfmVo.setAppCode(appCode);
        //add by zhongzq 20190919
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add(airSaleCfmVo.toString());
    }


    private void constructMessageVo(AirSaleCfmVo airSaleCfmVo) {
        boolean result = true;
        String returnCode = "00";
        String errCode = "00";
        // 查询空发申请响应记录
        AirDao airDao = new AirDao();
        try {
            result = airDao.checkAirSale(airSaleCfmVo, getOLDbHelper());
        } catch (SQLException ex) {
            logger.error("查询空发申请响应数据库记录异常：", ex);
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("查询空发申请响应数据库记录异常:"+ex.getMessage());
            returnCode = "01";
            errCode = "12";
        }
        if(!result){
            returnCode = "01";
            errCode = "61";
        }
        airSaleCfmVo.setReturnCode(returnCode);
        airSaleCfmVo.setErrCode(errCode);
        airSaleCfmVo.setSysRefNo(airSaleCfmVo.getWaterNo());
        //消息生成时间
        airSaleCfmVo.setMsgGenTime(DateHelper.currentTodToString());
    }

    private void writeBackMsg(AirSaleCfmVo airSaleCfmVo) {
        // 返回消息
        MessageQueue backMesg = getMessageQueue(airSaleCfmVo);
//        try {
//            LogUtil.writeRecvSendLog(null, "localhost", "1",//0:请求消息、1：响应消息
//                    airSaleCfmVo.getMessageId(), messageSequ, backMesg.getMessage(),
//                    "0", getOLDbHelper());//0：成功1：失败
//        } catch (Exception ex) {
//            logger.error("记录日志发生异常：", ex);
//        }
        sendMsgToConnectionQueue(backMesg);
    }
    
    private MessageQueue getMessageQueue(AirSaleCfmVo airSaleCfmVo) {
        byte[] msgVo = new ConstructMessage41().constructMessage(airSaleCfmVo);
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
    
    /*
    更新日统计表
    */
    private void updateStsAcc(AirSaleCfmVo airSaleCfmVo) {
        Map<String,Object> valueMap = new HashMap<>();
        Map<String,Object> whereMap = new HashMap<>();
        valueMap.put("air_sale_num", 1);
        whereMap.put("squad_day", airSaleCfmVo.getDealTime().substring(0, 8));
        whereMap.put("line_id", airSaleCfmVo.getTerminaNo().substring(0, 2));
        whereMap.put("station_id", airSaleCfmVo.getTerminaNo().substring(2, 4));
        PubDao.olStsAcc(valueMap, whereMap);
    }
}
