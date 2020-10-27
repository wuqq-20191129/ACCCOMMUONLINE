/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.message;

import com.goldsign.commu.app.dao.AirDao;
import com.goldsign.commu.app.dao.SeqDao;
import com.goldsign.commu.app.util.EncryptorJMJUtil;
import com.goldsign.commu.app.vo.AirChargeCfmVo;
import com.goldsign.commu.app.vo.EncryptorVo;
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
 * @datetime 2018-07-02
 * @author lind
 * 空中充值撤销确认
 */
public class Message35 extends MessageValidate{
    
    private final static byte[] LOCK = new byte[0];
    private static Logger logger = Logger.getLogger(Message35.class.getName());
    private static final String tranType = "18";
    //序列标签
    private static final String SEQ_LABLE = FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_air_charge_cl_cfm";

    @Override
    public void run() throws Exception {
        //数据区字节长度
        fix_recv_data_length = 154;
        process();
    }
    
    public void process() throws Exception {
        synchronized (LOCK) {
            processMessage();
        }
    }

    private void processMessage() throws Exception {
        
        AirChargeCfmVo airChargeCfmVo = new AirChargeCfmVo();
        try {
//            logger.info("-- 解析空中充值撤销确认消息35 --");
            ArrayList logList = new ArrayList();
            logList.add("空中充值撤销确认35消息");
            ThreadLocalUtil.LOG_THREAD_LOCAL.set(logList);
            // 校验数据区长度
            validateDataLen();
            
            //从消息中获取信息
            getDataVo(airChargeCfmVo);
            
            // 入库
            AirDao airDao = new AirDao();
            airDao.insertAirChargeCancelCfm(airChargeCfmVo, getOLDbHelper());
            
            //整合返回消息
            constructMessageVo(airChargeCfmVo);
            //入库和发送响应消息45
            //modiy by zhongziqi 20190809 优化逻辑 如果发送后下面数据库异常则会发送两条报文
//            writeBackMsg(airChargeCfmVo);
            airDao.insertAirChargeCLCfmResp(airChargeCfmVo, getOLDbHelper());
            // 更新统计表w_ol_sts_acc
            if("00".equals(airChargeCfmVo.getReturnCode())){
                updateStsAcc(airChargeCfmVo);
            }
            writeBackMsg(airChargeCfmVo);
//            logger.info("--空中充值撤销确认结束--");
            logger.info(new ThreadLocalUtil().ArrayToString(logList, true));
        } catch (Exception e) {
//            logger.error("处理35消息出现异常", e);
            // 出现异常返回响应的错误信息给终端
            dealException(airChargeCfmVo, e);
            logger.error(new ThreadLocalUtil().ArrayToString(ThreadLocalUtil.LOG_THREAD_LOCAL.get(), false) + "处理35消息出现异常", e);

            throw e;
        } finally {
            if (null != getOLDbHelper()) {
                getOLDbHelper().close();
            }
        }

    }
    
    private void dealException(AirChargeCfmVo airChargeCfmVo, Exception e) {
        String errCode = doException(e);
        airChargeCfmVo.setReturnCode("01");
        airChargeCfmVo.setErrCode(errCode);
        //消息生成时间
        airChargeCfmVo.setMsgGenTime(DateHelper.currentTodToString());

        writeBackMsg(airChargeCfmVo);
    }

    private void getDataVo(AirChargeCfmVo airChargeCfmVo) throws Exception  {
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
        // 票卡类型
        String cardType = getBcdString(62, 2);
        //票卡逻辑卡号
        String cardLogicalId = getCharString(64, 20);
        //票卡物理卡号
        String cardPhyId = getCharString(84, 20);
        //空充系统参照号
        long sysRefNoChr = getLong2(104);
        // 是否测试卡
        String isTestFlag = getCharString(104, 1);
        // 票卡联机交易计数
        long onlTranTimes = getLong2(105);
        // 票卡脱机交易计数
        long offlTranTimes = getLong2(109);
        // 业务类型
        String bussType = getCharString(113, 2);
        // 值类型
        String valueType = getCharString(115, 1);
        // 充值金额
        long chargeFee = getLong2(116);
        // 票卡余额
        long balance = getLong2(120);
        // TAC
        String tac = getCharString(124, 8);
        // 写卡结果
        String resultCode = getCharString(132, 1);
        // 操作员编码
        String operatorId = getCharString(133, 6);
        // 系统参照号
        long sysRefNo = getLong2(139);
        //系统时间
        String dealTime = getBcdString(143, 7);
        
        // acc流水号
        long waterNo = SeqDao.getInstance().selectNextSeq(SEQ_LABLE, getOLDbHelper());
        airChargeCfmVo.setWaterNo(waterNo);
        airChargeCfmVo.setMessageId(messageId);
        airChargeCfmVo.setMsgGenTime(msgGenTime);
        airChargeCfmVo.setTerminaNo(terminaNo);
        airChargeCfmVo.setSamLogicalId(samLogicalId);
        airChargeCfmVo.setTerminaSeq(terminaSeq);
        airChargeCfmVo.setBranchesCode(branchesCode);
        airChargeCfmVo.setCardType(cardType);
        airChargeCfmVo.setIssMainCode(issMainCode);
        airChargeCfmVo.setIssSubCode(issSubCode);
        airChargeCfmVo.setSysRefNo(waterNo);
        airChargeCfmVo.setCardLogicalId(cardLogicalId);
        airChargeCfmVo.setOperatorId(operatorId);
        airChargeCfmVo.setCardPhyId(cardPhyId);
        airChargeCfmVo.setIsTestFlag(isTestFlag);
        airChargeCfmVo.setBussType(bussType);
        airChargeCfmVo.setValueType(valueType);
        airChargeCfmVo.setChargeFee(chargeFee);
        airChargeCfmVo.setBalance(balance);
        airChargeCfmVo.setOfflTranTimes(offlTranTimes);
        airChargeCfmVo.setOnlTranTimes(onlTranTimes);
        airChargeCfmVo.setTac(tac);
        airChargeCfmVo.setResultCode(resultCode);
        airChargeCfmVo.setOperatorId(operatorId);
        airChargeCfmVo.setSysRefNo(sysRefNo);
        airChargeCfmVo.setDealTime(dealTime);
        airChargeCfmVo.setSysRefNoChr(sysRefNoChr);
        //add by zhongzq 20190919
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add(airChargeCfmVo.toString());
    }


    private void constructMessageVo(AirChargeCfmVo airChargeCfmVo) {
        EncryptorVo encryptorVo = new EncryptorVo();
        boolean result = true;
        
        // 查询空充撤销申请响应记录
        AirDao airDao = new AirDao();
        try {
            result = airDao.checkAirChargeCancel(airChargeCfmVo, getOLDbHelper());
        } catch (SQLException ex) {
            logger.error("查询空充撤销申请响应数据库记录异常：", ex);
            encryptorVo.setReturnCode("01");
            encryptorVo.setErrCode("12");
        }
        if(!result){
            encryptorVo.setReturnCode("01");
            encryptorVo.setErrCode("61");
        }
        
        //响应码与写卡结果
        if(encryptorVo.getReturnCode().equals("00") && airChargeCfmVo.getResultCode().equals("0")){
            //校验tac
            try {
                validateTAC(airChargeCfmVo, encryptorVo);
            } catch (Exception e) {
                encryptorVo.setReturnCode("01");
                encryptorVo.setErrCode("14");
                logger.error("加密机异常："+e.getMessage());
            }           
        }
        //判断写卡结果不成功时
        if(!airChargeCfmVo.getResultCode().equals("0")){
            encryptorVo.setReturnCode("01");
            encryptorVo.setErrCode("36");
        }
        
        airChargeCfmVo.setReturnCode(encryptorVo.getReturnCode());
        airChargeCfmVo.setErrCode(encryptorVo.getErrCode());
        airChargeCfmVo.setSysRefNo(airChargeCfmVo.getWaterNo());
        //消息生成时间
        airChargeCfmVo.setMsgGenTime(DateHelper.currentTodToString());
    }

    private void writeBackMsg(AirChargeCfmVo airChargeCfmVo) {
        // 返回消息
        MessageQueue backMesg = getMessageQueue(airChargeCfmVo);
//        try {
//            LogUtil.writeRecvSendLog(null, "localhost", "1",//0:请求消息、1：响应消息
//                    airChargeCfmVo.getMessageId(), messageSequ, backMesg.getMessage(),
//                    "0", getOLDbHelper());//0：成功1：失败
//        } catch (Exception ex) {
//            logger.error("记录日志发生异常：", ex);
//        }
        sendMsgToConnectionQueue(backMesg);
    }
    
    private MessageQueue getMessageQueue(AirChargeCfmVo airChargeCfmVo) {
        byte[] msgVo = new ConstructMessage45().constructMessage(airChargeCfmVo);
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
    
    private void validateTAC(AirChargeCfmVo airChargeCfmVo, EncryptorVo encryptorVo) throws Exception {
        // 发给加密机的信息 取TAC
        encryptorVo.setDealTime(airChargeCfmVo.getDealTime());
        encryptorVo.setSamLogicalId(airChargeCfmVo.getSamLogicalId());
        encryptorVo.setBalance(airChargeCfmVo.getBalance());
        encryptorVo.setChargeFee(airChargeCfmVo.getChargeFee());
        encryptorVo.setOnlTranTimes(airChargeCfmVo.getOnlTranTimes());
        encryptorVo.setCardLogicalId(airChargeCfmVo.getCardLogicalId());
        EncryptorJMJUtil.getTrxTac(encryptorVo,"06");//普通消费
        
        if(!airChargeCfmVo.getTac().equals(encryptorVo.getMac())){
            encryptorVo.setReturnCode("01");
            encryptorVo.setErrCode("71");//数据安全验证失败
        }
    }
    
    /*
    更新日统计表
    */
    private void updateStsAcc(AirChargeCfmVo airChargeCfmVo) {
        Map<String,Object> valueMap = new HashMap<>();
        Map<String,Object> whereMap = new HashMap<>();
        valueMap.put("air_return_fee", airChargeCfmVo.getChargeFee()/100);
        valueMap.put("air_return_num", 1);
        whereMap.put("squad_day", airChargeCfmVo.getDealTime().substring(0, 8));
        whereMap.put("line_id", airChargeCfmVo.getTerminaNo().substring(0, 2));
        whereMap.put("station_id", airChargeCfmVo.getTerminaNo().substring(2, 4));
        PubDao.olStsAcc(valueMap, whereMap);
    }
    
}
