package com.goldsign.commu.app.dao;

import com.goldsign.commu.app.vo.BaseVo;
import com.goldsign.commu.app.vo.Message55Vo;
import com.goldsign.commu.app.vo.Message65Vo;
import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * 激活
 *
 *
 * @author zhangjh
 */
public class Message55Dao {

    private Logger logger = Logger.getLogger(Message55Dao.class);
    // 激活信息SQL标签
    private static final String INSERT_55MESS_SQL = "insert into " + FrameDBConstant.COM_OL_P + "ol_chg_activation(water_no,message_id,termina_no,sam_logical_id,random_num,msg_gen_time1,insert_date) values (?,?,?,?,?,?,sysdate)";
    // 激活响应信息SQL标签
    private static final String UPDATE_55MESS_SQL = "update " + FrameDBConstant.COM_OL_P + "ol_chg_activation set mac=? ,return_code=?,err_code=? ,sys_ref_no=?,msg_gen_time2=?,update_date=sysdate where water_no=? ";
    // 查询终端是否激活
    private static final String QUERY_ACT_SQL = " select t.*, t.rowid from " + FrameDBConstant.COM_OL_P + "ol_chg_activation  t where  return_code='00' and t.err_code='00' and termina_no=? and sam_logical_id=? and  sysdate between update_date and update_date+? ";

    /**
     * 插入激活消息
     *
     * @param msg55Vo Message55Vo
     * @param dbHelper DbHelper
     * @return 更新的记录数
     * @throws SQLException
     */
    public int insert(Message55Vo msg55Vo, DbHelper dbHelper)
            throws SQLException {
        logger.debug("插入激活消息");
        Object[] values = {msg55Vo.getWaterNo(), msg55Vo.getMessageId(),
            msg55Vo.getTerminalNo(), msg55Vo.getSamLogicalId(),
            msg55Vo.getRandomNum(), msg55Vo.getMsgGenTime()};
        return dbHelper.executeUpdate(INSERT_55MESS_SQL, values);
    }

    /**
     * 更新激活响应消息
     *
     * @param msg65Vo Message65Vo
     * @param dbHelper DbHelper
     * @return 更新的记录数
     * @throws SQLException SQLException
     */
    public int updateMsg(Message65Vo msg65Vo, DbHelper dbHelper)
            throws SQLException {
        logger.debug(" 更新激活响应消息");
        Object[] values = {msg65Vo.getMac(), msg65Vo.getReturnCode(),
            msg65Vo.getErrCode(), msg65Vo.getSysRefNo(),
            msg65Vo.getMsgGenTime(), msg65Vo.getWaterNo()};
        return dbHelper.executeUpdate(UPDATE_55MESS_SQL, values);

    }

    public int queryDeviceActStatus(BaseVo msg51Vo, DbHelper dbHelper)
            throws SQLException {
        // FrameCodeConstant.EffectiveDate
        logger.debug("查询终端是否激活并在有效期内");
        Object[] values = {msg51Vo.getTerminalNo(), msg51Vo.getSamLogicalId(),
            FrameCodeConstant.EffectiveDate};
        boolean result = dbHelper.getFirstDocument(QUERY_ACT_SQL, values);
        return result ? 1 : 0;
    }
}
