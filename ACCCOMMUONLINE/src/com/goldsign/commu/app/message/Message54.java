package com.goldsign.commu.app.message;

import java.sql.SQLException;
import java.util.Date;
import org.apache.log4j.Logger;

import com.goldsign.commu.app.dao.Message54Dao;
import com.goldsign.commu.app.dao.SeqDao;
import com.goldsign.commu.app.vo.EncryptorVo;
import com.goldsign.commu.app.vo.Message54Vo;
import com.goldsign.commu.app.vo.Message64Vo;
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
 * 充值撤销确认
 * @author zhangjh
 */
public class Message54 extends MessageValidate {

    private static Logger logger = Logger.getLogger(Message51.class.getName());
    /**
     * 序列标签
     */
    private static final String SEQ_LABLE = FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_chg_sub";
    private final static byte[] LOCK = new byte[0];

    @Override
    public void run() throws Exception {
        // 业务类型 固定填写‘18
        buss_type_val = "18";
        // 充值撤销确认规定的数据区字节长度
        fix_recv_data_length = 150;
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
            logger.info("--充值撤销确认开始--");
            // 校验数据区长度

            validateDataLen();
            // 获取客户端的消息
            Message54Vo msg54Vo = getMessage54Vo();
            // 校验信息
            retCode = validateMsg(msg54Vo);
            encryptorVo.setErrCode(retCode);
            encryptorVo.setReturnCode("00".equals(retCode)?retCode:"01");
            Message54Dao msg54Dao = new Message54Dao();
            // 请求数据入库 status= 2
            msg54Dao.insert(msg54Vo, getOLDbHelper());
            // 校验
            validateMessage54(msg54Vo, msg54Dao, encryptorVo);
            Message64Vo msg64Vo = constructMessage64Vo(msg54Vo, encryptorVo.getReturnCode(), encryptorVo.getErrCode());
            // 入库
            msg54Dao.updateMsg(msg64Vo, getOLDbHelper());
            // 更新统计表w_ol_sts_acc
            if("00".equals(msg64Vo.getReturnCode())){
                updateStsAcc(msg54Vo);
            }
            writeBackMsg(msg64Vo);
            logger.info("--充值确认响应结束--");
        } catch (Exception e) {
            logger.error("处理54消息出现异常", e);
            // 出现异常返回响应的错误信息给终端
            dealException(e);
        } finally {

            if (null != getOLDbHelper()) {
                getOLDbHelper().close();
            }
        }
    }

    private Message54Vo getMessage54Vo() throws Exception {
        Message54Vo msg54Vo = new Message54Vo();
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
        String tac = getCharString(124, 8);
        // 写卡结果
        String writeRslt = getCharString(132, 1);
        // 操作员编码

        String operatorId = getCharString(133, 6);
        // 系统参照号

        long sysRefNo = getLong2(139);
        // 系统参照号

        long waterNo = SeqDao.getInstance().selectNextSeq(SEQ_LABLE,
                getOLDbHelper());
        msg54Vo.setWaterNo(waterNo);
        // 系统时间
        String sysdate = getBcdString(143, 7);
        msg54Vo.setMessageId(messageId);
        msg54Vo.setMsgGenTime(msgGenTime);
        msg54Vo.setTerminalNo(terminalNo);
        msg54Vo.setSamLogicalId(samLogicalId);
        msg54Vo.setTransationSeq(transationSeq);
        msg54Vo.setBranchesCode(branchesCode);
        msg54Vo.setPubMainCode(pubMainCode);
        msg54Vo.setPubSubCode(pubSubCode);
        msg54Vo.setCardType(cardType);
        msg54Vo.setTkLogicNo(tkLogicNo);
        msg54Vo.setTkPhyNo(tkPhyNo);
        msg54Vo.setIsTestTk(isTestTk);
        msg54Vo.setOnlTranTimes(onlTranTimes);
        msg54Vo.setOfflTranTimes(offlTranTimes);
        msg54Vo.setBussType(bussType);
        msg54Vo.setValueType(valueType);
        msg54Vo.setChargeFee(chargeFee);
        msg54Vo.setBalance(balance);
        msg54Vo.setTac(tac);
        msg54Vo.setWriteRslt(writeRslt);
        msg54Vo.setOperatorId(operatorId);
        msg54Vo.setSysRefNo(sysRefNo);
        msg54Vo.setSysdate(sysdate);
        return msg54Vo;
    }

    private Message64Vo constructMessage64Vo(Message54Vo msg54Vo,
            String returnCode, String errCode) {
        Message64Vo msg64Vo = new Message64Vo();
        msg64Vo.setMessageId(String.valueOf(64));
        // 消息生成时间
        msg64Vo.setMsgGenTime(DateHelper.dateToString(new Date()));
        msg64Vo.setReturnCode(returnCode);
        msg64Vo.setErrCode(errCode);
        if (null != msg54Vo) {
            msg64Vo.setTerminalNo(msg54Vo.getTerminalNo());
            msg64Vo.setSamLogicalId(msg54Vo.getSamLogicalId());
            msg64Vo.setTransationSeq(msg54Vo.getTransationSeq());
            msg64Vo.setTkLogicNo(msg54Vo.getTkLogicNo());
            msg64Vo.setWaterNo(msg54Vo.getWaterNo());
            msg64Vo.setSysRefNo(msg54Vo.getSysRefNo());
        }
        return msg64Vo;
    }

    private MessageQueue getMessageQueue(Message64Vo msg64Vo) {
        byte[] msg = new ConstructMessage64().constructMessage(msg64Vo);
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

    private void dealException(Exception e) {
        String errCode = doException(e);
        Message64Vo msg64Vo = constructMessage64Vo(null, "01", errCode);
        writeBackMsg(msg64Vo);
    }

    private void writeBackMsg(Message64Vo msg64Vo) {
        // 返回消息
        MessageQueue backMesg = getMessageQueue(msg64Vo);
//        try {
//            LogUtil.writeRecvSendLog(null, "localhost", "1",
//                    msg64Vo.getMessageId(), messageSequ, backMesg.getMessage(),
//                    "0", getOLDbHelper());
//        } catch (Exception ex) {
//            logger.error("记录日志发生异常：", ex);
//        }

        sendMsgToConnectionQueue(backMesg);
    }

    private void validateMessage54(Message54Vo msg54Vo, Message54Dao msg54Dao, EncryptorVo encryptorVo)
            throws SQLException, MessageException {
        boolean result = msg54Dao.query(msg54Vo, getOLDbHelper());
        if (!result) {
            logger.warn("没有查询到撤销充值成功的记录");
            encryptorVo.setReturnCode("01");
            encryptorVo.setErrCode("61");
        }

    }


    /*
    更新日统计表
    */
    private void updateStsAcc(Message54Vo msg54Vo) {
        Map<String,Object> valueMap = new HashMap<>();
        Map<String,Object> whereMap = new HashMap<>();
        valueMap.put("lc_return_fee", msg54Vo.getChargeFee()/100);
        valueMap.put("lc_return_num", 1);
        whereMap.put("squad_day", msg54Vo.getSysdate().substring(0, 8));
        whereMap.put("line_id", msg54Vo.getTerminalNo().substring(0, 2));
        whereMap.put("station_id", msg54Vo.getTerminalNo().substring(2, 4));
        PubDao.olStsAcc(valueMap, whereMap);
    }
}
