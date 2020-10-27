package com.goldsign.commu.app.dao;

import com.goldsign.commu.app.vo.Message51Vo;
import com.goldsign.commu.app.vo.Message61Vo;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author zhangjh
 */
public class Message51Dao {

    private final static Logger logger = Logger.getLogger(Message51Dao.class);
    private static final String INSERT_51MESS_SQL = "insert into " + FrameDBConstant.COM_OL_P + "OL_CHG_PLUS(water_no,message_id,termina_no,sam_logical_id,transation_seq,branches_code,pub_main_code,pub_sub_code,card_type,tk_Logic_no,tk_phy_no,is_test_tk,onl_tran_times,offl_tran_times,buss_type,value_type,charge_fee,balance,mac1,tk_chge_seq,last_tran_termno,last_tran_time,operator_id,msg_gen_time1,status,insert_date)\n"
            + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,1,sysdate)";
    private static final String UPDATE_51MESS_SQL = "update " + FrameDBConstant.COM_OL_P + "OL_CHG_PLUS set mac2=? ,sys_date=?,return_code=?,err_code=? ,update_date=sysdate,sys_ref_no=?,msg_gen_time2=? where water_no=? ";

    /**
     * 插入充值消息
     *
     */
    public int insert51Msg(Message51Vo msg51Vo, DbHelper dbHelper)
            throws SQLException {
        logger.debug("充值申请入库开始");
        Object[] values = {msg51Vo.getWaterNo(), msg51Vo.getMessageId(),
            msg51Vo.getTerminalNo(), msg51Vo.getSamLogicalId(),
            msg51Vo.getTransationSeq(), msg51Vo.getBranchesCode(),
            msg51Vo.getPubMainCode(), msg51Vo.getPubSubCode(),
            msg51Vo.getCardType(), msg51Vo.getTkLogicNo(),
            msg51Vo.getTkPhyNo(), msg51Vo.getIsTestTk(),
            msg51Vo.getOnlTranTimes(), msg51Vo.getOfflTranTimes(),
            msg51Vo.getBussType(), msg51Vo.getValueType(),
            msg51Vo.getChargeFee(), msg51Vo.getBalance(),
            msg51Vo.getMac1(), msg51Vo.getTkChgeSeq(),
            msg51Vo.getLastTranTermNo(), msg51Vo.getLastDealTime(),
            msg51Vo.getOperatorId(), msg51Vo.getMsgGenTime()};
        return dbHelper.executeUpdate(INSERT_51MESS_SQL, values);

    }

    public int updateMsg(Message61Vo msg61Vo, DbHelper dbHelper)
            throws SQLException {
        logger.debug("充值申请响应开始");
        Object[] values = {msg61Vo.getMac2(), msg61Vo.getSysdate(),
            msg61Vo.getReturnCode(), msg61Vo.getErrCode(),
            msg61Vo.getSysRefNo(), msg61Vo.getMsgGenTime(),
            msg61Vo.getWaterNo()};
        return dbHelper.executeUpdate(UPDATE_51MESS_SQL, values);
    }

}
