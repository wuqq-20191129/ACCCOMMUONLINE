package com.goldsign.commu.frame.thread;

import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-08-27
 * @Time: 15:58
 */
public class BufferDataMoveThread implements Runnable {
    private static final Logger logger = Logger.getLogger(BufferDataMoveThread.class);
    //后续有更多在考虑转成表配置
    private static final Map<String, String> tableMap = new HashMap<>(2);

    static {
        tableMap.put("w_ol_buf_qrcode_order", "w_ol_qrcode_order");
        tableMap.put("w_ol_buf_qrpay_order", "w_ol_qrpay_order");
    }

    @Override
    public void run() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        DbHelper dbHelper = null;
        try {
            while (true) {
                dbHelper = new DbHelper("BufferDataMoveDBHelper", FrameDBConstant.OL_DBCPHELPER.getConnection());
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, FrameCodeConstant.PARA_QR_BUFFER_MOVE_DAY);
                int curHour = calendar.get(Calendar.HOUR_OF_DAY);
                logger.debug(format.format(calendar.getTime()) + "235959" + ",curHour:" + curHour);
                if (FrameCodeConstant.PARA_QR_BUFFER_MOVE_CLOCK == curHour || FrameCodeConstant.PARA_QR_BUFFER_MOVE_TIME_NO_LIMIT == FrameCodeConstant.CONTROL_USED) {
                    //执行迁移存储过程
                    String sql = "call w_acc_ol.w_up_ol_buf_mv(?,?,?,?,?)";
                    for (Map.Entry<String, String> entry : tableMap.entrySet()) {
                        String originTable = entry.getKey();
                        String destTable = entry.getValue();
                        String date = format.format(calendar.getTime()) + "235959";
                        int[] pInIndexes = {1, 2, 3};
                        Object[] pInStmtValues = {originTable, destTable, date};
                        int[] pOutIndexes = {4, 5};//存储过程输出参数索引列表
                        int[] pOutTypes = {DbHelper.PARAM_OUT_TYPE_INTEGER, DbHelper.PARAM_OUT_TYPE_VACHAR};//存储过程输出参数值类型
                        dbHelper.runStoreProcForOracle(sql, pInIndexes, pInStmtValues, pOutIndexes, pOutTypes);//执行存储过程
                        String retResult = dbHelper.getOutParamStringValue(4);
                        String sRet = dbHelper.getOutParamStringValue(5);//存储过程返回结果代码
                        logger.info(retResult + "," + sRet);
                    }
                }
                PubUtil.finalProcess(dbHelper);
                Thread.sleep(FrameCodeConstant.PARA_QR_BUFFER_MOVE_THREAD_SLEEP_TIME);
//                Thread.sleep(3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}
