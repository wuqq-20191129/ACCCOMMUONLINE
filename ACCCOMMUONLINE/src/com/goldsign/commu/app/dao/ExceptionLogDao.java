/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.dao;

import com.goldsign.commu.app.vo.EceptionLog;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * 记录异常信息
 *
 * @author zhangjh
 */
public class ExceptionLogDao {

    private final static Logger logger = Logger.getLogger(ExceptionLogDao.class);
    private final static String INSERT_LABLE = " insert into " + FrameDBConstant.COM_OL_P + "ol_log_sys_error(id,ip,excp_id,excp_type,class_name,excp_desc,insert_date) "
            + "values(" + FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_log_sys_error.NEXTVAL,?,?,?,?,?,sysdate)";
    private final static byte[] LOCK = new byte[0];

    public static int insert(EceptionLog log) {

        // 只有调试时才记录日志
        if (logger.isInfoEnabled()) {
            synchronized (LOCK) {
                Object[] params = {log.getIp(), log.getExcpId(),
                    log.getExcpType(), log.getClassName(),
                    log.getExcpDesc()};
                DbHelper dbHelper = null;
                try {
                    dbHelper = new DbHelper("",
                            FrameDBConstant.OL_DBCPHELPER.getConnection());

                    dbHelper.executeUpdate(INSERT_LABLE, params);

                } catch (SQLException exception) {
                    logger.warn("插入日志异常：" + exception);
                } finally {
                    if (null != dbHelper) {
                        try {
                            dbHelper.closeConnection();
                        } catch (SQLException ex) {
                            logger.warn("关闭连接失败：" + ex);
                        }
                    }
                }
            }
        }
        return 0;
    }
}
