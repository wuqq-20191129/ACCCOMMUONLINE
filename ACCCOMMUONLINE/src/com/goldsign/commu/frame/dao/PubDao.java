package com.goldsign.commu.frame.dao;


import org.apache.log4j.Logger;

import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 综合查询DAO
 *
 * @author lind
 */
public class PubDao {

    private static Logger logger = Logger.getLogger(PubDao.class.getName());

    public PubDao() throws Exception {
        super();
    }

    /**
     * 查询PSAM卡
     *
     * @return
     */
    public static boolean checkPSAM(String samLogicalNo) {
        boolean result = true;
        DbHelper dbHelper = null;
        Object[] values = {samLogicalNo};
        try {
            dbHelper = new DbHelper("PubDao",
                    FrameDBConstant.OL_DBCPHELPER.getConnection());
            result = dbHelper.getFirstDocument("select 1 from " + FrameDBConstant.COM_TK_P + "IC_SAM_STOCK t"
                    + " where t.logic_no=? and t.produce_type='01' and t.is_instock='0' and t.is_bad='1' and t.stock_state='04'", values);//'成品卡，出库，好卡，卡分发出库'
        } catch (Exception e) {
            PubUtil.handleExceptionNoThrow(e, logger);
        } finally {
            PubUtil.finalProcess(dbHelper);
        }
        return result;
    }

    /**
     * 查询终端编号
     *
     * @return
     */
    public static boolean checkDevCode(String id) {
        boolean result = true;
        DbHelper dbHelper = null;
        Object[] values = {id};
        try {
            dbHelper = new DbHelper("PubDao",
                    FrameDBConstant.ST_DBCPHELPER.getConnection());
            result = dbHelper.getFirstDocument("select 1 from " + FrameDBConstant.COM_ST_P + "op_prm_dev_code t"
                    + " where t.line_id||t.station_id||t.dev_type_id||t.device_id=? and t.record_flag='0'", values);
        } catch (Exception e) {
            PubUtil.handleExceptionNoThrow(e, logger);
        } finally {
            PubUtil.finalProcess(dbHelper);
        }
        return result;
    }


    /*
    更新日统计表
    */
    public static synchronized int olStsAcc(Map<String, Object> valueMap, Map<String, Object> whereMap) {
        int n = 0;
        try {
            n = updateOLStsAcc(valueMap, whereMap);
            if (n < 1) {
                valueMap.putAll(whereMap);
                n = insertOLStsAcc(valueMap);
            }
            logger.info("更新" + FrameDBConstant.COM_OL_P + "ol_sts_acc 共" + n + "条数据");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return n;
    }

    /*
    更新日统计表
    */
    public static int updateOLStsAcc(Map<String, Object> valueMap, Map<String, Object> whereMap) {
        DbHelper dbHelper = null;
        int i = 0;

        try {
            StringBuilder valueSql = new StringBuilder();
            Object[] values = new Object[valueMap.size() + whereMap.size()];
            for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (i != 0) {
                    valueSql.append(",");
                }
                valueSql.append(key);
                valueSql.append("=");
                valueSql.append(key);
                valueSql.append("+? ");
                values[i] = value;
                i++;
            }

            StringBuilder whereSql = new StringBuilder();
            int j = 0;
            for (Map.Entry<String, Object> entry : whereMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (j != 0) {
                    whereSql.append(" and ");
                }
                whereSql.append(key);
                whereSql.append("=? ");
                values[i + j] = value;
                j++;
            }

            String sql = "update " + FrameDBConstant.COM_OL_P + "ol_sts_acc set " + valueSql.toString()
                    + " where " + whereSql.toString();

            logger.debug(sql);
            dbHelper = new DbHelper("PubDao", FrameDBConstant.OL_DBCPHELPER.getConnection());
            i = dbHelper.executeUpdate(sql, values);
        } catch (Exception e) {
            PubUtil.handleExceptionNoThrow(e, logger);
        } finally {
            PubUtil.finalProcess(dbHelper);
        }
        return i;
    }


    /*
    插入日统计表
    */
    public static int insertOLStsAcc(Map<String, Object> valueMap) {
        StringBuilder valueSql = new StringBuilder();
        StringBuilder whereSql = new StringBuilder();
        int i = 0;
        Object[] values = new Object[valueMap.size()];
        for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            valueSql.append(",");
            valueSql.append(key);
            whereSql.append(",?");
            values[i] = value;
            i++;
        }

        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_sts_acc (water_no " + valueSql.toString()
                + ")values (" + FrameDBConstant.COM_OL_P + "seq_w_ol_sts_acc.nextval " + whereSql.toString() + ")";
        DbHelper dbHelper = null;
        try {
            logger.debug(sql);
            dbHelper = new DbHelper("PubDao", FrameDBConstant.OL_DBCPHELPER.getConnection());
            i = dbHelper.executeUpdate(sql, values);
        } catch (Exception e) {
            PubUtil.handleExceptionNoThrow(e, logger);
        } finally {
            PubUtil.finalProcess(dbHelper);
        }
        return i;
    }


    /**
     * 查询配置表
     *
     * @return
     */
    public static int getOlPubFlagInt(String type) {
        boolean result = false;
        int n = 1;
        DbHelper dbHelper = null;
        try {
            dbHelper = new DbHelper("DeviceDao",
                    FrameDBConstant.OL_DBCPHELPER.getConnection());
            Object[] values = {type};
            String sql = "select t.code from " + FrameDBConstant.COM_OL_P + "OL_PUB_FLAG t where t.type=?";

            result = dbHelper.getFirstDocument(sql, values);
            if (result) {
                n = dbHelper.getItemIntValue("code");
            }
        } catch (Exception e) {
            PubUtil.handleExceptionNoThrow(e, logger);

        } finally {
            PubUtil.finalProcess(dbHelper);
        }
        return n;
    }

    /**
     * 查询配置表
     *
     * @return
     */
    public static String getOlPubFlagString(String type) {
        boolean result = false;
        String n = "";
        DbHelper dbHelper = null;
        try {
            dbHelper = new DbHelper("DeviceDao",
                    FrameDBConstant.OL_DBCPHELPER.getConnection());
            Object[] values = {type};
            String sql = "select t.code from " + FrameDBConstant.COM_OL_P + "OL_PUB_FLAG t where t.type=?";

            result = dbHelper.getFirstDocument(sql, values);
            if (result) {
                n = dbHelper.getItemValue("code");
            }
        } catch (Exception e) {
            PubUtil.handleExceptionNoThrow(e, logger);

        } finally {
            PubUtil.finalProcess(dbHelper);
        }
        return n;
    }


    /**
     * 获取所有可激活设备类型
     *
     * @return
     */
    public static Map<String, String> getOlPubFlagMap(String type) {
        boolean result = false;
        Map<String, String> map = new ConcurrentHashMap<String, String>();
        DbHelper dbHelper = null;
        try {
            dbHelper = new DbHelper("DeviceDao",
                    FrameDBConstant.OL_DBCPHELPER.getConnection());
            Object[] values = {type};
            String sql = "select t.code,t.code_text from " + FrameDBConstant.COM_OL_P + "OL_PUB_FLAG t where t.type=?";

            result = dbHelper.getFirstDocument(sql, values);
            while (result) {
                map.put(dbHelper.getItemValue("code"),
                        dbHelper.getItemValue("code_text"));
                result = dbHelper.getNextDocument();
            }
        } catch (Exception e) {
            PubUtil.handleExceptionNoThrow(e, logger);

        } finally {
            PubUtil.finalProcess(dbHelper);
        }
        return map;
    }

    public static List<String> getOlPubFlgList(String type) {
        List<String> list = new ArrayList<>();
        boolean result = false;
        DbHelper dbHelper = null;
        try {
            dbHelper = new DbHelper("PubDao",
                    FrameDBConstant.OL_DBCPHELPER.getConnection());
            String sql = "select t.code,t.code_text from " + FrameDBConstant.COM_OL_P + "OL_PUB_FLAG t where t.type=?";
            Object[] values = {type};
            result = dbHelper.getFirstDocument(sql, values);
            while (result) {
                list.add(dbHelper.getItemValue("code"));
                result = dbHelper.getNextDocument();
            }
        } catch (Exception e) {
            PubUtil.handleExceptionNoThrow(e, logger);
        } finally {
            PubUtil.finalProcess(dbHelper);
        }
        return list;
    }
}
