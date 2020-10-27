package com.goldsign.commu.app.message;

import com.goldsign.commu.app.dao.ExceptionLogDao;
import com.goldsign.commu.app.dao.MessageValidateDao;
import com.goldsign.commu.app.vo.BaseVo;
import com.goldsign.commu.app.vo.RechargeReqVo;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.exception.CommuException;
import com.goldsign.commu.frame.exception.MessageException;
import com.goldsign.commu.frame.message.MessageBase;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;
import java.io.IOException;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author zhangjh
 */
public abstract class MessageValidate extends MessageBase {
    // 时间BCD码长度

    private static final String valifySql = "call " + FrameDBConstant.COM_OL_P + "up_ol_chg_verify(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";// 存储过程调用语句
    private static final int MSG_GEN_TIME_LENTH = 7;
    // 业务类型
    protected String buss_type_val = "00";
    // 消息类型
    protected int fix_recv_data_length = 0;
    private static Logger logger = Logger.getLogger(MessageValidate.class
            .getName());

    protected String validateMsg(RechargeReqVo reqVo) throws  Exception {
        String retCode = "00";
        // 校验特定参数
        retCode = validateParams(reqVo, MSG_GEN_TIME_LENTH, this.buss_type_val);
        // 设备、卡校验
        if ("00".equals(retCode)){
            retCode = validateDevCard(reqVo);
        }
        // 其他判断(金额相关异常\冲正对应的充值原始记录不存在)
        if ("00".equals(retCode)){
            retCode = validateAct(reqVo);
        }
        return retCode;
    }

    /**
     * 校验传入数据中一些特定数据的格式是否正确
     *
     * @param reqVo 充值请求消息
     *
     * @throws MessageException 异常消息
     */
    private static String validateParams(RechargeReqVo reqVo, int msgGenTime,
            String bussTypVal) throws MessageException {
        String retCode = "00";
        // 校验时间 长度，getBCD方法有问题,
        if (msgGenTime * 2 != reqVo.getMsgGenTime().length()) {
            String mess = "充值请求的消息生成时间格式不对,传入的时间为[" + reqVo.getMsgGenTime()
                    + "]，规定长度为14个字符";
            logger.warn(mess);
            retCode = "60";
//            throw new MessageException("60");
        }
        // 校验关键位子的数据是否正确：此处校验‘业务类型’是否正确，充值为14，撤销为18（冲正）
        if ("00".equals(retCode) && !reqVo.getBussType().equals(bussTypVal)) {
            logger.warn("终端传入的业务类型值不等于"+bussTypVal);
            retCode = "60";
//            throw new MessageException("60");
        }
        if ("00".equals(retCode) && null != reqVo.getIsTestTk()) {
            if (!"1".equals(reqVo.getIsTestTk())
                    && !"0".equals(reqVo.getIsTestTk())) {
                logger.warn("卡应用标志不正确："+reqVo.getIsTestTk());
                retCode = "60";
//                throw new MessageException("60");
            }
        }
        return retCode;
    }

    /**
     *
     * @param reqVo
     */
    private String validateAct(RechargeReqVo reqVo) throws SQLException,
            MessageException, Exception {
        // logger.info("设备激活校验");
        // Message55Dao msg55Dao = new Message55Dao();
        // int rows = msg55Dao.queryDeviceActStatus(reqVo, getOLDbHelper());
        // if (rows <= 0) {
        // String mess = "设备未激活或激活超过有效期";
        // logger.warn(mess);
        // throw new MessageException("23");
        // }

        String retCode = "00";// 存储过程参数返回的执行结果代码

        int[] pInIndexes = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
            16, 17, 18};// 存储过程输入参数索引列表
        Object[] pInStmtValues = {reqVo.getMessageId(), reqVo.getTerminalNo(),
            reqVo.getSamLogicalId(), reqVo.getTransationSeq(),
            reqVo.getBranchesCode(), reqVo.getPubMainCode(),
            reqVo.getPubSubCode(), reqVo.getCardType(),
            reqVo.getTkLogicNo(), reqVo.getTkPhyNo(), reqVo.getIsTestTk(),
            reqVo.getOnlTranTimes(), reqVo.getOfflTranTimes(),
            reqVo.getBussType(), reqVo.getValueType(),
            reqVo.getChargeFee(), reqVo.getBalance(),
            FrameCodeConstant.EffectiveDate};// 存储过程输入参数值

//        for (int i = 0; i < pInStmtValues.length; i++) {
//            logger.debug(pInStmtValues[i]);
//        }
        int[] pOutIndexes = {19};// 存储过程输出参数索引列表
        int[] pOutTypes = {DbHelper.PARAM_OUT_TYPE_VACHAR};// 存储过程输出参数值类型

        try {
            getOLDbHelper().runStoreProcForOracle(valifySql, pInIndexes,
                    pInStmtValues, pOutIndexes, pOutTypes);// 执行存储过程
            /*
             * 取输出参数值
             */
            retCode = getOLDbHelper().getOutParamStringValue(19);// 获取返回代码输出参数值

        } catch (IllegalArgumentException | IllegalStateException | SQLException e) {
            PubUtil.handleException(e, logger);
            retCode = "12";
        } finally {
            logger.warn("call " + FrameDBConstant.COM_OL_P + "up_ol_chg_verify,retCode is " + retCode);
            return retCode;
        }
    }

    public String doException(Exception e) {
        // 未知错误
        String errCode = "13";
        if (e instanceof SQLException) {
            logger.error("数据库系统异常:", e);
            errCode = "12";
        } else if (e instanceof MessageException) {
            errCode = e.getMessage();
        } else if (e instanceof CommuException) {
            logger.error("前置机系统异常：", e);
            errCode = "60";
        } else if (e instanceof IOException) {
            logger.error("加密机系统异常：", e);
            errCode = "14";
        } else {
            logger.error("未知错误：", e);
        }
        ExceptionLogDao.insert(ConstructEceptionLog.constructLog("4001", "3",
                thisClassName, e.getMessage()));
        logger.error("返回的错误码:" + errCode);
        return errCode;
    }

    protected void validateDataLen() throws CommuException {
        int dataLen = data.length;
        if (dataLen != fix_recv_data_length) {
            String messs = "终端传入的数据区的数据长度不正确，传入的长度[" + dataLen + "]字节,规定长度["
                    + fix_recv_data_length + "]字节";
            logger.error(messs);
            throw new MessageException("60");
        }
    }

    private String validateDevCard(BaseVo reqVo) throws SQLException,
            MessageException {
        String retCode = "00";
        // 设备相关异常
        retCode = MessageValidateDao.isLegalDevice(reqVo);

        // 卡片相关异常
        if("00".equals(retCode)){
            retCode = MessageValidateDao.isBlackList((RechargeReqVo) reqVo, getSTDbHelper());
        }
        return retCode;
    }
    
    /**
     * 如果输入的字符串不够长度，就在前面补0
     * @param input
     * @param length
     * @return
     */
    public String dealStr(String input, int length) {
        // 字符串长度超过要求长度，截取要求长度的字符串
        if (input.length() > length) {
            return input.substring(0, length - 1);
        } else {
            // 前面补0
            StringBuilder stringBuilder = new StringBuilder();
            String zero = "0";
            for (int index = 0; index < length - input.length(); index++) {
                stringBuilder.append(zero);
            }
            stringBuilder.append(input);
            return stringBuilder.toString();
        }
    }
    
    /**
     * 校验结果
     *
     * @param datas 加密机返回的结果
     * @param rspMsgLen 数据长度
     * @throws IOException
     */
    public void validateResult(byte[] datas, int rspMsgLen) throws IOException {
        // 传回的有数据长度，但是没有数据
        if (rspMsgLen > 0 && datas.length == 0) {
            String mess = "加密机传回的数据长度大于0，实际读取的数据长度为0";
            throw new IOException(mess);
        } else if (rspMsgLen != 0 && datas.length != rspMsgLen) {
            String mess = "加密机传回的数据长度大于0，与实际读取的数据长度不一致";
            throw new IOException(mess);
        }

    }
    
}
