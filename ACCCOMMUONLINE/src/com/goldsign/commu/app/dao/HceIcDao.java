package com.goldsign.commu.app.dao;

import com.goldsign.commu.app.util.EncryptorJMJUtil;
import com.goldsign.commu.app.vo.AirSaleCfmVo;
import com.goldsign.commu.app.vo.AirSaleVo;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.constant.FrameMessageCodeConstant;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * HCE票卡信息DAO类
 * 2018-1-4
 * @author lind
 */
public class HceIcDao {

    // 初始化
    private static final HceIcDao HCE_IC_DAO = new HceIcDao();
    private final static Logger logger = Logger.getLogger(HceIcDao.class);

    /**
     * 私有构造方法
     *
     */
    private HceIcDao() {
    }

    /**
     * 获取单例对象
     *
     * @return
     */
    public static HceIcDao getInstance() {
        return HCE_IC_DAO;
    }

    /*
    空发查询申请状态（“1”）的手机号（票务中）
    */
    public boolean checkAirSaleTK(AirSaleVo airSaleVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("空发查询申请状态的手机号");
        long startTime = System.currentTimeMillis();

        boolean result = false;
        //modify by zhongzq 20190912
//        Object[] values = {airSaleVo.getPhoneNo(), "1"};//手机号，手机设备标识
        Object[] values = {airSaleVo.getPhoneNo(), "1",airSaleVo.getCardType()};//手机号，手机设备标识
        String sql = "select * from "+ FrameDBConstant.COM_TK_P + "ic_hce_card_info where phone_no=? and act_flag=? and card_type = ?" ;
        result = olDbHelper.getFirstDocument(sql, values);
        if(result){
            airSaleVo.setCardLogicalId(olDbHelper.getItemValue("card_logical_id"));
        }
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("空发查询申请状态的手机号耗时：" + (endTime - startTime));
        return result;
    }
    
    
    /*
    空发取HCE逻辑卡号
    */
    public synchronized boolean getCardLogicalTK(AirSaleVo airSaleVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("空发取HCE逻辑卡号");
        long startTime = System.currentTimeMillis();
        boolean result = false;
        int seqNo = 0;
        String hceType = airSaleVo.getTerminaNo().substring(0, 2);
        //modify by zhongzq 20190911 票种条件改为动态获取
//        Object[] values = {hceType+FrameMessageCodeConstant.HCE_LOGCAL_NO_INDEX.get(hceType),
//                FrameMessageCodeConstant.HCE_LOGCAL_CARD_TYPE,FrameMessageCodeConstant.IS_TEST_FLAG};//手机票
        String cardSub = airSaleVo.getCardType().substring(2,4);
        Object[] values = {hceType+FrameMessageCodeConstant.HCE_LOGCAL_NO_INDEX.get(hceType),
                FrameMessageCodeConstant.HCE_CARD_LOGIC_NO_MAPPING.get(cardSub),FrameMessageCodeConstant.IS_TEST_FLAG};//手机票
        
        olDbHelper.setAutoCommit(false);
        //取最小HCE逻辑卡号段序号
//        String sql = "select * from (select t.seq_no,to_number(t.start_logicno) start_logicno,t.city_code,t.industry_code,t.app_identifier,t.bill_no from "
//                + FrameDBConstant.COM_TK_P + "IC_BC_LOGIC_NO t where (t.seq_no<to_number(t.end_logicno) and t.seq_no>=to_number(t.start_logicno)) "
//                + " and t.produce_factory_id||substr(t.start_logicno,0,1)=? and t.blank_card_type=? and app_identifier=? and t.record_flag=0 order by t.bill_date) where rownum=1" ;
        //modify by zhongzq 20190911 优化sql
        String sql = "select * from (select t.seq_no,to_number(t.start_logicno) start_logicno,to_number(t.end_logicno) end_logicno,t.city_code,t.industry_code,t.app_identifier,t.bill_no from "
                + FrameDBConstant.COM_TK_P + "IC_BC_LOGIC_NO t where t.seq_no < to_number(t.end_logicno)  "
                + " and t.produce_factory_id||substr(t.start_logicno,0,1)=? and t.blank_card_type=? and app_identifier=? and t.record_flag=0 order by t.bill_date) where rownum=1" ;
        result = olDbHelper.getFirstDocument(sql, values);

        //更新最小HCE逻辑卡号段序号
        if(result){
            seqNo = olDbHelper.getItemIntValue("seq_no");
            String cityCode = olDbHelper.getItemValue("city_code")!=null?olDbHelper.getItemValue("city_code"):FrameMessageCodeConstant.HCE_LOGCAL_CITY_CODE;
            String industryCode = olDbHelper.getItemValue("industry_code")!=null?olDbHelper.getItemValue("industry_code"):FrameMessageCodeConstant.HCE_LOGCAL_BUSINESS_CODE;
            String appIdentifier = olDbHelper.getItemValue("app_identifier")!=null?olDbHelper.getItemValue("app_identifier"):FrameMessageCodeConstant.IS_TEST_FLAG;
            airSaleVo.setIsTestFlag(appIdentifier);
            String billNo = olDbHelper.getItemValue("bill_no");
            int seqNo1 = seqNo>0?seqNo+1:olDbHelper.getItemIntValue("start_logicno");


            
//            Object[] values1 = {seqNo1, FrameMessageCodeConstant.HCE_LOGCAL_CARD_TYPE, seqNo, billNo};
            Object[] values1 = {seqNo1, FrameMessageCodeConstant.HCE_CARD_LOGIC_NO_MAPPING.get(cardSub), seqNo, billNo};
            sql = "update "+ FrameDBConstant.COM_TK_P + "IC_BC_LOGIC_NO set seq_no=? where blank_card_type=? and record_flag=0 and seq_no=? and bill_no=?";
            //add by zhongzq 2011009
            int endNo = olDbHelper.getItemIntValue("end_logicno");
            if(endNo==seqNo1){
                sql = "update "+ FrameDBConstant.COM_TK_P + "IC_BC_LOGIC_NO set seq_no=?,is_used ='1'  where blank_card_type=? and record_flag=0 and seq_no=? and bill_no=?";
            }
            result = olDbHelper.executeUpdate(sql, values1)>0?true:false;
            
            //插入HCE使用记录
//            if(result){


            if(result){
                String cardIndex = EncryptorJMJUtil.dealStr(String.valueOf(seqNo1), 9);
//                String cardLogicalId = cityCode+industryCode+FrameMessageCodeConstant.HCE_LOGCAL_CARD_TYPE+appIdentifier+cardIndex;
                String cardLogicalId = cityCode+industryCode+FrameMessageCodeConstant.HCE_CARD_LOGIC_NO_MAPPING.get(cardSub)+appIdentifier+cardIndex;
                airSaleVo.setCardLogicalId(cardLogicalId);
                //modify by zhongzq 目前除了手机票 其他都是不可重用
//                Object[] values2 = {airSaleVo.getCardType(), airSaleVo.getPhoneNo(), airSaleVo.getImsi(), airSaleVo.getImei(),
//                    airSaleVo.getCardLogicalId(), airSaleVo.getCardPhyId(), seqNo1, airSaleVo.getDealDay(), airSaleVo.getFaceValue(), airSaleVo.getDepositFee(),
//                    "1", "1", airSaleVo.getIsTestFlag()};
                Object[] values2 = {airSaleVo.getCardType(), airSaleVo.getPhoneNo(), airSaleVo.getImsi(), airSaleVo.getImei(),
                    airSaleVo.getCardLogicalId(), airSaleVo.getCardPhyId(), seqNo1, airSaleVo.getDealDay(), airSaleVo.getFaceValue(), airSaleVo.getDepositFee(),
                    "1", "80".equals(FrameMessageCodeConstant.HCE_CARD_LOGIC_NO_MAPPING.get(cardSub))?"1":"0", airSaleVo.getIsTestFlag()};
                sql = "insert into "+ FrameDBConstant.COM_TK_P + "ic_hce_card_info (water_no, card_type, phone_no, imsi, imei, "
                        + "card_logical_id, card_phy_id, logical_seq, deal_day, face_value, deposit_fee, "
                        + "act_flag, reuse_flag, is_test_flag, insert_date) "
                        + "values (w_acc_tk.w_seq_w_ic_hce_card_info.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate)";
                result = olDbHelper.executeUpdate(sql, values2)>0?true:false;
            }
        }
        
        olDbHelper.commitTran();
        olDbHelper.setAutoCommit(true);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("空发取HCE逻辑卡号耗时：" + (endTime - startTime));
        return result;
    }
    
    /*
    接收确认消息后更新HCE逻辑卡号
    */
    public int updateCardLogicalTK(AirSaleCfmVo airSaleCfmVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("接收确认消息后更新HCE逻辑卡号");
        long startTime = System.currentTimeMillis();
        int result = 0;
        //modify by zhongzqi 20190919
//        Object[] values = {airSaleCfmVo.getImsi(), airSaleCfmVo.getImei(),"2", airSaleCfmVo.getPhoneNo(), airSaleCfmVo.getCardType()};
//        String sql = "update "+ FrameDBConstant.COM_TK_P + "ic_hce_card_info set imsi=?,imei=?,act_flag=? where phone_no=? and card_type=?" ;
        Object[] values = {airSaleCfmVo.getImsi(), airSaleCfmVo.getImei(),"2", airSaleCfmVo.getCardLogicalId(), airSaleCfmVo.getPhoneNo()};

        String sql = "update "+ FrameDBConstant.COM_TK_P + "ic_hce_card_info set imsi=?,imei=?,act_flag=? where card_logical_id=? and phone_no=?" ;
        result = olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("接收确认消息后更新HCE逻辑卡号耗时：" + (endTime - startTime)+"参数："+ Arrays.toString(values)+"更新结果："+result);
        return result;
    }
    
}
