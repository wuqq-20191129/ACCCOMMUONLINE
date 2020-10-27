package com.goldsign.commu.app.dao;

import com.goldsign.commu.app.vo.Message54Vo;
import com.goldsign.commu.app.vo.Message64Vo;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * 撤销充值确认
 *
 *
 * @author zhangjh
 */
public class Message54Dao {

    private Logger logger = Logger.getLogger(Message54Dao.class);
    // 撤销充值确认SQL标签
    private static final String INSERT_54MESS_SQL = "insert into " + FrameDBConstant.COM_OL_P + "OL_CHG_SUB(water_no,message_id,termina_no,sam_logical_id,transation_seq,branches_code,pub_main_code,pub_sub_code,card_type,tk_Logic_no,tk_phy_no,is_test_tk,onl_tran_times,offl_tran_times,buss_type,value_type,charge_fee,balance,tac,write_rslt,operator_id,sys_ref_no,sys_date,msg_gen_time1,status,insert_date)\n"
            + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,2,sysdate)";
    // 撤销充值确认响应SQL标签
    private static final String UPDATE_54MESS_SQL = "update " + FrameDBConstant.COM_OL_P + "OL_CHG_SUB set return_code=?,err_code=? ,msg_gen_time2=?,update_date=sysdate where water_no=? ";
    // 查询是否撤销充值成功
    private static final String QR_SQL = "select t.*, t.rowid from " + FrameDBConstant.COM_OL_P + "OL_CHG_SUB t where t.water_no=? and t.return_code='00' and t.status=1 and t.msg_gen_time2=?";

    /**
     * 插入撤销充值确认消息
     *
     * @param msg54Vo
     * @param dbHelper
     * @return
     * @throws SQLException
     */
    public int insert(Message54Vo msg54Vo, DbHelper dbHelper)
            throws SQLException {
        logger.debug("插入撤销充值申请消息");
        Object[] values = {msg54Vo.getWaterNo(), msg54Vo.getMessageId(),
            msg54Vo.getTerminalNo(), msg54Vo.getSamLogicalId(),
            msg54Vo.getTransationSeq(), msg54Vo.getBranchesCode(),
            msg54Vo.getPubMainCode(), msg54Vo.getPubSubCode(),
            msg54Vo.getCardType(), msg54Vo.getTkLogicNo(),
            msg54Vo.getTkPhyNo(), msg54Vo.getIsTestTk(),
            msg54Vo.getOnlTranTimes(), msg54Vo.getOfflTranTimes(),
            msg54Vo.getBussType(), msg54Vo.getValueType(),
            msg54Vo.getChargeFee(), msg54Vo.getBalance(), msg54Vo.getTac(),
            msg54Vo.getWriteRslt(), msg54Vo.getOperatorId(),
            msg54Vo.getSysRefNo(), msg54Vo.getSysdate(),
            msg54Vo.getMsgGenTime()};
        return dbHelper.executeUpdate(INSERT_54MESS_SQL, values);
    }

    /**
     * 插入撤销充值确认响应消息
     *
     * @param msg64Vo 撤销充值申请响应
     * @param dbHelper DbHelper
     * @return 修改的记录
     * @throws SQLException
     */
    public int updateMsg(Message64Vo msg64Vo, DbHelper dbHelper)
            throws SQLException {
        logger.debug("插入撤销充值申请响应消息");
        Object[] values = {msg64Vo.getReturnCode(), msg64Vo.getErrCode(),
            msg64Vo.getMsgGenTime(), msg64Vo.getWaterNo()};
        return dbHelper.executeUpdate(UPDATE_54MESS_SQL, values);
    }

    public boolean query(Message54Vo msg54Vo, DbHelper firstDbHelper)
            throws SQLException {
        logger.debug(" 查询撤销信息");
        Object[] values = {msg54Vo.getSysRefNo(), msg54Vo.getSysdate()};
        boolean result = firstDbHelper.getFirstDocument(QR_SQL, values);
        return result;
    }
}
