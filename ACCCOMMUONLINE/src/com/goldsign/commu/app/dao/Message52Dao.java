package com.goldsign.commu.app.dao;

import com.goldsign.commu.app.vo.Message52Vo;
import com.goldsign.commu.app.vo.Message62Vo;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 *
 * @author zhangjh
 */
public class Message52Dao {

    private Logger logger = Logger.getLogger(Message52Dao.class);
    // 充值确认
    // msg_gen_time2传上来的系统时间（61返回的）
    private static final String INSERT_52MESS_SQL = "insert into " + FrameDBConstant.COM_OL_P + "OL_CHG_PLUS(water_no,message_id,termina_no,sam_logical_id,transation_seq,branches_code,pub_main_code,pub_sub_code,card_type,tk_Logic_no,tk_phy_no,is_test_tk,onl_tran_times,offl_tran_times,buss_type,value_type,charge_fee,balance,mac1,write_rslt,operator_id,sys_ref_no,sys_date,msg_gen_time1,status,insert_date)\n"
            + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,2,sysdate)";
    // 充值确认响应
    private static final String UPDATE_52MESS_SQL = "update " + FrameDBConstant.COM_OL_P + "OL_CHG_PLUS set return_code=?,err_code=? ,msg_gen_time2=?,update_date=sysdate where water_no=? ";
    // 查询是否充值成功
    private static final String QR_SQL = "select t.* from " + FrameDBConstant.COM_OL_P + "OL_CHG_PLUS t where t.water_no=? and t.return_code='00' and t.status=1 and t.msg_gen_time2=?";

    /**
     * 插入充值消息
     * @param msg52Vo Message52Vo
     * @param dbHelper DbHelper
     * @return 更新的记录数
     * @throws SQLException SQLException
     */
    public int insert(Message52Vo msg52Vo, DbHelper dbHelper)
            throws SQLException {
        logger.debug("插入充值消息");
        Object[] values = {msg52Vo.getWaterNo(), msg52Vo.getMessageId(),
            msg52Vo.getTerminalNo(), msg52Vo.getSamLogicalId(),
            msg52Vo.getTransationSeq(), msg52Vo.getBranchesCode(),
            msg52Vo.getPubMainCode(), msg52Vo.getPubSubCode(),
            msg52Vo.getCardType(), msg52Vo.getTkLogicNo(),
            msg52Vo.getTkPhyNo(), msg52Vo.getIsTestTk(),
            msg52Vo.getOnlTranTimes(), msg52Vo.getOfflTranTimes(),
            msg52Vo.getBussType(), msg52Vo.getValueType(),
            msg52Vo.getChargeFee(), msg52Vo.getBalance(), msg52Vo.getTac(),
            msg52Vo.getWriteRslt(), msg52Vo.getOperatorId(),
            msg52Vo.getSysRefNo(), msg52Vo.getSysdate(),
            msg52Vo.getMsgGenTime()};
        return dbHelper.executeUpdate(INSERT_52MESS_SQL, values);
    }

    /**
     * 更新充值响应消息
     *
     *
     * @param msg62Vo Message62Vo
     * @param dbHelper DbHelper
     * @return 更新的记录数
     * @throws SQLException SQLException
     */
    public int updateMsg(Message62Vo msg62Vo, DbHelper dbHelper)
            throws SQLException {
        logger.debug(" 更新充值响应消息");
        Object[] values = {msg62Vo.getReturnCode(), msg62Vo.getErrCode(),
            msg62Vo.getMsgGenTime(), msg62Vo.getWaterNo()};
        return dbHelper.executeUpdate(UPDATE_52MESS_SQL, values);
    }

    /**
     * 校验是否充值
     * @param msg52Vo
     * @param dbHelper
     * @return
     * @throws SQLException
     */
    public boolean query(Message52Vo msg52Vo, DbHelper dbHelper)
            throws SQLException {
        boolean result = true;
        logger.debug(" 查询充值信息");
        Object[] values = {msg52Vo.getSysRefNo(), msg52Vo.getSysdate()};
        logger.debug("系统参照号：" + msg52Vo.getSysRefNo());
        logger.debug("系统时间：" + msg52Vo.getSysdate());
        result = dbHelper.getFirstDocument(QR_SQL, values);
        if(result){
            msg52Vo.setOnlTranTimes(dbHelper.getItemLongValue("onl_tran_times"));
            msg52Vo.setSysdate(dbHelper.getItemValue("sys_date"));
        }
        return result;
    }
}
