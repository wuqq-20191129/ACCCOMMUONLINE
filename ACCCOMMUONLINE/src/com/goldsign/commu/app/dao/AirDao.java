/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.dao;

import com.goldsign.commu.app.vo.AirChargeCfmVo;
import com.goldsign.commu.app.vo.AirChargeVo;
import com.goldsign.commu.app.vo.AirSaleCfmVo;
import com.goldsign.commu.app.vo.AirSaleVo;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import com.goldsign.lib.db.util.DbHelper;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * @datetime 2017-12-6 15:32:45
 * @author lind
 * HCE空中交易DAO
 */
public class AirDao {
    
    private final static Logger logger = Logger.getLogger(AirDao.class);
    
    /*
    空中发售申请入库
    */
    public void insertAirSale(AirSaleVo vo, DbHelper olDbHelper) throws Exception{
        long startTime = System.currentTimeMillis();
//        logger.debug("空中发售申请入库开始");
        Object[] values = {vo.getWaterNo(), vo.getMessageId(),
            vo.getMsgGenTime(), vo.getTerminaNo(), vo.getSamLogicalId(), vo.getTerminaSeq(),
            vo.getBranchesCode(), vo.getCardType(),
            vo.getPhoneNo(), vo.getImsi(), vo.getImei(), vo.getAppCode()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_air_sale"
            + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, branches_code, card_type, phone_no, imsi, imei, app_code, insert_date)"
            + "values (?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("30入库耗时:" + (endTime - startTime));
    }
    
    /*
    空中发售申请响应入库
    */
    public void insertAirSaleResp(AirSaleVo vo, DbHelper olDbHelper) throws SQLException{
        long startTime = System.currentTimeMillis();
//        logger.debug("空中发售申请响应入库开始");
        Object[] values = {"40", vo.getMsgGenTime(), vo.getSysRefNo(), vo.getTerminaNo(), vo.getSamLogicalId(), vo.getTerminaSeq(), vo.getBranchesCode(), vo.getCardType(),
            vo.getPhoneNo(), vo.getImsi(), vo.getImei(), vo.getProductCode(), vo.getCityCode(), vo.getBusinessCode(), vo.getDealDay(), vo.getDealDevCode(), vo.getCardVer(), vo.getCardDay(),
            vo.getCardAppDay(), vo.getCardAppVer(), vo.getCardLogicalId(), vo.getCardPhyId(), vo.getExpiryDate(), vo.getFaceValue(), vo.getDepositFee(), vo.getAppExpiryStart(),vo.getAppExpiryDay(),
            vo.getSaleActFlag(), vo.getIsTestFlag(), vo.getChargeLimit(), vo.getLimitMode(), vo.getLimitEntryStation(), vo.getLimitExitStation(), vo.getCardMac(), vo.getReturnCode(),vo.getErrCode(), vo.getAppCode()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_air_sale"
            + "(water_no, message_id, msg_gen_time, sys_ref_no, termina_no, sam_logical_id, termina_seq, branches_code, card_type,"
                + " phone_no, imsi, imei, product_code, city_code, business_code, deal_day, deal_dev_code, card_ver, card_day,"
                + " card_app_day, card_app_ver, card_logical_id, card_phy_id, expiry_date, face_value, deposit_fee, app_expiry_start, app_expiry_day,"
                + " sale_act_flag, is_test_flag, charge_limit, "
                + "limit_mode, limit_entry_station, limit_exit_station, card_mac, return_code, err_code, app_code, insert_date)"
            + "values ("+FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_air_sale.nextval,"
                + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("40入库耗时:" + (endTime - startTime));
    }
    
    /*
    查询空中发售申请响应
    */
    public boolean checkAirSale(AirSaleCfmVo airSaleCfmVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("查询空中发售申请响应开始");
        long startTime = System.currentTimeMillis();
        boolean result = false;
        Object[] values = {airSaleCfmVo.getTerminaSeq(), airSaleCfmVo.getSysRefNo(), airSaleCfmVo.getIsTestFlag(), airSaleCfmVo.getPhoneNo()};
        String sql = "select sam_logical_id, termina_seq, card_logical_id, card_phy_id from " 
                + FrameDBConstant.COM_OL_P + "ol_air_sale where message_id='40' and termina_seq=? and sys_ref_no=? and is_test_flag=? and phone_no=? and return_code='00'";
        result = olDbHelper.getFirstDocument(sql, values);
        if(result){
            airSaleCfmVo.setSamLogicalId(olDbHelper.getItemValue("sam_logical_id"));
            airSaleCfmVo.setTerminaSeq(olDbHelper.getItemLongValue("termina_seq"));
            airSaleCfmVo.setCardLogicalId(olDbHelper.getItemValue("card_logical_id"));
        }
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("查询空中发售申请响应41耗时:" + (endTime - startTime));
        return result;
    }
    
    
    /*
    查询空中发售确认
    */
//    public boolean checkAirSaleCfm(String phoneNo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("查询空中发售确认开始");
//        Object[] values = {phoneNo};
//        String sql = "select * from " + FrameDBConstant.COM_OL_P + "OL_AIR_SALE_CFM where phone_no=? and message_id='31'";
//        return olDbHelper.getFirstDocument(sql, values);
//    }
    
    
    /*
    空中发售确认入库
    */
    public void insertAirSaleCfm(AirSaleCfmVo vo, DbHelper olDbHelper) throws SQLException{
//        logger.debug("空中发售确认入库开始");
        long startTime = System.currentTimeMillis();
        Object[] values = {vo.getWaterNo(), vo.getMessageId(),
            vo.getMsgGenTime(), vo.getTerminaNo(), vo.getSamLogicalId(), vo.getTerminaSeq(),
            vo.getBranchesCode(),vo.getIssMainCode(), vo.getIssSubCode(), vo.getPhoneNo(), vo.getImsi(), vo.getImei(), vo.getCardType(),
            vo.getCardLogicalId(), vo.getCardPhyId(), vo.getIsTestFlag(), vo.getResultCode(), vo.getDealTime(), vo.getSysRefNo(), vo.getAppCode()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_air_sale_cfm"
            + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, branches_code, iss_main_code, iss_sub_code, phone_no, imsi, imei,"
                + "card_type, card_logical_id, card_phy_id, is_test_flag, result_code, deal_time, sys_ref_no, app_code, insert_date)"
            + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("31入库耗时:" + (endTime - startTime));
    }
    
    /*
    空中发售确认响应入库
    */
    public int insertAirSaleCfmResp(AirSaleCfmVo vo, DbHelper olDbHelper) throws SQLException{
//        logger.debug("空中发售确认响应入库开始");
        long startTime = System.currentTimeMillis();
        Object[] values = {"41",
            vo.getMsgGenTime(), vo.getTerminaNo(), vo.getSamLogicalId(), vo.getTerminaSeq(),
            vo.getCardLogicalId(), vo.getCardPhyId(), vo.getIsTestFlag(), vo.getSysRefNo(), vo.getPhoneNo(), vo.getReturnCode(), vo.getErrCode(), vo.getAppCode()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_air_sale_cfm"
            + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq,"
                + "card_logical_id, card_phy_id, is_test_flag, sys_ref_no, phone_no, return_code, err_code, app_code, insert_date)"
            + "values ("+FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_air_sale_cfm.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("41入库耗时:" + (endTime - startTime));
        return olDbHelper.executeUpdate(sql, values);
    }
    
    
    
    /*
    空中充值申请入库
    */
    public void insertAirCharge(AirChargeVo vo, DbHelper olDbHelper) throws SQLException{
//        logger.debug("空中充值申请入库开始");
        long startTime = System.currentTimeMillis();
        Object[] values = {vo.getWaterNo(), vo.getMessageId(),
            vo.getMsgGenTime(), vo.getTerminaNo(), vo.getSamLogicalId(), vo.getTerminaSeq(),
            vo.getBranchesCode(),vo.getIssMainCode(), vo.getIssSubCode(), vo.getCardType(), vo.getCardLogicalId(), vo.getCardPhyId(), 
            vo.getIsTestFlag(), vo.getOnlTranTimes(), vo.getOfflTranTimes(), vo.getBussType(), vo.getValueType(), vo.getChargeFee(), vo.getBalance(), vo.getMac1(), vo.getTkChgeSeq(),
            vo.getLastTranTermno(), vo.getLastTranTime(), vo.getOperatorId(), vo.getPhoneNo(), vo.getPaidChannelType(), vo.getPaidChannelCode(), vo.getSysRefNo()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_air_charge"
            + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, branches_code, iss_main_code, iss_sub_code, card_type, card_logical_id, card_phy_id,"
                + " is_test_flag, onl_tran_times, offl_tran_times, buss_type, value_type, charge_fee, balance, mac1, tk_chge_seq,"
                + " last_tran_termno, last_tran_time, operator_id, phone_no, paid_channel_type, paid_channel_code, sys_ref_no, insert_date)"
            + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";

        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("32入库耗时:" + (endTime - startTime));
    }
    
    /*
    空中充值申请响应入库
    */
    public void insertAirChargeResp(AirChargeVo vo, DbHelper olDbHelper) throws SQLException{
//        logger.debug("空中充值申请响应入库开始");
        long startTime = System.currentTimeMillis();
        Object[] values = {"42", vo.getMsgGenTime(), vo.getTerminaNo(), vo.getSamLogicalId(), vo.getTerminaSeq(),
            vo.getCardType(), vo.getCardLogicalId(), vo.getIsTestFlag(), vo.getPhoneNo(), vo.getMac2(), vo.getDealTime(), vo.getSysRefNo(), vo.getReturnCode(), vo.getErrCode(),vo.getOnlTranTimes()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_air_charge"
            + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, card_type, card_logical_id, is_test_flag,"
                + " phone_no, mac2, deal_time, sys_ref_no, return_code, err_code, insert_date, onl_tran_times)"
            + "values ("+FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_air_charge.nextval,"
                + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("42入库耗时:" + (endTime - startTime));

    }
    
    
    /*
    空中充值确认入库
    */
    public void insertAirChargeCfm(AirChargeCfmVo vo, DbHelper olDbHelper) throws SQLException{
//        logger.debug("空中充值确认入库开始");
        long startTime = System.currentTimeMillis();
        Object[] values = {vo.getWaterNo(), vo.getMessageId(),
            vo.getMsgGenTime(), vo.getTerminaNo(), vo.getSamLogicalId(), vo.getTerminaSeq(),
            vo.getBranchesCode(),vo.getIssMainCode(), vo.getIssSubCode(), vo.getCardType(), vo.getCardLogicalId(), vo.getCardPhyId(), 
            vo.getIsTestFlag(), vo.getOnlTranTimes(), vo.getOfflTranTimes(), vo.getBussType(), vo.getValueType(), vo.getChargeFee(), vo.getBalance(), vo.getTac(), vo.getOperatorId(),
            vo.getResultCode(), vo.getDealTime(), vo.getSysRefNo()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_air_charge_cfm"
            + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, branches_code, iss_main_code, iss_sub_code, card_type, card_logical_id, card_phy_id,"
                + " is_test_flag, onl_tran_times, offl_tran_times, buss_type, value_type, charge_fee, balance, tac, operator_id,"
                + " result_code, deal_time, sys_ref_no, insert_date)"
            + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("33入库耗时:" + (endTime - startTime));
    }
    
    /*
    空中充值确认响应入库
    */
    public int insertAirChargeCfmResp(AirChargeCfmVo vo, DbHelper olDbHelper) throws SQLException{
//        logger.debug("空中充值确认响应入库开始");
        int result = 0;
        long startTime = System.currentTimeMillis();
        Object[] values = {"43", vo.getMsgGenTime(), vo.getTerminaNo(), vo.getSamLogicalId(), vo.getTerminaSeq(),
            vo.getCardType(), vo.getCardLogicalId(), vo.getIsTestFlag(), vo.getSysRefNo(), vo.getReturnCode(), vo.getErrCode()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_air_charge_cfm"
            + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, card_type, card_logical_id, is_test_flag,"
                + " sys_ref_no, return_code, err_code, insert_date)"
            + "values ("+FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_air_charge_cfm.nextval,"
                + "?,?,?,?,?,?,?,?,?,?,?,sysdate)";
        result = olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("43入库耗时:" + (endTime - startTime));
        return result;
    }
    
    /*
    查询空中充值申请响应
    */
    public boolean checkAirCharge(AirChargeCfmVo airChargeCfmVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("查询空中充值申请响应开始");
        long startTime = System.currentTimeMillis();
        boolean result = false;
        Object[] values = {airChargeCfmVo.getTerminaSeq(), airChargeCfmVo.getSysRefNo(), airChargeCfmVo.getIsTestFlag(), airChargeCfmVo.getDealTime()};
        String sql = "select sam_logical_id, termina_seq, card_logical_id, card_phy_id, onl_tran_times from " 
                + FrameDBConstant.COM_OL_P + "ol_air_charge where message_id='42' and termina_seq=? and sys_ref_no=? and is_test_flag=? and deal_time=? and return_code='00'";
        result = olDbHelper.getFirstDocument(sql, values);
        if(result){
//            airChargeCfmVo.setSamLogicalId(olDbHelper.getItemValue("sam_logical_id"));
//            airChargeCfmVo.setTerminaSeq(olDbHelper.getItemLongValue("termina_seq"));
//            airChargeCfmVo.setCardLogicalId(olDbHelper.getItemValue("card_logical_id"));
            airChargeCfmVo.setOnlTranTimes(olDbHelper.getItemLongValue("onl_tran_times"));
        }
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("查询空中充值申请耗时:" + (endTime - startTime));
        return result;
    }
    
    
    /*
    查询空中充值确认
    */
    public AirChargeVo checkAirChargeCfm(AirChargeVo airChargeVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("查询空中充值确认开始");
        long startTime = System.currentTimeMillis();
        AirChargeVo acv = new AirChargeVo();
        boolean result = true;
        Object[] values = {airChargeVo.getCardLogicalId(), airChargeVo.getSysRefNoChr(), airChargeVo.getIsTestFlag()};
        String sql = "select termina_no, card_type, card_logical_id, is_test_flag, buss_type, value_type, charge_fee, balance, phone_no,"
                + " result_code, err_code, deal_time, sys_ref_no from " + FrameDBConstant.COM_OL_P + "ol_air_charge_cfm "
                + " where message_id='33' and card_logical_id=? and sys_ref_no=? and is_test_flag=?";
        result = olDbHelper.getFirstDocument(sql, values);
        if(result){
            acv.setTerminaNo(olDbHelper.getItemValue("termina_no"));
            acv.setIsTestFlag(olDbHelper.getItemValue("is_test_flag"));
            acv.setCardLogicalId(olDbHelper.getItemValue("card_logical_id"));
            acv.setBussType(olDbHelper.getItemValue("buss_type"));
            acv.setValueType(olDbHelper.getItemValue("value_type"));
            acv.setChargeFee(olDbHelper.getItemLongValue("charge_fee"));
            acv.setBalance(olDbHelper.getItemLongValue("balance"));
            acv.setDealTime(olDbHelper.getItemValue("deal_time"));
            acv.setSysRefNo(olDbHelper.getItemLongValue("sys_ref_no"));
        }
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("查询空中充值确认耗时:" + (endTime - startTime));
        return acv;
    }
    
    
    /*
    空中充值撤销申请入库
    */
    public void insertAirChargeCancel(AirChargeVo vo, DbHelper olDbHelper) throws SQLException{
//        logger.debug("空中充值撤销申请入库开始");
        long startTime = System.currentTimeMillis();
        Object[] values = {vo.getWaterNo(), vo.getMessageId(),
            vo.getMsgGenTime(), vo.getTerminaNo(), vo.getSamLogicalId(), vo.getTerminaSeq(),
            vo.getBranchesCode(),vo.getIssMainCode(), vo.getIssSubCode(), vo.getCardType(), vo.getCardLogicalId(), vo.getCardPhyId(), 
            vo.getIsTestFlag(), vo.getOnlTranTimes(), vo.getOfflTranTimes(), vo.getBussType(), vo.getValueType(), vo.getChargeFee(), vo.getBalance(), 
            vo.getLastTranTermno(), vo.getLastTranTime(), vo.getOperatorId(), vo.getPhoneNo(), vo.getPaidChannelType(), vo.getPaidChannelCode(), vo.getSysRefNo(), vo.getSysRefNoChr()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_air_charge_cancel"
            + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, branches_code, iss_main_code, iss_sub_code, card_type, card_logical_id, card_phy_id,"
                + " is_test_flag, onl_tran_times, offl_tran_times, buss_type, value_type, charge_fee, balance,"
                + " last_tran_termno, last_tran_time, operator_id, phone_no, paid_channel_type, paid_channel_code, sys_ref_no, sys_ref_no_chr, insert_date)"
            + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("34入库耗时:" + (endTime - startTime));
    }
    
    /*
    空中充值撤销申请响应入库
    */
    public void insertAirChargeCancelResp(AirChargeVo vo, DbHelper olDbHelper) throws SQLException{
//        logger.debug("空中充值撤销申请响应入库开始");
        long startTime = System.currentTimeMillis();
        Object[] values = {"44", vo.getMsgGenTime(), vo.getTerminaNo(), vo.getSamLogicalId(), vo.getTerminaSeq(),
            vo.getCardType(), vo.getCardLogicalId(), vo.getIsTestFlag(), vo.getPhoneNo(), vo.getDealTime(), vo.getSysRefNo(), 
            vo.getReturnCode(), vo.getErrCode(),vo.getOnlTranTimes()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_air_charge_cancel"
            + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, card_type, card_logical_id, is_test_flag,"
                + " phone_no, deal_time, sys_ref_no, return_code, err_code, insert_date, onl_tran_times)"
            + "values ("+FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_air_charge_cancel.nextval,"
                + "?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("44入库耗时:" + (endTime - startTime));
    }
    
    
    /*
    空中充值撤销确认入库
    */
    public void insertAirChargeCancelCfm(AirChargeCfmVo vo, DbHelper olDbHelper) throws SQLException{
//        logger.debug("空中充值撤销确认入库开始");
        long startTime = System.currentTimeMillis();
        Object[] values = {vo.getWaterNo(), vo.getMessageId(),
            vo.getMsgGenTime(), vo.getTerminaNo(), vo.getSamLogicalId(), vo.getTerminaSeq(),
            vo.getBranchesCode(),vo.getIssMainCode(), vo.getIssSubCode(), vo.getCardType(), vo.getCardLogicalId(), vo.getCardPhyId(), 
            vo.getIsTestFlag(), vo.getOnlTranTimes(), vo.getOfflTranTimes(), vo.getBussType(), vo.getValueType(), vo.getChargeFee(), vo.getBalance(), vo.getTac(), vo.getOperatorId(),
            vo.getResultCode(), vo.getDealTime(), vo.getSysRefNo(), vo.getSysRefNoChr()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_air_charge_cancel_cfm"
            + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, branches_code, iss_main_code, iss_sub_code, card_type, card_logical_id, card_phy_id,"
                + " is_test_flag, onl_tran_times, offl_tran_times, buss_type, value_type, charge_fee, balance, tac, operator_id,"
                + " result_code, deal_time, sys_ref_no, sys_ref_no_chr, insert_date)"
            + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("35入库耗时:" + (endTime - startTime));
    }
    
    /*
    空中充值撤销确认响应入库
    */
    public int insertAirChargeCLCfmResp(AirChargeCfmVo vo, DbHelper olDbHelper) throws SQLException{
//        logger.debug("空中充值撤销确认响应入库开始");
        long startTime = System.currentTimeMillis();
        int result = 0;
        Object[] values = {"45", vo.getMsgGenTime(), vo.getTerminaNo(), vo.getSamLogicalId(), vo.getTerminaSeq(),
            vo.getCardType(), vo.getCardLogicalId(), vo.getIsTestFlag(), vo.getSysRefNo(), vo.getReturnCode(), vo.getErrCode(), vo.getSysRefNoChr()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_air_charge_cancel_cfm"
            + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, card_type, card_logical_id, is_test_flag,"
                + " sys_ref_no, return_code, err_code, sys_ref_no_chr, insert_date)"
            + "values ("+FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_air_charge_cl_cfm.nextval,"
                + "?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
        result =  olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("34入库耗时:" + (endTime - startTime));
        return result;
    }
    
    /*
    查询空中充值撤销申请响应
    */
    public boolean checkAirChargeCancel(AirChargeCfmVo airChargeCfmVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("查询空中充值撤销申请响应开始");
        long startTime = System.currentTimeMillis();
        boolean result = false;
        Object[] values = {airChargeCfmVo.getTerminaSeq(), airChargeCfmVo.getSysRefNo(), airChargeCfmVo.getIsTestFlag(), airChargeCfmVo.getDealTime()};
        String sql = "select sam_logical_id, termina_seq, card_logical_id, card_phy_id, onl_tran_times from " 
                + FrameDBConstant.COM_OL_P + "ol_air_charge_cancel where message_id='44' and termina_seq=? and sys_ref_no=? and is_test_flag=? and deal_time=? and return_code='00'";
        result = olDbHelper.getFirstDocument(sql, values);
        if(result){
//            airChargeCfmVo.setSamLogicalId(olDbHelper.getItemValue("sam_logical_id"));
//            airChargeCfmVo.setTerminaSeq(olDbHelper.getItemLongValue("termina_seq"));
//            airChargeCfmVo.setCardLogicalId(olDbHelper.getItemValue("card_logical_id"));
//            airChargeCfmVo.setOnlTranTimes(olDbHelper.getItemLongValue("onl_tran_times"));
        }
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("查询空中充值撤销申请响应耗时:" + (endTime - startTime));
        return result;
    }

}
