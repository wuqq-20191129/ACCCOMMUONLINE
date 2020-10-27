package com.goldsign.commu.app.message;

import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.dao.Message53Dao;
import com.goldsign.commu.app.dao.SeqDao;
import com.goldsign.commu.app.vo.EncryptorVo;
import com.goldsign.commu.app.vo.Message53Vo;
import com.goldsign.commu.app.vo.Message63Vo;
import com.goldsign.commu.frame.connection.CommuConnection;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.LogUtil;
import com.goldsign.commu.frame.vo.MessageQueue;

/**
 * 充值撤销申请
 * @author zhangjh
 */
public class Message53 extends MessageValidate {

    private final static byte[] LOCK = new byte[0];
    private static Logger logger = Logger.getLogger(Message51.class.getName());
    /**
     * 序列标签
     */
    private static final String SEQ_LABLE = FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_chg_sub";

    @Override
    public void run() throws Exception {
        // 充值撤销申请规定的数据区字节长度
        fix_recv_data_length = 153;
        // 业务类型 固定填写‘18
        buss_type_val = "18";
        process();
    }

    public void process() throws Exception {
        synchronized (LOCK) {
            processMessage();
        }
    }

    public void processMessage() throws Exception {
        EncryptorVo encryptorVo = new EncryptorVo();
        String retCode = "00";
        try {
            logger.info("--充值撤销申请开始--");
            // 校验数据区长度

            validateDataLen();
            // 获取客户端的消息
            Message53Vo msg53Vo = getMessage53Vo();
            // 校验信息
            retCode = validateMsg(msg53Vo);
            encryptorVo.setErrCode(retCode);
            encryptorVo.setReturnCode("00".equals(retCode)?retCode:"01");
            // 请求数据入库 status= 1
            Message53Dao msgDao = new Message53Dao();
            msgDao.insert(msg53Vo, getOLDbHelper());
            // 查询数据库是否充值记录（终端交易序列号-1） validateMsg(msg53Vo)存储过程已经校验
//            validateMessage53(msg53Vo, msgDao, encryptorVo);
            // 校验，终端是否激活，并在有效期??

            Message63Vo msg63Vo = constructMessage63Vo(msg53Vo, encryptorVo.getReturnCode(), encryptorVo.getErrCode());
            // 入库
            msgDao.updateMsg(msg63Vo, getOLDbHelper());
            writeBackMsg(msg63Vo);
            logger.info("--充值撤销申请结束--");
        } catch (Exception e) {
            logger.error("处理53消息出现异常", e);
            // 出现异常返回响应的错误信息给终端
            dealException(e);
            throw e;
        } finally {

            if (null != getOLDbHelper()) {
                getOLDbHelper().close();
            }
        }
    }

    private Message53Vo getMessage53Vo() throws Exception {
        Message53Vo msg53Vo = new Message53Vo();
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
        // 上次交易终端编号
        String lastTranTermNo = getCharString(124, 16);
        // 上次交易时间
        String lastDealTime = getBcdString(140, 7);
        // 操作员编码
        String operatorId = getCharString(147, 6);
        // 系统参照号
        long waterNo = SeqDao.getInstance().selectNextSeq(SEQ_LABLE,
                getOLDbHelper());
        msg53Vo.setWaterNo(waterNo);
        msg53Vo.setMessageId(messageId);
        msg53Vo.setMsgGenTime(msgGenTime);
        msg53Vo.setTerminalNo(terminalNo);
        msg53Vo.setSamLogicalId(samLogicalId.trim());
        msg53Vo.setTransationSeq(transationSeq);
        msg53Vo.setBranchesCode(branchesCode);
        msg53Vo.setPubMainCode(pubMainCode);
        msg53Vo.setPubSubCode(pubSubCode);
        msg53Vo.setCardType(cardType);
        msg53Vo.setTkLogicNo(tkLogicNo.trim());
        msg53Vo.setTkPhyNo(tkPhyNo.trim());
        msg53Vo.setIsTestTk(isTestTk);
        msg53Vo.setOnlTranTimes(onlTranTimes);
        msg53Vo.setOfflTranTimes(offlTranTimes);
        msg53Vo.setBussType(bussType);
        msg53Vo.setValueType(valueType);
        msg53Vo.setChargeFee(chargeFee);
        msg53Vo.setBalance(balance);
        msg53Vo.setLastTranTermNo(lastTranTermNo);
        msg53Vo.setLastDealTime(lastDealTime);
        msg53Vo.setOperatorId(operatorId);
        
//        logger.info(msg53Vo.toString());
        return msg53Vo;
    }

    private Message63Vo constructMessage63Vo(Message53Vo msg53Vo,
            String returnCode, String errCode) {
        // 返回的消息生成时间、系统时间
        String nowDate = DateHelper.dateToString(new Date());
        Message63Vo msg63Vo = new Message63Vo();

        msg63Vo.setMessageId("63");
        // 消息生成时间
        msg63Vo.setMsgGenTime(nowDate);
        msg63Vo.setSysdate(nowDate);
        msg63Vo.setReturnCode(returnCode);
        msg63Vo.setErrCode(errCode);
        if (null != msg53Vo) {
            // 系统参照号
            msg63Vo.setSysRefNo(msg53Vo.getWaterNo());
            msg63Vo.setWaterNo(msg53Vo.getWaterNo());
            msg63Vo.setTerminalNo(msg53Vo.getTerminalNo());
            msg63Vo.setSamLogicalId(msg53Vo.getSamLogicalId());
            msg63Vo.setTransationSeq(msg53Vo.getTransationSeq());
            msg63Vo.setTkLogicNo(msg53Vo.getTkLogicNo());
        }
        return msg63Vo;
    }

    private MessageQueue getMessageQueue(Message63Vo msg63Vo) {
        byte[] msg = new ConstructMessage63().constructMessage(msg63Vo);

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

    private void writeBackMsg(Message63Vo msg63Vo) {
        // 返回消息
        MessageQueue backMesg = getMessageQueue(msg63Vo);
//        try {
//            LogUtil.writeRecvSendLog(null, "localhost", "1",
//                    msg63Vo.getMessageId(), messageSequ, backMesg.getMessage(),
//                    "0", getOLDbHelper());
//        } catch (Exception ex) {
//            logger.error("记录日志发生异常：" + ex);
//        }
        sendMsgToConnectionQueue(backMesg);
    }

    private void dealException(Exception e) {
        String errCode = doException(e);
        Message63Vo msg63Vo = constructMessage63Vo(null, "01", errCode);
        writeBackMsg(msg63Vo);
    }

    /**
     * 校验是否充值
     * @param msg53Vo
     * @param msgDao
     * @throws SQLException SQLException
     */
//    private void validateMessage53(Message53Vo msg53Vo, Message53Dao msgDao, 
//            EncryptorVo encryptorVo) throws SQLException{
//        boolean result = msgDao.query(msg53Vo, getSTDbHelper());
//        if (!result) {
//            logger.warn("没有查询到充值确认的记录,终端交易序列号:"+ String.valueOf(msg53Vo.getTransationSeq()-1));
//            encryptorVo.setReturnCode("01");
//            encryptorVo.setErrCode("61");
//        }
//    }
}
