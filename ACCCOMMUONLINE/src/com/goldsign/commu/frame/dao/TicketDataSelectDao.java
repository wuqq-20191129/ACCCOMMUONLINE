package com.goldsign.commu.frame.dao;

import com.goldsign.commu.app.vo.TicketAttributeVo;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.PubUtil;
import com.goldsign.lib.db.util.DbHelper;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-09-10
 * @Time: 16:07
 */
public class TicketDataSelectDao {

    private static Logger logger = Logger.getLogger(TicketDataSelectDao.class.getName());

    public  static Map<String, String> getTkCardTypeLogicNoMapping(String type) {
        boolean result = false;
        Map<String, String> map = new HashMap<String, String>();
        DbHelper dbHelper = null;
        try {
            dbHelper = new DbHelper("TicketDataSelectDao",
                    FrameDBConstant.OL_DBCPHELPER.getConnection());
            Object[] values = {type};
            String sql = "select t.CARD_SUB_TYPE,t.LOGICAL_CARD_TYPE from " + FrameDBConstant.COM_TK_P + "ic_cod_logical_card_type_con t where t.CARD_LARGE_TYPE=?";

            result = dbHelper.getFirstDocument(sql, values);
            while (result) {
                map.put(dbHelper.getItemValue("CARD_SUB_TYPE"),
                        dbHelper.getItemValue("LOGICAL_CARD_TYPE"));
                result = dbHelper.getNextDocument();
            }
        } catch (Exception e) {
            PubUtil.handleExceptionNoThrow(e, logger);

        } finally {
            PubUtil.finalProcess(dbHelper);
        }
        return map;
    }

    public static Map<String, TicketAttributeVo> getTicketAttribute(){
        boolean result = false;
        Map<String, TicketAttributeVo> map = new HashMap<>();
        DbHelper dbHelper = null;
        try {
            dbHelper = new DbHelper("TicketDataSelectDao",
                    FrameDBConstant.OL_DBCPHELPER.getConnection());
            String sql = "select t.card_main_id,t.card_sub_id,t.Max_Store_Val,ceil(t.exp_date / 1440) vaild_days," +
                    "       t.Is_Activation,t.card_cost, t.deposit_amnt, b.code_text pre_vaild_days" +
                    "  from "+FrameDBConstant.COM_ST_P+"op_prm_card_para t" +
                    "  left join "+FrameDBConstant.COM_OL_P+"ol_pub_flag b" +
                    "    on b.type = '11' and t.card_main_id || t.card_sub_id = b.code " +
                    " where t.record_flag = '0' and t.card_main_id = '08'" ;

            result = dbHelper.getFirstDocument(sql);
            while (result) {
                TicketAttributeVo vo  =new TicketAttributeVo();
                String cardMainId = dbHelper.getItemValue("card_main_id");
                String cardSubId = dbHelper.getItemValue("card_sub_id");
                String saleActiveFlag = dbHelper.getItemValue("Is_Activation");

                vo.setCardMainId(cardMainId);
                vo.setCardSubId(cardSubId);
                //充值上限
                vo.setFaceValue(Long.parseLong(dbHelper.getItemValue("card_cost")));
                vo.setDepositFee(Long.parseLong(dbHelper.getItemValue("deposit_amnt")));
                vo.setTicketUsedValidDays(Long.parseLong(dbHelper.getItemValue("vaild_days")));
                vo.setTicketUsedPreValidDays(Long.parseLong(dbHelper.getItemValue("pre_vaild_days")));
                //表字段含义为使用前是否需激活  该属性用途为发售激活标志 含义相反
                if("1".equals(saleActiveFlag)) {
                    vo.setSaleActivateFlag("0");
                }else {
                    vo.setSaleActivateFlag("1");
                }
                vo.setChargeMaxLimit(Long.parseLong(dbHelper.getItemValue("Max_Store_Val")));
                //押金
                map.put(cardMainId+cardSubId, vo);
                result = dbHelper.getNextDocument();
            }
        } catch (Exception e) {
            PubUtil.handleExceptionNoThrow(e, logger);

        } finally {
            PubUtil.finalProcess(dbHelper);
        }
        return map;
    }
}
