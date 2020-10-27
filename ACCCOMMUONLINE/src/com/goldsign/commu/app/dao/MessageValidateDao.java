package com.goldsign.commu.app.dao;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.goldsign.commu.app.vo.BaseVo;
import com.goldsign.commu.app.vo.RechargeReqVo;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.exception.MessageException;
import com.goldsign.lib.db.util.DbHelper;

public class MessageValidateDao {

    private final static String QR_SQL1 = "select count(1) counts from " + FrameDBConstant.COM_ST_P + "op_prm_black_list_mtr t where t.card_logical_id =?";
    private final static String QR_SQL2 = "select count(1) counts from " + FrameDBConstant.COM_ST_P + "op_prm_black_list_oct t where t.card_logical_id =?";
    private static final Logger LOGGER = Logger
            .getLogger(MessageValidateDao.class);

    public static String isBlackList(RechargeReqVo reqVo, DbHelper seccondDbHelper)
            throws IllegalArgumentException, IllegalStateException,
            SQLException, MessageException {
        Object[] values = {reqVo.getTkLogicNo()};
        boolean result = seccondDbHelper.getFirstDocument(QR_SQL1, values);
        if (result) {
            Integer counts = seccondDbHelper.getItemIntegerValue("counts");
            if (counts > 0) {
                LOGGER.warn(reqVo.getTkLogicNo()+"充值卡为黑名单卡");
                return "34";
//                throw new MessageException("34");
            }
            result = seccondDbHelper.getFirstDocument(QR_SQL2, values);
            if (result) {
                counts = seccondDbHelper.getItemIntegerValue("counts");
                if (counts > 0) {
                    LOGGER.warn(reqVo.getTkLogicNo()+"充值卡为黑名单卡");
                    return "34";
//                    throw new MessageException("34");
                }
            }

        }
        return "00";
    }

    /**
     * 判断设备是否合法
     *
     * @param reqVo
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     * @throws SQLException
     * @throws MessageException
     */
    public static String isLegalDevice(BaseVo reqVo)
            throws IllegalArgumentException, IllegalStateException,
            SQLException, MessageException {
        if (!FrameCodeConstant.ALL_DEV.containsKey(reqVo.getTerminalNo())) {
            LOGGER.warn(reqVo.getTerminalNo()+"设备在系统中不存在");
            return "21";
//            throw new MessageException("21");
        }
        return "00";
    }
}
