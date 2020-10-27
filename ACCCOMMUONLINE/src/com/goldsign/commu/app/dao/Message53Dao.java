package com.goldsign.commu.app.dao;

import com.goldsign.commu.app.vo.Message53Vo;
import com.goldsign.commu.app.vo.Message63Vo;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * 撤销充值
 *
 *
 * @author zhangjh
 */
public class Message53Dao {

    private Logger logger = Logger.getLogger(Message53Dao.class);
    // 撤销充值申请SQL标签
    private static final String INSERT_53MESS_SQL = "insert into " + FrameDBConstant.COM_OL_P + "OL_CHG_SUB(water_no,message_id,termina_no,sam_logical_id,transation_seq,branches_code,pub_main_code,pub_sub_code,card_type,tk_Logic_no,tk_phy_no,is_test_tk,onl_tran_times,offl_tran_times,buss_type,value_type,charge_fee,balance,last_tran_termno,last_trantime,operator_id,msg_gen_time1,status,insert_date)\n"
            + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,1,sysdate)";
    // 撤销充值申请SQL标签
    private static final String UPDATE_53MESS_SQL = "update " + FrameDBConstant.COM_OL_P + "OL_CHG_SUB set sys_date=?,return_code=?,err_code=? ,msg_gen_time2=?,update_date=sysdate,sys_ref_no=? where water_no=? ";

    // 查询是否充值成功
    private static final String QR_SQL = "select t.* from " + FrameDBConstant.COM_OL_P + "OL_CHG_PLUS t where t.message_id=? and t.transation_seq=?";
    
    /**
     * 插入撤销充值申请消息
     *
     * @param msg53Vo Message53Vo
     * @param dbHelper DbHelper
     * @return 更新的记录数
     * @throws SQLException SQLException
     */
    public int insert(Message53Vo msg53Vo, DbHelper dbHelper)
            throws SQLException {
        logger.debug("插入撤销充值申请消息");
        Object[] values = {msg53Vo.getWaterNo(), msg53Vo.getMessageId(),
            msg53Vo.getTerminalNo(), msg53Vo.getSamLogicalId(),
            msg53Vo.getTransationSeq(), msg53Vo.getBranchesCode(),
            msg53Vo.getPubMainCode(), msg53Vo.getPubSubCode(),
            msg53Vo.getCardType(), msg53Vo.getTkLogicNo(),
            msg53Vo.getTkPhyNo(), msg53Vo.getIsTestTk(),
            msg53Vo.getOnlTranTimes(), msg53Vo.getOfflTranTimes(),
            msg53Vo.getBussType(), msg53Vo.getValueType(),
            msg53Vo.getChargeFee(), msg53Vo.getBalance(),
            msg53Vo.getLastTranTermNo(), msg53Vo.getLastDealTime(),
            msg53Vo.getOperatorId(), msg53Vo.getMsgGenTime()};

        return dbHelper.executeUpdate(INSERT_53MESS_SQL, values);
    }

    /**
     * 更新撤销充值响应消息
     * @param msg63Vo 撤销充值响应消息
     * @param dbHelper DbHelper
     * @return 更新的记录数
     * @throws SQLException
     */
    public int updateMsg(Message63Vo msg63Vo, DbHelper dbHelper)
            throws SQLException {
        logger.debug("更新撤销充值响应消息");
        Object[] values = {msg63Vo.getSysdate(), msg63Vo.getReturnCode(),
            msg63Vo.getErrCode(), msg63Vo.getMsgGenTime(),
            msg63Vo.getSysRefNo(), msg63Vo.getWaterNo()};
        return dbHelper.executeUpdate(UPDATE_53MESS_SQL, values);

    }
    
    /**
     * 校验是否充值
     * @param msg53Vo
     * @param dbHelper
     * @return
     * @throws SQLException
     */
    public boolean query(Message53Vo msg53Vo, DbHelper dbHelper)
            throws SQLException {
        boolean result = true;
        logger.debug(" 查询充值信息");
        Object[] values = {"52", msg53Vo.getTransationSeq()-1};
        result = dbHelper.getFirstDocument(QR_SQL, values);
        return result;
    }
}
