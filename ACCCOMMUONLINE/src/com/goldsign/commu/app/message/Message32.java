/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.message;

import com.goldsign.commu.app.dao.AirDao;
import com.goldsign.commu.app.dao.SeqDao;
import com.goldsign.commu.app.util.EncryptorJMJUtil;
import com.goldsign.commu.app.vo.AirChargeVo;
import com.goldsign.commu.app.vo.EncryptorVo;
import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.LogUtil;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import com.goldsign.commu.frame.vo.MessageQueue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author lind
 * 空中充值申请
 * @datetime 2017-12-6
 */
public class Message32 extends MessageValidate {

    private final static byte[] LOCK = new byte[0];
    private static Logger logger = Logger.getLogger(Message32.class.getName());
    /**
     * 交易类型标识
     */
    private static final String tranType = "14";
    //序列标签
    private static final String SEQ_LABLE = FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_air_charge";

    @Override
    public void run() throws Exception {
        //数据区字节长度
        fix_recv_data_length = 186;
        process();
    }

    public void process() throws Exception {
        synchronized (LOCK) {
            processMessage();
        }
    }

    private void processMessage() throws Exception {

        AirChargeVo airChargeVo = new AirChargeVo();
        try {
//            logger.info("-- 解析空中充值申请消息32 --");
            //汇总日志为1条输出 20190911 zhongzq
            ArrayList logList = new ArrayList();
            logList.add("空中充值申请业务32消息");
            ThreadLocalUtil.LOG_THREAD_LOCAL.set(logList);
            // 校验数据区长度
            validateDataLen();

            //从消息中获取信息
            getDataVo(airChargeVo);

            // 入库
            AirDao airDao = new AirDao();
            airDao.insertAirCharge(airChargeVo, getOLDbHelper());

            //整合返回消息
            constructMessageVo(airChargeVo);
            //入库和发送响应消息42
            //modiy by zhongziqi 20190809 优化逻辑 如果发送后下面数据库异常则会发送两条报文
//            writeBackMsg(airChargeVo);
            airDao.insertAirChargeResp(airChargeVo, getOLDbHelper());
            writeBackMsg(airChargeVo);
//            logger.info("--空中充值请求响应结束--");
            logger.info(new ThreadLocalUtil().ArrayToString(logList, true));
        } catch (Exception e) {
//            logger.error("处理32消息出现异常", e);
            // 出现异常返回响应的错误信息给终端
            dealException(airChargeVo, e);
            logger.error(new ThreadLocalUtil().ArrayToString(ThreadLocalUtil.LOG_THREAD_LOCAL.get(), false) + "处理32消息出现异常", e);

            throw e;
        } finally {
            if (null != getOLDbHelper()) {
                getOLDbHelper().close();
            }
        }

    }

    private void dealException(AirChargeVo airChargeVo, Exception e) {
        String errCode = doException(e);
        airChargeVo.setReturnCode("01");
        airChargeVo.setErrCode(errCode);
        //消息生成时间
        airChargeVo.setMsgGenTime(DateHelper.currentTodToString());

        writeBackMsg(airChargeVo);
    }

    private void getDataVo(AirChargeVo airChargeVo) throws Exception {
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
        // MAC1
        String mac1 = getCharString(124, 8);
        // 卡片充值随机数
        String tkChgeSeq = getCharString(132, 8);
        // 上次交易终端编号
        String lastTranTermNo = getCharString(140, 16);
        // 上次交易时间
        String lastDealTime = getBcdString(156, 7);
        // 操作员编码
        String operatorId = getCharString(163, 6);
        // 手机号
        String phoneNo = getCharString(169, 11);
        //充值渠道类型
        String paidChannelType = getCharString(180, 2);
        //充值渠道代码
        String paidChannelCode = getCharString(182, 4);

        // acc流水号
        long waterNo = SeqDao.getInstance().selectNextSeq(SEQ_LABLE, getOLDbHelper());
        airChargeVo.setWaterNo(waterNo);
        airChargeVo.setMessageId(messageId);
        airChargeVo.setMsgGenTime(msgGenTime);
        airChargeVo.setTerminaNo(terminaNo);
        airChargeVo.setSamLogicalId(samLogicalId);
        airChargeVo.setTerminaSeq(terminaSeq);
        airChargeVo.setBranchesCode(branchesCode);
        airChargeVo.setCardType(cardType);
        airChargeVo.setPhoneNo(phoneNo);
        airChargeVo.setIssMainCode(issMainCode);
        airChargeVo.setIssSubCode(issSubCode);
        airChargeVo.setSysRefNo(waterNo);
        airChargeVo.setCardLogicalId(cardLogicalId);
        airChargeVo.setPaidChannelCode(paidChannelCode);
        airChargeVo.setPaidChannelType(paidChannelType);
        airChargeVo.setOperatorId(operatorId);
        airChargeVo.setCardPhyId(cardPhyId);
        airChargeVo.setIsTestFlag(isTestFlag);
        airChargeVo.setBussType(bussType);
        airChargeVo.setValueType(valueType);
        airChargeVo.setChargeFee(chargeFee);
        airChargeVo.setBalance(balance);
        airChargeVo.setOfflTranTimes(offlTranTimes);
        airChargeVo.setOnlTranTimes(onlTranTimes);
        airChargeVo.setLastTranTermno(lastTranTermNo);
        airChargeVo.setLastTranTime(lastDealTime);
        airChargeVo.setTkChgeSeq(tkChgeSeq);
        airChargeVo.setMac1(mac1);
        airChargeVo.setSysRefNo(waterNo);

        //add by zhongzq 20190919
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add(airChargeVo.toString());
    }


    private void constructMessageVo(AirChargeVo airChargeVo) {
        EncryptorVo encryptorVo = new EncryptorVo();
        String sysTime = DateHelper.currentTodToString();
        encryptorVo.setDealTime(sysTime);
        encryptorVo.setSamLogicalId(airChargeVo.getSamLogicalId());
        encryptorVo.setBalance(airChargeVo.getBalance());
        encryptorVo.setChargeFee(airChargeVo.getChargeFee());
        encryptorVo.setOnlTranTimes(airChargeVo.getOnlTranTimes());
        encryptorVo.setCardLogicalId(airChargeVo.getCardLogicalId());
        encryptorVo.setTkChgeSeq(airChargeVo.getTkChgeSeq());

        try {
            //增加校验票种 36 卡不支持充值 20190828 zhongzq
            if (PubUtil.validAllowChangeTicketType(airChargeVo.getCardType())) {
                //校验mac1
                validateMac1(airChargeVo, encryptorVo);

                if (encryptorVo.getReturnCode().equals("00")) {
                    // 发给加密机的信息 取mac2
                    EncryptorJMJUtil.getMac2(encryptorVo);
                } else {
                    sysTime = "00000000000000";// 如果充值失败全为0
                    encryptorVo.setMac("00000000");
                }
            }else {
                ThreadLocalUtil.LOG_THREAD_LOCAL.get().add(airChargeVo.getCardType()+"不支持充值");
                encryptorVo.setErrCode("01");
                //卡不支持充值
                encryptorVo.setErrCode("36");
            }
        } catch (Exception e) {
            encryptorVo.setReturnCode("01");
            encryptorVo.setErrCode("14");
            logger.error("加密机异常：" + e.getMessage());
            ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("加密机异常：" + e.getMessage());
        }

        airChargeVo.setDealTime(sysTime);
        airChargeVo.setMac2(encryptorVo.getMac());
        airChargeVo.setReturnCode(encryptorVo.getReturnCode());
        airChargeVo.setErrCode(encryptorVo.getErrCode());
        //消息生成时间
        airChargeVo.setMsgGenTime(DateHelper.currentTodToString());
    }



    private void writeBackMsg(AirChargeVo airChargeVo) {
        // 返回消息
        MessageQueue backMesg = getMessageQueue(airChargeVo);
//        try {
//            LogUtil.writeRecvSendLog(null, "localhost", "1",//0:请求消息、1：响应消息
//                    airChargeVo.getMessageId(), messageSequ, backMesg.getMessage(),
//                    "0", getOLDbHelper());//0：成功1：失败
//        } catch (Exception ex) {
//            logger.error("记录日志发生异常：", ex);
//        }
        sendMsgToConnectionQueue(backMesg);
    }

    private MessageQueue getMessageQueue(AirChargeVo airChargeVo) {
        byte[] msgVo = new ConstructMessage42().constructMessage(airChargeVo);
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


    private void validateMac1(AirChargeVo airChargeVo, EncryptorVo encryptorVo) throws Exception {
        // 发给加密机的信息 取mac1
        EncryptorJMJUtil.getMac1(encryptorVo);
        if (!airChargeVo.getMac1().equals(encryptorVo.getMac())) {
            encryptorVo.setReturnCode("01");
            encryptorVo.setErrCode("70");//数据安全验证失败
        }
    }
}
