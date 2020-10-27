package com.goldsign.commu.app.message;

import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.dao.Message51Dao;
import com.goldsign.commu.app.dao.SeqDao;
import com.goldsign.commu.app.util.EncryptorJMJUtil;
import com.goldsign.commu.app.vo.EncryptorVo;
import com.goldsign.commu.app.vo.Message51Vo;
import com.goldsign.commu.app.vo.Message61Vo;
import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.LogUtil;
import com.goldsign.commu.frame.vo.MessageQueue;

/**
 * 充值申请
 * @author zhangjh
 */
public class Message51 extends MessageValidate {

    private final static byte[] LOCK = new byte[0];
    private static Logger logger = Logger.getLogger(Message51.class.getName());
    /**
     * 交易类型标识
     */
    private static final String tranType = "14";
    /**
     * 序列标签
     */
    private static final String SEQ_LABLE = FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_chg_plus";

    @Override
    public void run() throws Exception {
        // 充值申请规定的数据区字节长度
        fix_recv_data_length = 169;
        buss_type_val = "14";
        process();
    }

    public void process() throws Exception {
        synchronized (LOCK) {
            processMessage();
        }
    }

    public void processMessage() throws Exception {
        // 交易时间
        String nowDate = DateHelper.dateToString(new Date());
        EncryptorVo encryptorVo = new EncryptorVo();
        try {
            logger.info("--充值请求开始--");
            // 校验数据区长度
            validateDataLen();

            // 获取客户端的消息
            Message51Vo msg51Vo = getMessage51Vo();
            Message51Dao msDao = new Message51Dao();

            // 校验信息
            validateMsg(msg51Vo);
            
            // 入库
            msDao.insert51Msg(msg51Vo, getOLDbHelper());

            // 校验MAC1
            validateMac1(msg51Vo, encryptorVo);
            if(encryptorVo.getReturnCode().equals("00")){
                // 发给加密机的信息 取mac2
                encryptorVo.setDealTime(nowDate);
                EncryptorJMJUtil.getMac2(encryptorVo);
            }else{
                nowDate = "00000000000000";// 如果充值失败全为0
                encryptorVo.setMac("00000000");
            }
            Message61Vo msg61Vo = buildMsg(encryptorVo.getErrCode(),encryptorVo.getReturnCode(),nowDate, encryptorVo.getMac(),msg51Vo);
            
            // 更新数据
            msDao.updateMsg(msg61Vo, getOLDbHelper());
            writeBackMsg(msg61Vo);
            logger.info("--充值请求响应结束--");
        } catch (Exception e) {
            logger.error("处理51消息出现异常", e);
            // 出现异常返回响应的错误信息给终端
            dealException(nowDate, e);
            throw e;
        } finally {
            if (null != getOLDbHelper()) {
                getOLDbHelper().close();
            }
        }

    }

    
    private MessageQueue getMessageQueue(Message61Vo msg61vo) {
        byte[] msg61 = new ConstructMessage61().constructMessage(msg61vo);
        MessageQueue messageQueue = new MessageQueue();
        messageQueue.setMessageTime(new Date(System.currentTimeMillis()));
        messageQueue.setIpAddress(this.messageFrom);
        messageQueue.setMessage(msg61);
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

    private Message51Vo getMessage51Vo() throws Exception {
        Message51Vo message51Vo = new Message51Vo();
        // 消息类型
        String messageId = getCharString(0, 2);

        // 消息生成时间
        String msgGenTime = getBcdString(2, 7);

        // 终端编号
        String terminalNo = getCharString(9, 9);
        // Sam卡逻辑卡号
        String samLogicalId = getCharString(18, 16);
        // 终端交易序列号
        long transationSeq = getLong2(34);
        // 网点编码
        String branchesCode = getCharString(38, 16);
        // 发行者主编码
        String pubMainCode = getCharString(54, 4);
        // 发行者子编码
        String pubSubCode = getCharString(58, 4);
        // 票卡类型
        String cardType = getBcdString(62, 2);
        // 票卡逻辑卡号
        String tkLogicNo = getCharString(64, 20);
        // 票卡物理卡号
        String tkPhyNo = getCharString(84, 20);
        // 是否测试卡
        String isTestTk = getCharString(104, 1);
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
        // 系统参照号
        long waterNo = SeqDao.getInstance().selectNextSeq(SEQ_LABLE,
                getOLDbHelper());
        message51Vo.setWaterNo(waterNo);
        message51Vo.setMessageId(messageId);
        message51Vo.setMsgGenTime(msgGenTime);
        message51Vo.setTerminalNo(terminalNo);
        message51Vo.setSamLogicalId(samLogicalId);
        message51Vo.setTransationSeq(transationSeq);
        message51Vo.setBranchesCode(branchesCode);
        message51Vo.setPubMainCode(pubMainCode);
        message51Vo.setPubSubCode(pubSubCode);
        message51Vo.setCardType(cardType);
        message51Vo.setTkLogicNo(tkLogicNo.trim());
        message51Vo.setTkPhyNo(tkPhyNo.trim());
        message51Vo.setIsTestTk(isTestTk);
        message51Vo.setOnlTranTimes(onlTranTimes);
        message51Vo.setOfflTranTimes(offlTranTimes);
        message51Vo.setBussType(bussType);
        message51Vo.setValueType(valueType);
        message51Vo.setChargeFee(chargeFee);
        message51Vo.setBalance(balance);
        message51Vo.setMac1(mac1);
        message51Vo.setTkChgeSeq(tkChgeSeq);
        message51Vo.setLastTranTermNo(lastTranTermNo);
        message51Vo.setLastDealTime(lastDealTime);
        message51Vo.setOperatorId(operatorId);
        return message51Vo;
    }

    
    private void writeBackMsg(Message61Vo msg61Vo) {
        // 返回消息
        MessageQueue backMesg = getMessageQueue(msg61Vo);
//        try {
//            LogUtil.writeRecvSendLog(null, "localhost", "1",
//                    msg61Vo.getMessageId(), messageSequ, backMesg.getMessage(),
//                    "0", getOLDbHelper());
//        } catch (Exception ex) {
//            logger.error("记录日志发生异常：", ex);
//        }
        sendMsgToConnectionQueue(backMesg);
    }

    /**
     *
     * @param errCode 错误描述
     * @param returnCode 响应码
     * @param sysdate 系统时间
     * @param mac mac
     * @param msg51vo 充值请求消息
     * @return 充值响应消息
     */
    private Message61Vo buildMsg(String errCode, String returnCode,
            String sysdate, String mac, Message51Vo msg51vo) {
        Message61Vo msg61Vo = new Message61Vo();
        msg61Vo.setErrCode(errCode);
        msg61Vo.setReturnCode(returnCode);
        msg61Vo.setMessageId("61");
        // 消息生成时间
        msg61Vo.setMsgGenTime(sysdate);
        // MAC
        msg61Vo.setMac2(mac);
        // 系统时间
        msg61Vo.setSysdate(sysdate);
        if (null != msg51vo) {
            msg61Vo.setTerminalNo(msg51vo.getTerminalNo());
            msg61Vo.setSamLogicalId(msg51vo.getSamLogicalId());
            msg61Vo.setTransationSeq(msg51vo.getTransationSeq());
            msg61Vo.setTkLogicNo(msg51vo.getTkLogicNo());
            msg61Vo.setWaterNo(msg51vo.getWaterNo());
            // 系统参照号 回写上传的系统参照号
            msg61Vo.setSysRefNo(msg51vo.getWaterNo());
        }
        return msg61Vo;
    }

    private void dealException(String nowDate, Exception e) {
        String errCode = doException(e);
        Message61Vo msg61Vo = buildMsg(errCode, "01", nowDate, "00000000", null);
        writeBackMsg(msg61Vo);
    }

    private void validateMac1(Message51Vo msg51Vo, EncryptorVo encryptorVo) throws Exception {
        // 发给加密机的信息 取mac1
        encryptorVo.setSamLogicalId(msg51Vo.getSamLogicalId());
        encryptorVo.setBalance(msg51Vo.getBalance());
        encryptorVo.setChargeFee(msg51Vo.getChargeFee());
        encryptorVo.setOnlTranTimes(msg51Vo.getOnlTranTimes());
        encryptorVo.setCardLogicalId(msg51Vo.getTkLogicNo());
        encryptorVo.setTkChgeSeq(msg51Vo.getTkChgeSeq());
        EncryptorJMJUtil.getMac1(encryptorVo);
        if(!msg51Vo.getMac1().equals(encryptorVo.getMac())){
            encryptorVo.setReturnCode("01");
            encryptorVo.setErrCode("70");//数据安全验证失败
        }
    }

}
