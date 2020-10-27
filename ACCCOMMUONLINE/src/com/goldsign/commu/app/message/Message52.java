package com.goldsign.commu.app.message;

import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.dao.Message52Dao;
import com.goldsign.commu.app.dao.SeqDao;
import com.goldsign.commu.app.util.EncryptorJMJUtil;
import com.goldsign.commu.app.vo.EncryptorVo;
import com.goldsign.commu.app.vo.Message52Vo;
import com.goldsign.commu.app.vo.Message62Vo;
import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.dao.PubDao;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.exception.MessageException;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.LogUtil;
import com.goldsign.commu.frame.vo.MessageQueue;
import java.util.HashMap;
import java.util.Map;

/**
 * 充值确认
 * @author zhangjh
 */
public class Message52 extends MessageValidate {

    private final static byte[] LOCK = new byte[0];
    private static Logger logger = Logger.getLogger(Message51.class.getName());
    private static final String tranType = "14";
    /**
     * 序列标签
     */
    private static final String SEQ_LABLE = FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_chg_plus";

    @Override
    public void run() throws Exception {
        // 充值确认规定的数据区字节长度
        fix_recv_data_length = 150;
        buss_type_val = "14";
        process();
    }

    public void process() throws Exception {
        synchronized (LOCK) {
            processMessage();
        }
    }

    public void processMessage() throws Exception {

        EncryptorVo encryptorVo = new EncryptorVo();
        try {
            logger.info("--充值确认开始--");
            // 校验数据区长度
            validateDataLen();

            // 获取客户端的消息
            Message52Vo msg52Vo = getMessage52Vo(SEQ_LABLE);
            // 校验信息
            validateMsg(msg52Vo);
            Message52Dao msDao = new Message52Dao();

            // 请求数据入库 status= 2
            msDao.insert(msg52Vo, getOLDbHelper());
            
            // 校验
            validateMessage52(msg52Vo, msDao, encryptorVo);
            if(encryptorVo.getReturnCode().equals("00")){
                // 校验TAC
                validateTAC(msg52Vo, encryptorVo);
            }
            
            // 回写的传入的系统参照号
            Message62Vo msg62Vo = constructMessage62Vo(msg52Vo, encryptorVo.getReturnCode(), encryptorVo.getErrCode());
            // 入库
            msDao.updateMsg(msg62Vo, getOLDbHelper());
            // 更新统计表w_ol_sts_acc
            if("00".equals(msg62Vo.getReturnCode())){
                updateStsAcc(msg52Vo);
            }
            writeBackMsg(msg62Vo);
            logger.info("--充值确认响应结束--");
        } catch (Exception e) {
            logger.error("处理52消息出现异常:", e);
            // 出现异常返回响应的错误信息给终端
            dealException(e);
            throw e;
        } finally {
            if (null != getOLDbHelper()) {
                getOLDbHelper().close();
            }
        }
    }

    private Message52Vo getMessage52Vo(String sqlLable) throws Exception {
        Message52Vo msg52Vo = new Message52Vo();
        // 系统参照号
        long waterNo = SeqDao.getInstance().selectNextSeq(sqlLable,
                getOLDbHelper());
        msg52Vo.setWaterNo(waterNo);
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
        // TAC
        String tac = getCharString(124, 8);
        // 写卡结果
        String writeRslt = getCharString(132, 1);
        // 操作员编码
        String operatorId = getCharString(133, 6);
        // 系统参照号
        long sysRefNo = getLong2(139);
        // 系统时间
        String sysdate = getBcdString(143, 7);

        msg52Vo.setMessageId(messageId);
        msg52Vo.setMsgGenTime(msgGenTime);
        msg52Vo.setTerminalNo(terminalNo);
        msg52Vo.setSamLogicalId(samLogicalId);
        msg52Vo.setTransationSeq(transationSeq);
        msg52Vo.setBranchesCode(branchesCode);
        msg52Vo.setPubMainCode(pubMainCode);
        msg52Vo.setPubSubCode(pubSubCode);
        msg52Vo.setCardType(cardType);
        msg52Vo.setTkLogicNo(tkLogicNo.trim());
        msg52Vo.setTkPhyNo(tkPhyNo.trim());
        msg52Vo.setIsTestTk(isTestTk);
        msg52Vo.setOnlTranTimes(onlTranTimes);
        msg52Vo.setOfflTranTimes(offlTranTimes);
        msg52Vo.setBussType(bussType);
        msg52Vo.setValueType(valueType);
        msg52Vo.setChargeFee(chargeFee);
        msg52Vo.setBalance(balance);
        msg52Vo.setTac(tac);
        msg52Vo.setWriteRslt(writeRslt);
        msg52Vo.setOperatorId(operatorId);
        msg52Vo.setSysRefNo(sysRefNo);
        msg52Vo.setSysdate(sysdate);
        return msg52Vo;
    }

    private Message62Vo constructMessage62Vo(Message52Vo msg52Vo,
            String returnCode, String errCode) {
        Message62Vo msg62Vo = new Message62Vo();
        msg62Vo.setMessageId("62");
        msg62Vo.setReturnCode(returnCode);
        msg62Vo.setErrCode(errCode);
        // 消息生成时间
        msg62Vo.setMsgGenTime(DateHelper.dateToString(new Date()));
        if (null != msg52Vo) {
            msg62Vo.setTerminalNo(msg52Vo.getTerminalNo());
            msg62Vo.setSamLogicalId(msg52Vo.getSamLogicalId());
            msg62Vo.setTransationSeq(msg52Vo.getTransationSeq());
            msg62Vo.setTkLogicNo(msg52Vo.getTkLogicNo());
            // 回写传入的系统参照号
            msg62Vo.setSysRefNo(msg52Vo.getSysRefNo());
            msg62Vo.setWaterNo(msg52Vo.getWaterNo());
        }
        return msg62Vo;
    }

    private MessageQueue getMessageQueue(Message62Vo msg62Vo) {
        byte[] msg = new ConstructMessage62().constructMessage(msg62Vo);

        MessageQueue messageQueue = new MessageQueue();
        messageQueue.setMessageTime(new Date(System.currentTimeMillis()));
        messageQueue.setIpAddress(this.messageFrom);
        messageQueue.setMessage(msg);
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

    /**
     * 校验是否充值
     * @param msg52Vo Message52Vo
     * @param msDao Message52Dao
     * @throws SQLException SQLException
     * @throws MessageException MessageException
     */
    private void validateMessage52(Message52Vo msg52Vo, Message52Dao msDao, EncryptorVo encryptorVo)
            throws SQLException, MessageException {
        boolean result = msDao.query(msg52Vo, getOLDbHelper());
        if (!result) {
            logger.warn("没有查询到充值成功的记录");
            encryptorVo.setReturnCode("01");
            encryptorVo.setErrCode("61");
        }
    }

    private void writeBackMsg(Message62Vo msg62Vo) {
        // 返回消息
        MessageQueue backMesg = getMessageQueue(msg62Vo);
//        try {
//            LogUtil.writeRecvSendLog(null, "localhost", "1",
//                    msg62Vo.getMessageId(), messageSequ, backMesg.getMessage(),
//                    "0", getOLDbHelper());
//        } catch (Exception ex) {
//            logger.error("记录日志发生异常：", ex);
//        }
        sendMsgToConnectionQueue(backMesg);
    }

    private void dealException(Exception e) {
        String errCode = doException(e);
        Message62Vo msg62Vo = constructMessage62Vo(null, "01", errCode);
        writeBackMsg(msg62Vo);
    }
    
    private void validateTAC(Message52Vo msg52Vo, EncryptorVo encryptorVo) throws Exception {
        // 发给加密机的信息 取TAC
        encryptorVo.setDealTime(msg52Vo.getSysdate());
        encryptorVo.setSamLogicalId(msg52Vo.getSamLogicalId());
        encryptorVo.setBalance(msg52Vo.getBalance());
        encryptorVo.setChargeFee(msg52Vo.getChargeFee());
        encryptorVo.setOnlTranTimes(msg52Vo.getOnlTranTimes());
        encryptorVo.setCardLogicalId(msg52Vo.getTkLogicNo());
        EncryptorJMJUtil.getTac(encryptorVo);

        if(!msg52Vo.getTac().equals(encryptorVo.getMac())){
            encryptorVo.setReturnCode("01");
            encryptorVo.setErrCode("71");//数据安全验证失败
        }
    }

    
    /*
    更新日统计表
    */
    private void updateStsAcc(Message52Vo msg52Vo) {
        Map<String,Object> valueMap = new HashMap<>();
        Map<String,Object> whereMap = new HashMap<>();
        valueMap.put("lc_charge_fee", msg52Vo.getChargeFee()/100);
        valueMap.put("lc_charge_num", 1);
        whereMap.put("squad_day", msg52Vo.getSysdate().substring(0, 8));
        whereMap.put("line_id", msg52Vo.getTerminalNo().substring(0, 2));
        whereMap.put("station_id", msg52Vo.getTerminalNo().substring(2, 4));
        PubDao.olStsAcc(valueMap, whereMap);
    }
}
