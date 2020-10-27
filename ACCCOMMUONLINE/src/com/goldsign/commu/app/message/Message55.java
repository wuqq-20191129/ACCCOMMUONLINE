package com.goldsign.commu.app.message;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.dao.Message55Dao;
import com.goldsign.commu.app.dao.MessageValidateDao;
import com.goldsign.commu.app.dao.SeqDao;
import com.goldsign.commu.app.manager.ChgActProccessor;
import com.goldsign.commu.app.util.EncryptorJMJUtil;
import com.goldsign.commu.app.vo.EncryptorVo;
import com.goldsign.commu.app.vo.Message55Vo;
import com.goldsign.commu.app.vo.Message65Vo;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.exception.MessageException;
import com.goldsign.commu.frame.util.DateHelper;
import java.util.Date;

/**
 * 激活
 *
 * @author zhangjh
 */
public class Message55 extends MessageValidate {

    private final static byte[] LOCK = new byte[0];
    private static Logger logger = Logger.getLogger(Message55.class.getName());
    /**
     * 序列标签
     */
    private static final String SEQ_LABLE = FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_chg_activation";

    @Override
    public void run() throws Exception {
        // 激活请求规定的数据区字节长度
        fix_recv_data_length = 50;
        process();
    }

    public void process() throws Exception {
        synchronized (LOCK) {
            processMessage();
        }
    }

    public void processMessage() throws Exception {

        try {
            logger.info("--激活开始--");
            // 校验数据区长度

            validateDataLen();
            // 获取客户端的消息
            Message55Vo msg55Vo = getMessage55Vo();
            // 设备相关异常
            MessageValidateDao.isLegalDevice(msg55Vo);
            Message55Dao msg55Dao = new Message55Dao();

            // 激活信息入库
            msg55Dao.insert(msg55Vo, getOLDbHelper());
            // 发给加密机的信息
            EncryptorVo encryptorVo = new EncryptorVo();
            encryptorVo.setSamLogicalId(msg55Vo.getSamLogicalId());
            encryptorVo.setRandomNum(msg55Vo.getRandomNum());
            EncryptorJMJUtil.getPsamMac(encryptorVo);
            Message65Vo msg65Vo = buildMsg(encryptorVo.getErrCode(),encryptorVo.getReturnCode(),encryptorVo.getMac(),msg55Vo);
            // 入库
            msg55Dao.updateMsg(msg65Vo, getOLDbHelper());
            ChgActProccessor.getInstance().writeBackMsg(msg65Vo, messageSequ,
                    getOLDbHelper(), bridge);
            logger.info("--激活响应成功--");
        } catch (Exception e) {
            logger.error("处理55消息出现异常", e);
            ChgActProccessor.getInstance().dealException(this, e, messageSequ,
                    getOLDbHelper(), bridge);
        } finally {
            if (null != getOLDbHelper()) {
                getOLDbHelper().close();
            }
        }

    }

    private Message55Vo getMessage55Vo() throws Exception {
        Message55Vo msg55Vo = new Message55Vo();
        // 消息类型
        String messageId = getCharString(0, 2);
        // 消息生成时间
        String msgGenTime = getBcdString(2, 7);
        // 终端编号
        String terminalNo = getCharString(9, 9);
        String deviceType = terminalNo.substring(4, 6);
        // 非可激活设备返回错误消息
        if (!FrameCodeConstant.CHARGE_DEV_TYPE.containsKey(deviceType)) {
            logger.error("非可激活设备不能进行激活操作");
            throw new MessageException("60");
        }

        // Sam卡逻辑卡号
        String samLogicalId = getCharString(18, 16);
        // 随机数
        String randomNum = getCharString(34, 16);
        // 系统参照号
        long sysRefNo = SeqDao.getInstance().selectNextSeq(SEQ_LABLE,
                getOLDbHelper());
        msg55Vo.setWaterNo(sysRefNo);
        msg55Vo.setMessageId(messageId);
        msg55Vo.setMsgGenTime(msgGenTime);
        msg55Vo.setTerminalNo(terminalNo);
        msg55Vo.setSamLogicalId(samLogicalId);
        msg55Vo.setRandomNum(randomNum);
        return msg55Vo;
    }


    private Message65Vo buildMsg(String errCode, String returnCode,
            String mac, Message55Vo msg55Vo) {
        Message65Vo msgVo = new Message65Vo();
        msgVo.setErrCode(errCode);
        msgVo.setReturnCode(returnCode);
        // MAC
        msgVo.setMac(mac);

        // 消息类型
        msgVo.setMessageId("65");
        // 消息生成时间
        msgVo.setMsgGenTime(DateHelper.dateToString(new Date()));
        if (null != msg55Vo) {
            msgVo.setWaterNo(msg55Vo.getWaterNo());
            // 系统参照号
            msgVo.setSysRefNo(msg55Vo.getWaterNo());
            // 终端编号
            msgVo.setTerminalNo(msg55Vo.getTerminalNo());
            // Sam卡逻辑卡号
            msgVo.setSamLogicalId(msg55Vo.getSamLogicalId());
        }
        return msgVo;
    }

}