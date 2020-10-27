package com.goldsign.commu.frame.dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.goldsign.commu.frame.constant.FrameCodeConstant;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.constant.FrameMessageCodeConstant;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;

/**
 *
 * @author hejj
 */
public class DeviceDao {

    private static Logger logger = Logger.getLogger(DeviceDao.class.getName());
    private static String sqlStr = "select line_id||station_id||dev_type_id||device_id dev,ip_address from " + FrameDBConstant.COM_ST_P + "op_prm_dev_code where record_flag='0'";
    private static String sqlChargeDevType = "select t.code,t.code_text from " + FrameDBConstant.COM_OL_P + "OL_PUB_FLAG t where t.type='1'";
//    private static String sqlStrLine = "select line_id,lc_ip from " + FrameDBConstant.COM_ST_P + "op_prm_line where record_flag='0'";
    private static String sqlStrLine = "select line_id,ip from " + FrameDBConstant.COM_OL_P + "ol_cod_lc_line";
    private static String sqlStrAcc = "select line_id||station_id||dev_type_id||device_id dev,ip_address from " + FrameDBConstant.COM_ST_P + "op_prm_dev_code_acc where record_flag='0'";

    public DeviceDao() throws Exception {
        super();
    }

    /**
     * 获取所有设备类型Ip
     *
     * @return
     */
    public static Map<String, String> getAllDevIp() {
        boolean result = false;
        Map<String, String> allDevIp = new ConcurrentHashMap<String, String>();
        DbHelper dbHelper = null;
        try {
            dbHelper = new DbHelper("DeviceDao",
                    FrameDBConstant.ST_DBCPHELPER.getConnection());
            result = dbHelper.getFirstDocument(sqlStr);
            while (result) {
                allDevIp.put(dbHelper.getItemValue("dev"),
                        dbHelper.getItemValue("ip_address"));
                result = dbHelper.getNextDocument();
            }
        } catch (Exception e) {
            PubUtil.handleExceptionNoThrow(e, logger);

        } finally {
            PubUtil.finalProcess(dbHelper);
        }
        return allDevIp;
    }
    
    /**
     * 获取所有设备类型Ip
     *
     * @return
     */
    public static Map<String, String> getAllDevAcc() {
        boolean result = false;
        Map<String, String> allDevIp = new ConcurrentHashMap<String, String>();
        DbHelper dbHelper = null;
        try {
            dbHelper = new DbHelper("DeviceDao",
                    FrameDBConstant.ST_DBCPHELPER.getConnection());
            result = dbHelper.getFirstDocument(sqlStrAcc);
            while (result) {
                allDevIp.put(dbHelper.getItemValue("dev"),
                        dbHelper.getItemValue("ip_address"));
                result = dbHelper.getNextDocument();
            }
        } catch (Exception e) {
            PubUtil.handleExceptionNoThrow(e, logger);

        } finally {
            PubUtil.finalProcess(dbHelper);
        }
        return allDevIp;
    }
    
    
    /**
     * 获取所有可激活设备类型
     *
     * @return
     */
    public static Map<String, String> getChargeDevType() {
        boolean result = false;
        Map<String, String> map = new ConcurrentHashMap<String, String>();
        DbHelper dbHelper = null;
        try {
            dbHelper = new DbHelper("DeviceDao",
                    FrameDBConstant.OL_DBCPHELPER.getConnection());
            result = dbHelper.getFirstDocument(sqlChargeDevType);
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
    
    
    /**
     * 获取所有线路Ip
     * W_OL_COD_LC_LINE
     * @return
     */
    public static Map<String, String> getAllLineIp() {
        boolean result = false;
        Map<String, String> allLineIp = new ConcurrentHashMap<String, String>();
        DbHelper dbHelper = null;
        try {
            dbHelper = new DbHelper("DeviceDao",
                    FrameDBConstant.OL_DBCPHELPER.getConnection());
            result = dbHelper.getFirstDocument(sqlStrLine);
            while (result) {
                allLineIp.put(dbHelper.getItemValue("line_id"), dbHelper.getItemValue("ip"));
                result = dbHelper.getNextDocument();
            }
        } catch (Exception e) {
            PubUtil.handleExceptionNoThrow(e, logger);

        } finally {
            PubUtil.finalProcess(dbHelper);
        }
        return allLineIp;
    }

    /**
     *
     */
    public synchronized static void update() {
        FrameCodeConstant.ALL_DEV = getAllDevIp();
        FrameCodeConstant.CHARGE_DEV_TYPE = getChargeDevType();
        FrameCodeConstant.ALL_LINE = getAllLineIp();
//        FrameCodeConstant.ALL_DEV_ACC = getAllDevAcc();
    }

    /**
     *
     */
    public synchronized static Map<String, String> query() {
        return FrameCodeConstant.ALL_DEV;
    }
}
