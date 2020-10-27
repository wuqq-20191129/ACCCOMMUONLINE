/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.dao;

import com.goldsign.commu.app.vo.QrPayOrderVo;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.DateHelper;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import com.goldsign.lib.db.util.DbHelper;
import org.apache.log4j.Logger;

import java.sql.SQLException;

/**
 * @author lind
 * 支付二维码
 * @datetime 2017-5-8
 */
public class QrPayDao {
    private final static Object LOCK = new Object();
    private final static Logger logger = Logger.getLogger(QrPayDao.class);

    /*
    支付二维码二维码订单上传请求入库
    */
    public void insertQrPayCreate(QrPayOrderVo qrPayOrderVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("支付二维码二维码订单上传请求入库开始");
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
        //modify by zhongzq 20190108 业务纠正 补充accSeq录入
        Object[] values = {qrPayOrderVo.getWaterNo(), qrPayOrderVo.getMessageId(),
                qrPayOrderVo.getMsgGenTime(), qrPayOrderVo.getTerminalNo(),
                qrPayOrderVo.getSamLogicalId(), qrPayOrderVo.getTerminaSeq(), qrPayOrderVo.getAccSeq(),
                qrPayOrderVo.getOrderNo(), qrPayOrderVo.getOrderDate(),
                qrPayOrderVo.getCardType(), qrPayOrderVo.getSaleFee(),
                qrPayOrderVo.getSaleTimes(), qrPayOrderVo.getDealFee()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrpay_create"
                + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq,acc_seq, order_no, order_date, card_type, sale_fee, sale_times, deal_fee, insert_date)"
                + "values (?,?,?,?,?,?,?,?,to_date(?,'yyyymmddhh24miss'),?,?,?,?,sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("70入库耗时:" + (endTime - startTime));
    }

    /*
    支付二维码二维码订单上传请求响应入库
    */
    public void insertQrPayCreateRsp(QrPayOrderVo qrPayOrderVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("支付二维码二维码订单上传请求响应开始");
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
        Object[] values = {"80",
                qrPayOrderVo.getMsgGenTime(), qrPayOrderVo.getTerminalNo(),
                qrPayOrderVo.getSamLogicalId(), qrPayOrderVo.getTerminaSeq(),
                qrPayOrderVo.getAccSeq(), qrPayOrderVo.getOrderNo(),
                qrPayOrderVo.getQrpayId(), qrPayOrderVo.getQrpayData(), qrPayOrderVo.getReturnCode()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrpay_create"
                + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, acc_seq, order_no, qrpay_id, qrpay_data, return_code, insert_date)"
                + "values (" + FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrpay_create.nextval,"
                + "?,?,?,?,?,?,?,?,?,?,sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("80入库耗时:" + (endTime - startTime));
    }

    /*
    取支付标识
    */
    public int qrPayID(DbHelper olDbHelper) throws SQLException {
        logger.debug("查询取支付标识");
        int id = 1;
        boolean result = true;
        String sql = "select max(qrpay_id)+1 qrpay_id from " + FrameDBConstant.COM_OL_P + "ol_qrpay_id";
        result = olDbHelper.getFirstDocument(sql);
        if (result) {
            id = olDbHelper.getItemIntValue("qrpay_id");
        }
        return id;
    }

    /*
    更新支付标识
    */
    public int updateQrPayID(int id, DbHelper olDbHelper) throws SQLException {
        logger.debug("更新支付标识");
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
        String sql = "update " + FrameDBConstant.COM_OL_P + "ol_qrpay_id set qrpay_id = ?";
        Object[] values = {id};
        return olDbHelper.executeUpdate(sql, values);
    }

    /*
    查询订单重复
    */
    public boolean checkOrderNo(QrPayOrderVo qrPayOrderVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("查询订单重复开始");
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
        boolean result = true;
        Object[] values = {qrPayOrderVo.getOrderNo()};
        //增加缓存表机制 zhongzq 20190823
        String sql = "select 1 from "
                + FrameDBConstant.COM_OL_P + "ol_buf_qrpay_order where order_no=?";
        result = olDbHelper.getFirstDocument(sql, values);
        if (!result) {
            sql = "select 1 from "
                    + FrameDBConstant.COM_OL_P + "ol_qrpay_order where order_no=?";
            result = olDbHelper.getFirstDocument(sql, values);
        }
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("查询订单重复耗时:" + (endTime - startTime));
        return result;
    }

    /*
    支付二维码订单状态查询请求入库
    */
    public void insertQrPayStatus(QrPayOrderVo qrPayOrderVo, DbHelper olDbHelper) throws SQLException {
        logger.debug("支付二维码订单状态查询请求入库开始");
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
        //modify by zhongzq 业务纠正 增加accSeq 20180108
//        Object[] values = {qrPayOrderVo.getWaterNo(), qrPayOrderVo.getMessageId(),
//                qrPayOrderVo.getMsgGenTime(), qrPayOrderVo.getTerminalNo(), qrPayOrderVo.getTerminaSeq(),
//                qrPayOrderVo.getPhoneNo(), qrPayOrderVo.getImsi(), qrPayOrderVo.getImei(), qrPayOrderVo.getAppCode(),
//                qrPayOrderVo.getQrpayId(), qrPayOrderVo.getQrpayData()
//        };
//        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrpay_orstatus "
//                + "(water_no, message_id, msg_gen_time, termina_no, hce_seq, phone_no, imsi, imei, app_code, qrpay_id, qrpay_data,insert_date)"
//                + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate)";
//        olDbHelper.executeUpdate(sql, values);
        Object[] values = {qrPayOrderVo.getWaterNo(), qrPayOrderVo.getMessageId(),
                qrPayOrderVo.getMsgGenTime(), qrPayOrderVo.getTerminalNo(), qrPayOrderVo.getTerminaSeq(), qrPayOrderVo.getAccSeq(),
                qrPayOrderVo.getPhoneNo(), qrPayOrderVo.getImsi(), qrPayOrderVo.getImei(), qrPayOrderVo.getAppCode(),
                qrPayOrderVo.getQrpayId(), qrPayOrderVo.getQrpayData()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrpay_orstatus "
                + "(water_no, message_id, msg_gen_time, termina_no, hce_seq, acc_seq,phone_no, imsi, imei, app_code, qrpay_id, qrpay_data,insert_date)"
                + " values (?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("71入库耗时:" + (endTime - startTime));
    }

    /*
    支付二维码订单状态查询响应入库
    */
    public void insertQrPayStatusRsp(QrPayOrderVo qrPayOrderVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("支付二维码订单状态查询请求响应入库开始");
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
        //modify by zhongzq 业务纠正 增加accSeq 20180108
//        Object[] values = {qrPayOrderVo.getMessageId(), qrPayOrderVo.getMsgGenTime(), qrPayOrderVo.getTerminalNo(),
//                qrPayOrderVo.getTerminaSeq(), qrPayOrderVo.getPhoneNo(), qrPayOrderVo.getImsi(), qrPayOrderVo.getImei(),
//                qrPayOrderVo.getAppCode(), qrPayOrderVo.getQrpayId(), qrPayOrderVo.getQrpayData(), qrPayOrderVo.getOrderNo(),
//                qrPayOrderVo.getOrderDate().equals("00000000000000") ? DateHelper.currentTodToString() : qrPayOrderVo.getOrderDate(),
//                qrPayOrderVo.getCardType(), qrPayOrderVo.getSaleFee(), qrPayOrderVo.getSaleTimes(), qrPayOrderVo.getDealFee(),
//                qrPayOrderVo.getCardTypeTotal(), qrPayOrderVo.getSaleFeeTotal(), qrPayOrderVo.getSaleTimesTotal(), qrPayOrderVo.getDealFeeTotal(),
//                qrPayOrderVo.getStatus()
//        };
//        qrPayOrderVo.toString();
//        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrpay_orstatus"
//                + "(water_no, message_id, msg_gen_time, termina_no, hce_seq, phone_no, imsi, imei, app_code, qrpay_id, qrpay_data, order_no, order_date, "
//                + "card_type, sale_fee, sale_times, deal_fee, card_type_total, sale_fee_total, sale_times_total, deal_fee_total, status, insert_date)"
//                + "values (" + FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrpay_orstatus.nextval,"
//                + "?,?,?,?,?,?,?,?,?,?,?,to_date(?,'yyyymmddhh24miss'),?,?,?,?,?,?,?,?,?,sysdate)";
        Object[] values = {"81", qrPayOrderVo.getMsgGenTime(), qrPayOrderVo.getTerminalNo(),
                qrPayOrderVo.getTerminaSeq(), qrPayOrderVo.getAccSeq(), qrPayOrderVo.getPhoneNo(), qrPayOrderVo.getImsi(), qrPayOrderVo.getImei(),
                qrPayOrderVo.getAppCode(), qrPayOrderVo.getQrpayId(), qrPayOrderVo.getQrpayData(), qrPayOrderVo.getOrderNo(),
                qrPayOrderVo.getOrderDate().equals("00000000000000") ? DateHelper.currentTodToString() : qrPayOrderVo.getOrderDate(),
                qrPayOrderVo.getCardType(), qrPayOrderVo.getSaleFee(), qrPayOrderVo.getSaleTimes(), qrPayOrderVo.getDealFee(),
                qrPayOrderVo.getCardTypeTotal(), qrPayOrderVo.getSaleFeeTotal(), qrPayOrderVo.getSaleTimesTotal(), qrPayOrderVo.getDealFeeTotal(),
                qrPayOrderVo.getStatus()
        };
        qrPayOrderVo.toString();
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrpay_orstatus"
                + "(water_no, message_id, msg_gen_time, termina_no, hce_seq,acc_seq, phone_no, imsi, imei, app_code, qrpay_id, qrpay_data, order_no, order_date, "
                + "card_type, sale_fee, sale_times, deal_fee, card_type_total, sale_fee_total, sale_times_total, deal_fee_total, status, insert_date)"
                + "values (" + FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrpay_orstatus.nextval,"
                + "?,?,?,?,?,?,?,?,?,?,?,?,to_date(?,'yyyymmddhh24miss'),?,?,?,?,?,?,?,?,?,sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("81入库耗时:" + (endTime - startTime));
    }

    /*
    查询支付二维码订单状态
    */
    public QrPayOrderVo qrPayOrder(QrPayOrderVo qrPayOrderVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("查询二维码订单状态开始");
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
        boolean result = false;
        QrPayOrderVo queryVo = new QrPayOrderVo();
        Object[] values = {qrPayOrderVo.getQrpayId()};
        //modify by zhongzq  20181228 增加订单有效时间
        //modify by zhongzq 20190823 增加缓存表机制
        String sql = "select acc_seq, order_no, status, to_char(order_date,'yyyymmddhh24miss') order_date,"
                + " nvl(trim(card_type),'0000') card_type, nvl(trim(card_type_total),'0000') card_type_total, qrpay_id, qrpay_data, to_char(pay_date,'yyyymmddhh24miss'),"
                + " pay_status, nvl(sale_fee,0) sale_fee, nvl(sale_times,0) sale_times, nvl(deal_fee,0) deal_fee, pay_date,"
                + "nvl(sale_fee_total,0) sale_fee_total, nvl(sale_times_total,0) sale_times_total, nvl(deal_fee_total,0) deal_fee_total, order_ip, to_char(valid_time,'yyyymmddhh24miss') valid_time,last_status from "
                + FrameDBConstant.COM_OL_P + "ol_buf_qrpay_order where qrpay_id=?";
        result = olDbHelper.getFirstDocument(sql, values);
        if (!result) {
            sql = "select acc_seq, order_no, status, to_char(order_date,'yyyymmddhh24miss') order_date,"
                    + " nvl(trim(card_type),'0000') card_type, nvl(trim(card_type_total),'0000') card_type_total, qrpay_id, qrpay_data, to_char(pay_date,'yyyymmddhh24miss'),"
                    + " pay_status, nvl(sale_fee,0) sale_fee, nvl(sale_times,0) sale_times, nvl(deal_fee,0) deal_fee, pay_date,"
                    + "nvl(sale_fee_total,0) sale_fee_total, nvl(sale_times_total,0) sale_times_total, nvl(deal_fee_total,0) deal_fee_total, order_ip, to_char(valid_time,'yyyymmddhh24miss') valid_time,last_status from "
                    + FrameDBConstant.COM_OL_P + "ol_qrpay_order where qrpay_id=?";
            result = olDbHelper.getFirstDocument(sql, values);
        }
        if (result) {
            queryVo.setAccSeq(olDbHelper.getItemLongValue("acc_seq"));
            queryVo.setOrderNo(olDbHelper.getItemValue("order_no"));
            queryVo.setStatus(olDbHelper.getItemValue("status"));
            queryVo.setOrderDate(olDbHelper.getItemValue("order_date"));
            queryVo.setCardType(olDbHelper.getItemValue("card_type"));
            String cardType = olDbHelper.getItemValue("card_type");
            queryVo.setCardType(cardType.equals("") ? "0000" : cardType);
            String cardTypeTotal = olDbHelper.getItemValue("card_type_total");
            queryVo.setCardTypeTotal(cardTypeTotal.equals("") ? "0000" : cardTypeTotal);
            queryVo.setQrpayId(olDbHelper.getItemValue("qrpay_id"));
            queryVo.setQrpayData(olDbHelper.getItemValue("qrpay_data"));
            queryVo.setPayDate(olDbHelper.getItemValue("pay_date"));
            queryVo.setPayStatus(olDbHelper.getItemValue("pay_status"));
            queryVo.setSaleFeeTotal(olDbHelper.getItemLongValue("sale_fee_total"));
            queryVo.setSaleTimesTotal(olDbHelper.getItemLongValue("sale_times_total"));
            queryVo.setDealFeeTotal(olDbHelper.getItemLongValue("deal_fee_total"));
            queryVo.setSaleFee(olDbHelper.getItemLongValue("sale_fee"));
            queryVo.setSaleTimes(olDbHelper.getItemLongValue("sale_times"));
            queryVo.setDealFee(olDbHelper.getItemLongValue("deal_fee"));
            queryVo.setOrderIp(olDbHelper.getItemValue("order_ip"));
            //add by zhongzq  20181228 增加订单有效时间
            queryVo.setValidTime(olDbHelper.getItemValue("valid_time"));
            queryVo.setLastStatus(olDbHelper.getItemValue("last_status"));
        } else {
            queryVo.setStatus("03");//订单不存在
        }
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("查询订单耗时:" + (endTime - startTime));
        return queryVo;
    }

    /*
    支付二维码订单入库
    */
    public void insertQrPayOrder(QrPayOrderVo qrPayOrderVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("支付二维码订单开始");
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
        //modify by zhongzq 20181228 增加订单有效期
        Object[] values = {qrPayOrderVo.getOrderNo(), qrPayOrderVo.getOrderDate(), qrPayOrderVo.getCardType(), qrPayOrderVo.getQrpayId(), qrPayOrderVo.getQrpayData(),
                qrPayOrderVo.getSaleFee(), qrPayOrderVo.getSaleTimes(), qrPayOrderVo.getDealFee(), qrPayOrderVo.getAccSeq(), qrPayOrderVo.getOrderIp(), qrPayOrderVo.getValidTime()
        };
        //status: 0:未支付1:已支付2:订单取消
        //增加缓存表机制 20190823 zhongzq
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_buf_qrpay_order"
                + " (water_no, order_no, status, order_date, card_type_total, qrpay_id, qrpay_data, sale_fee_total, sale_times_total, deal_fee_total, acc_seq, order_ip, insert_date，valid_time)"
                + "values (" + FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrpay_order.nextval, ?, '0', to_date(?,'yyyymmddhh24miss'), ?, ?, ?, ?, ?, ?, ?, ?, sysdate,to_date(?,'yyyymmddhh24miss'))";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("订单入库耗时:" + (endTime - startTime));
    }

    /*
    更新支付二维码订单状态
    */
    public int updateQrPayOrder(QrPayOrderVo qrPayOrderVo, DbHelper dbHelper)
            throws SQLException {
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
        //add by zhongzq 增加回滚机制 20190102 补充phone_no 修改status更新状态
//        logger.debug("支付二维码订单更新订单表开始");
        int n = 0;
        dbHelper.setAutoCommit(false);
//        String sql = "update " + FrameDBConstant.COM_OL_P + "ol_qrpay_order set status=?, pay_date=to_date(?,'yyyymmddhh24miss'), pay_status=?, pay_channel_type=?, pay_channel_code=?"
//                + " where qrpay_id=? and qrpay_data=? and status=?";
//        Object[] values = {qrPayOrderVo.getStatus(), qrPayOrderVo.getPayDate(), qrPayOrderVo.getPayStatus(), qrPayOrderVo.getPayChannelType(), qrPayOrderVo.getPayChannelCode(),
//                qrPayOrderVo.getQrpayId(), qrPayOrderVo.getQrpayData(), "0"};
        //增加缓存表机制
        String sql = "update " + FrameDBConstant.COM_OL_P + "ol_buf_qrpay_order set status=?, phone_no = ?,pay_date=to_date(?,'yyyymmddhh24miss'), pay_status=?, pay_channel_type=?, pay_channel_code=?,last_status=?,update_time=sysdate"
                + " where qrpay_id=? and qrpay_data=? and (status='0' or status = '5')";
        Object[] values = {qrPayOrderVo.getStatus(), qrPayOrderVo.getPhoneNo(), qrPayOrderVo.getPayDate(), qrPayOrderVo.getPayStatus(), qrPayOrderVo.getPayChannelType(), qrPayOrderVo.getPayChannelCode(),
                qrPayOrderVo.getLastStatus(), qrPayOrderVo.getQrpayId(), qrPayOrderVo.getQrpayData()};
        n = dbHelper.executeUpdate(sql, values);
        if (n == 0) {
            sql = "update " + FrameDBConstant.COM_OL_P + "ol_qrpay_order set status=?, phone_no = ?,pay_date=to_date(?,'yyyymmddhh24miss'), pay_status=?, pay_channel_type=?, pay_channel_code=?,last_status=?,update_time=sysdate"
                    + " where qrpay_id=? and qrpay_data=? and (status='0' or status = '5')";
            n = dbHelper.executeUpdate(sql, values);
        }
        if (n > 1) {
            dbHelper.rollbackTran();
            StringBuffer exceptionMsg = new StringBuffer();
            exceptionMsg.append("更新订单记录大于1，订单记录数:");
            exceptionMsg.append(n);
            exceptionMsg.append(",qrpay_id=");
            exceptionMsg.append(qrPayOrderVo.getQrpayId());
            exceptionMsg.append(" and qrpay_data=");
            exceptionMsg.append(qrPayOrderVo.getQrpayData());
            throw new SQLException(exceptionMsg.toString());
        } else {
            dbHelper.commitTran();
            dbHelper.setAutoCommit(true);
        }
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("更新订单耗时:" + (endTime - startTime));
        return n;
    }


    /*
    更新支付二维码订单出票情况
    */
    public int updateQrPayOrderDown(QrPayOrderVo qrPayOrderVo, DbHelper dbHelper)
            throws SQLException {
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
//        logger.debug("更新支付二维码订单出票情况开始");
        int result = 0;
        //增加缓存表机制
        String sql = "update " + FrameDBConstant.COM_OL_P + "ol_buf_qrpay_order set sale_fee=?, sale_times=?, deal_fee=?, card_type=?,update_time=sysdate "
                + " where qrpay_id=? and qrpay_data=?";
        Object[] values = {qrPayOrderVo.getSaleFee(), qrPayOrderVo.getSaleTimes(), qrPayOrderVo.getDealFee(), qrPayOrderVo.getCardType(),
                qrPayOrderVo.getQrpayId(), qrPayOrderVo.getQrpayData()};
        result = dbHelper.executeUpdate(sql, values);
        if (result == 0) {
            sql = "update " + FrameDBConstant.COM_OL_P + "ol_qrpay_order set sale_fee=?, sale_times=?, deal_fee=?, card_type=?,update_time=sysdate "
                    + " where qrpay_id=? and qrpay_data=?";
            result = dbHelper.executeUpdate(sql, values);
        }
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("更新出票情况订单耗时:" + (endTime - startTime));
        return result;
    }


    /*
    支付二维码订单app支付结果请求入库
    */
    public void insertQrPayApp(QrPayOrderVo qrPayOrderVo, DbHelper olDbHelper) throws SQLException {
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
//        logger.debug("支付二维码订单app支付结果请求入库开始");
        //modify by zhongzq 20180108 增加accSeq
//        Object[] values = {qrPayOrderVo.getWaterNo(), qrPayOrderVo.getMessageId(),
//                qrPayOrderVo.getMsgGenTime(), qrPayOrderVo.getTerminalNo(), qrPayOrderVo.getTerminaSeq(),
//                qrPayOrderVo.getPhoneNo(), qrPayOrderVo.getImsi(), qrPayOrderVo.getImei(), qrPayOrderVo.getAppCode(),
//                qrPayOrderVo.getQrpayId(), qrPayOrderVo.getQrpayData(), qrPayOrderVo.getPayDate(), qrPayOrderVo.getPayStatus(),
//                qrPayOrderVo.getPayChannelType(), qrPayOrderVo.getPayChannelCode()
//        };
//        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrpay_app "
//                + "(water_no, message_id, msg_gen_time, termina_no, hce_seq, phone_no, imsi, imei, app_code, qrpay_id, qrpay_data, pay_date, pay_status,"
//                + " pay_channel_type, pay_channel_code, insert_date)"
//                + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, to_date(?,'yyyymmddhh24miss'), ?, ?, ?, sysdate)";
        Object[] values = {qrPayOrderVo.getWaterNo(), qrPayOrderVo.getMessageId(),
                qrPayOrderVo.getMsgGenTime(), qrPayOrderVo.getTerminalNo(), qrPayOrderVo.getTerminaSeq(), qrPayOrderVo.getAccSeq(),
                qrPayOrderVo.getPhoneNo(), qrPayOrderVo.getImsi(), qrPayOrderVo.getImei(), qrPayOrderVo.getAppCode(),
                qrPayOrderVo.getQrpayId(), qrPayOrderVo.getQrpayData(), qrPayOrderVo.getPayDate(), qrPayOrderVo.getPayStatus(),
                qrPayOrderVo.getPayChannelType(), qrPayOrderVo.getPayChannelCode()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrpay_app "
                + "(water_no, message_id, msg_gen_time, termina_no, hce_seq,acc_seq, phone_no, imsi, imei, app_code, qrpay_id, qrpay_data, pay_date, pay_status,"
                + " pay_channel_type, pay_channel_code, insert_date)"
                + " values (?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, to_date(?,'yyyymmddhh24miss'), ?, ?, ?, sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("72入库耗时:" + (endTime - startTime));
    }

    /*
    支付二维码订单app支付结果响应入库
    */
    public int insertQrPayAppRsp(QrPayOrderVo qrPayOrderVo, DbHelper olDbHelper) throws SQLException {
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
        logger.debug("支付二维码订单app支付结果请求响应入库开始");
        int reslut = 0;
        Object[] values = {"82", qrPayOrderVo.getMsgGenTime(), qrPayOrderVo.getTerminalNo(),
                qrPayOrderVo.getTerminaSeq(), qrPayOrderVo.getAccSeq(), qrPayOrderVo.getQrpayId(), qrPayOrderVo.getQrpayData(), qrPayOrderVo.getReturnCode()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrpay_app"
                + "(water_no, message_id, msg_gen_time, termina_no, hce_seq, acc_seq, qrpay_id, qrpay_data, return_code, insert_date)"
                + "values (" + FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrpay_app.nextval,"
                + "?,?,?,?,?,?,?,?,sysdate)";
        reslut = olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("82入库耗时:" + (endTime - startTime));
        return reslut;
    }


    /*
    支付二维码订单支付结果下发请求入库
    */
    public void insertQrPayDown(QrPayOrderVo qrPayOrderVo, DbHelper olDbHelper) throws SQLException {
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
//        logger.debug("支付二维码订单支付结果下发请求入库开始");
        Object[] values = {qrPayOrderVo.getMessageId(),
                qrPayOrderVo.getMsgGenTime(), qrPayOrderVo.getTerminaSeq(), qrPayOrderVo.getAccSeq(),
                qrPayOrderVo.getOrderNo(), qrPayOrderVo.getQrpayId(), qrPayOrderVo.getQrpayData(),
                qrPayOrderVo.getPayChannelType(), qrPayOrderVo.getPayChannelCode(),
                qrPayOrderVo.getPayDate(), qrPayOrderVo.getPayStatus()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrpay_down "
                + "(water_no, message_id, msg_gen_time, termina_seq, acc_seq, order_no, qrpay_id, qrpay_data, pay_channel_type, pay_channel_code, pay_date, pay_status, insert_date)"
                + " values (" + FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrpay_app.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, to_date(?,'yyyymmddhh24miss'), ?, sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("73入库耗时:" + (endTime - startTime));
    }

    /*
    支付二维码订单支付结果下发请求响应入库
    */
    public int insertQrPayDownRsp(QrPayOrderVo qrPayOrderVo, DbHelper olDbHelper) throws SQLException {
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
//        logger.debug("支付二维码订单支付结果下发请求响应入库开始");
        int reslut = 0;
        Object[] values = {"83", qrPayOrderVo.getMsgGenTime(),
                qrPayOrderVo.getTerminaSeq(), qrPayOrderVo.getAccSeq(), qrPayOrderVo.getOrderNo(),
                qrPayOrderVo.getQrpayId(), qrPayOrderVo.getQrpayData(), qrPayOrderVo.getDealTime(), qrPayOrderVo.getCardType(),
                qrPayOrderVo.getSaleFee(), qrPayOrderVo.getSaleTimes(), qrPayOrderVo.getDealFee(),
                qrPayOrderVo.getReturnCode()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrpay_down"
                + "(water_no, message_id, msg_gen_time, termina_seq, acc_seq, order_no, qrpay_id, qrpay_data, deal_time, card_type, sale_fee, sale_times, deal_fee, return_code, insert_date)"
                + "values (" + FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrpay_app.nextval,"
                + "?,?,?,?,?,?,?,to_date(?,'yyyymmddhh24miss'),?,?,?,?,?,sysdate)";
        reslut = olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("83入库耗时:" + (endTime - startTime));
        return reslut;
    }

    public void insertQrPayRefund(QrPayOrderVo qrPayOrderVo, DbHelper olDbHelper) throws SQLException {
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
        Object[] values = {qrPayOrderVo.getWaterNo(), qrPayOrderVo.getMessageId(),
                qrPayOrderVo.getMsgGenTime(), qrPayOrderVo.getTerminalNo(), qrPayOrderVo.getTerminaSeq(), qrPayOrderVo.getAccSeq(),
                qrPayOrderVo.getPhoneNo(), qrPayOrderVo.getImsi(), qrPayOrderVo.getImei(), qrPayOrderVo.getAppCode(),
                qrPayOrderVo.getQrpayId(), qrPayOrderVo.getQrpayData(), qrPayOrderVo.getPayChannelType(), qrPayOrderVo.getPayChannelCode(),
                qrPayOrderVo.getRefundDate(), qrPayOrderVo.getRefundFee(), qrPayOrderVo.getRefundReason()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrpay_refund "
                + "(water_no, message_id, msg_gen_time, termina_no, hce_seq,acc_seq, phone_no, imsi, imei, app_code, qrpay_id, qrpay_data,"
                + " pay_channel_type, pay_channel_code,refund_date,refund_fee,refund_reason, insert_date)"
                + " values (?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, to_date(?,'yyyymmddhh24miss'), ?, ?, sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("74入库耗时:" + (endTime - startTime));
    }

    public int updateRefundQrPayOrder(QrPayOrderVo constructVo, DbHelper dbHelper)
            throws SQLException {
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("==更新订单退款响应==qrpayId==" + constructVo.getQrpayId());
        int n = 0;
        dbHelper.setAutoCommit(false);
        //增加缓存表机制
        String sql = "update " + FrameDBConstant.COM_OL_P + "ol_buf_qrpay_order set status=?,refund_fee = ?,update_time = sysdate,last_status=? "
                + " where qrpay_id=? and qrpay_data=? and status=?";
        Object[] values = {constructVo.getStatus(), constructVo.getRefundFee(), constructVo.getLastStatus(), constructVo.getQrpayId(), constructVo.getQrpayData(), "1"};
        n = dbHelper.executeUpdate(sql, values);
        if (n == 0) {
            sql = "update " + FrameDBConstant.COM_OL_P + "ol_qrpay_order set status=?,refund_fee = ?,update_time = sysdate,last_status=? "
                    + " where qrpay_id=? and qrpay_data=? and status=?";
            n = dbHelper.executeUpdate(sql, values);
        }
        if (n > 1) {
            dbHelper.rollbackTran();
            StringBuffer exceptionMsg = new StringBuffer();
            exceptionMsg.append("更新订单记录大于1,订单记录数:");
            exceptionMsg.append(n);
            exceptionMsg.append(",qrpay_id=");
            exceptionMsg.append(constructVo.getQrpayId());
            exceptionMsg.append(" and qrpay_data=");
            exceptionMsg.append(constructVo.getQrpayData());
            exceptionMsg.append("and status= '1'");
            throw new SQLException(exceptionMsg.toString());
        } else {
            dbHelper.commitTran();
            dbHelper.setAutoCommit(true);
        }
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("更新订单退款耗时:" + (endTime - startTime));
        return n;
    }


    public void insertQrPayRefundRsp(QrPayOrderVo qrPayOrderVo, DbHelper olDbHelper) throws SQLException {
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
        Object[] values = {"84",
                qrPayOrderVo.getMsgGenTime(), qrPayOrderVo.getTerminalNo(), qrPayOrderVo.getTerminaSeq(), qrPayOrderVo.getAccSeq(),
                qrPayOrderVo.getPhoneNo(), qrPayOrderVo.getImsi(), qrPayOrderVo.getImei(), qrPayOrderVo.getAppCode(),
                qrPayOrderVo.getQrpayId(), qrPayOrderVo.getQrpayData(), qrPayOrderVo.getPayChannelType(), qrPayOrderVo.getPayChannelCode(),
                qrPayOrderVo.getRefundDate(), qrPayOrderVo.getRefundFee(), qrPayOrderVo.getRefundReason(), qrPayOrderVo.getReturnCode(), qrPayOrderVo.getAccHandleTime(), qrPayOrderVo.getUpdateFlag()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrpay_refund "
                + "(water_no, message_id, msg_gen_time, termina_no, hce_seq,acc_seq, phone_no, imsi, imei, app_code, qrpay_id, qrpay_data,"
                + " pay_channel_type, pay_channel_code,refund_date,refund_fee,refund_reason,return_code,acc_handle_time, update_flag,insert_date)"
                + " values (" + FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrpay_refund.nextval," +
                "?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, to_date(?,'yyyymmddhh24miss'), ?,?,?,?,?, sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("84入库耗时:" + (endTime - startTime));
    }

    public void insertQrPayCancel(QrPayOrderVo qrPayOrderVo, DbHelper olDbHelper) throws SQLException {
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
//        logger.debug("支付二维码二维码订单取消请求入库开始");
        Object[] values = {qrPayOrderVo.getWaterNo(), qrPayOrderVo.getMessageId(),
                qrPayOrderVo.getMsgGenTime(), qrPayOrderVo.getTerminalNo(),
                qrPayOrderVo.getSamLogicalId(), qrPayOrderVo.getTerminaSeq(), qrPayOrderVo.getAccSeq(),
                qrPayOrderVo.getOrderNo(), qrPayOrderVo.getOrderDate(),
                qrPayOrderVo.getCardType(), qrPayOrderVo.getSaleFee(),
                qrPayOrderVo.getSaleTimes(), qrPayOrderVo.getDealFee(),
                qrPayOrderVo.getOrderCancelTime(), qrPayOrderVo.getGetOrderCancelReason()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrpay_cancel"
                + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq,acc_seq, order_no, order_date, card_type, sale_fee, sale_times, deal_fee,order_cancel_date,order_cancel_reason, insert_date)"
                + "values (?,?,?,?,?,?,?,?,to_date(?,'yyyymmddhh24miss'),?,?,?,?,to_date(?,'yyyymmddhh24miss'),?,sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("75入库耗时:" + (endTime - startTime));
    }

    public int updateCancelQrPayOrder(QrPayOrderVo constructVo, DbHelper dbHelper) throws SQLException {
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
        int n = 0;
        dbHelper.setAutoCommit(false);
//        logger.info("=====更新取消订单状态=========");
        //增加缓存表机制 20190823 zhongzq
        String sql = "update " + FrameDBConstant.COM_OL_P + "ol_buf_qrpay_order set status='4'，update_time=sysdate ,last_status = ?"
                + " where order_no = ? and (status='0' or status = '1' or status = '5' )";
        Object[] values = {constructVo.getLastStatus(), constructVo.getOrderNo()};
        n = dbHelper.executeUpdate(sql, values);
        if (n == 0) {
            sql = "update " + FrameDBConstant.COM_OL_P + "ol_qrpay_order set status='4'，update_time=sysdate ,last_status = ?"
                    + " where order_no = ? and (status='0' or status = '1' or status = '5' )";
            n = dbHelper.executeUpdate(sql, values);
        }
        if (n > 1) {
            dbHelper.rollbackTran();
            StringBuffer exceptionMsg = new StringBuffer();
            exceptionMsg.append("更新订单记录大于1,订单记录数:");
            exceptionMsg.append(n);
            exceptionMsg.append(",qrpay_id=");
            exceptionMsg.append(constructVo.getQrpayId());
            exceptionMsg.append(" and qrpay_data=");
            exceptionMsg.append(constructVo.getQrpayData());
            exceptionMsg.append("and (status='0' or statu = '1' or statu = '5' )");
            throw new SQLException(exceptionMsg.toString());
        } else {
            dbHelper.commitTran();
            dbHelper.setAutoCommit(true);
        }
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("更新订单取消耗时:" + (endTime - startTime));
        return n;
    }

    public void insertQrPayCancelRsp(QrPayOrderVo qrPayOrderVo, DbHelper olDbHelper) throws SQLException {
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
//        logger.debug("支付二维码二维码订单取消响应入库");
        Object[] values = {"85",
                qrPayOrderVo.getMsgGenTime(), qrPayOrderVo.getTerminalNo(),
                qrPayOrderVo.getSamLogicalId(), qrPayOrderVo.getTerminaSeq(),
                qrPayOrderVo.getOrderNo(), qrPayOrderVo.getOrderDate(),
                qrPayOrderVo.getCardType(), qrPayOrderVo.getSaleFee(),
                qrPayOrderVo.getSaleTimes(), qrPayOrderVo.getDealFee(),
                qrPayOrderVo.getOrderCancelTime(), qrPayOrderVo.getGetOrderCancelReason(),
                qrPayOrderVo.getAccSeq(), qrPayOrderVo.getReturnCode()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrpay_cancel"
                + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, order_no, order_date, card_type, sale_fee, sale_times, deal_fee,order_cancel_date,order_cancel_reason,acc_seq,return_code, insert_date)"
                + "values (" + FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrpay_cancel.nextval" + ",?,?,?,?,?,?,to_date(?,'yyyymmddhh24miss'),?,?,?,?,to_date(?,'yyyymmddhh24miss'),?,?,?,sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("85入库耗时:" + (endTime - startTime));
    }

    public QrPayOrderVo queryByOrderNo(QrPayOrderVo qrPayOrderVo, DbHelper olDbHelper) throws SQLException {
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
//        logger.debug("查询二维码订单状态开始");
        boolean result = true;
        Object[] values = {qrPayOrderVo.getOrderNo()};
        //增加缓存表机制 20190823 zhongzq
        String sql = "select acc_seq, order_no, status, to_char(order_date,'yyyymmddhh24miss') order_date,"
                + " nvl(trim(card_type),'0000') card_type, nvl(trim(card_type_total),'0000') card_type_total, qrpay_id, qrpay_data, to_char(pay_date,'yyyymmddhh24miss'),"
                + " pay_status, nvl(sale_fee,0) sale_fee, nvl(sale_times,0) sale_times, nvl(deal_fee,0) deal_fee, pay_date,"
                + "nvl(sale_fee_total,0) sale_fee_total, nvl(sale_times_total,0) sale_times_total, nvl(deal_fee_total,0) deal_fee_total, order_ip, to_char(valid_time,'yyyymmddhh24miss') valid_time,last_status from "
                + FrameDBConstant.COM_OL_P + "ol_buf_qrpay_order where order_no=?  ";

        result = olDbHelper.getFirstDocument(sql, values);
        if (!result) {
            sql = "select acc_seq, order_no, status, to_char(order_date,'yyyymmddhh24miss') order_date,"
                    + " nvl(trim(card_type),'0000') card_type, nvl(trim(card_type_total),'0000') card_type_total, qrpay_id, qrpay_data, to_char(pay_date,'yyyymmddhh24miss'),"
                    + " pay_status, nvl(sale_fee,0) sale_fee, nvl(sale_times,0) sale_times, nvl(deal_fee,0) deal_fee, pay_date,"
                    + "nvl(sale_fee_total,0) sale_fee_total, nvl(sale_times_total,0) sale_times_total, nvl(deal_fee_total,0) deal_fee_total, order_ip, to_char(valid_time,'yyyymmddhh24miss') valid_time,last_status from "
                    + FrameDBConstant.COM_OL_P + "ol_qrpay_order where order_no=?  ";

            result = olDbHelper.getFirstDocument(sql, values);
        }
        if (result) {
            qrPayOrderVo.setAccSeq(olDbHelper.getItemLongValue("acc_seq"));
            qrPayOrderVo.setOrderNo(olDbHelper.getItemValue("order_no"));
            qrPayOrderVo.setStatus(olDbHelper.getItemValue("status"));
            qrPayOrderVo.setOrderDate(olDbHelper.getItemValue("order_date"));
            qrPayOrderVo.setCardType(olDbHelper.getItemValue("card_type"));
            String cardType = olDbHelper.getItemValue("card_type");
            qrPayOrderVo.setCardType(cardType.equals("") ? "0000" : cardType);
            String cardTypeTotal = olDbHelper.getItemValue("card_type_total");
            qrPayOrderVo.setCardTypeTotal(cardTypeTotal.equals("") ? "0000" : cardTypeTotal);
            qrPayOrderVo.setQrpayId(olDbHelper.getItemValue("qrpay_id"));
            qrPayOrderVo.setQrpayData(olDbHelper.getItemValue("qrpay_data"));
            qrPayOrderVo.setPayDate(olDbHelper.getItemValue("pay_date"));
            qrPayOrderVo.setPayStatus(olDbHelper.getItemValue("pay_status"));
            qrPayOrderVo.setSaleFeeTotal(olDbHelper.getItemLongValue("sale_fee_total"));
            qrPayOrderVo.setSaleTimesTotal(olDbHelper.getItemLongValue("sale_times_total"));
            qrPayOrderVo.setDealFeeTotal(olDbHelper.getItemLongValue("deal_fee_total"));
            qrPayOrderVo.setSaleFee(olDbHelper.getItemLongValue("sale_fee"));
            qrPayOrderVo.setSaleTimes(olDbHelper.getItemLongValue("sale_times"));
            qrPayOrderVo.setDealFee(olDbHelper.getItemLongValue("deal_fee"));
            qrPayOrderVo.setOrderIp(olDbHelper.getItemValue("order_ip"));
            //modify by zhongzq  20181228 增加订单有效时间
            qrPayOrderVo.setValidTime(olDbHelper.getItemValue("valid_time"));
            qrPayOrderVo.setLastStatus(olDbHelper.getItemValue("last_status"));
        } else {
            qrPayOrderVo.setStatus("03");//订单不存在
        }
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("查询订单-order_no耗时:" + (endTime - startTime));
        return qrPayOrderVo;
    }

    public Long getQrPayAccSeq(QrPayOrderVo qrPayOrderVo, DbHelper olDbHelper) throws SQLException {
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
//        logger.debug("查询订单中心流水号");
        boolean result = true;

        Object[] values = {qrPayOrderVo.getQrpayId()};


        long accSeq = 0;
        //增加缓存表机制
        String sql = "select acc_seq from "
                + FrameDBConstant.COM_OL_P + "ol_buf_qrpay_order where qrpay_id=?";
        if ("0000000000".equals(qrPayOrderVo.getQrpayId())) {
            values = new Object[]{qrPayOrderVo.getOrderNo()};
            sql = "select acc_seq from "
                    + FrameDBConstant.COM_OL_P + "ol_buf_qrpay_order where order_no=?";
        }
        result = olDbHelper.getFirstDocument(sql, values);
        if (!result) {
            sql = "select acc_seq from "
                    + FrameDBConstant.COM_OL_P + "ol_qrpay_order where qrpay_id=?";
            if ("0000000000".equals(qrPayOrderVo.getQrpayId())) {
                values = new Object[]{qrPayOrderVo.getOrderNo()};
                sql = "select acc_seq from "
                        + FrameDBConstant.COM_OL_P + "ol_qrpay_order where order_no=?";
            }
            result = olDbHelper.getFirstDocument(sql, values);
        }
        if (result) {
            accSeq = olDbHelper.getItemLongValue("acc_seq");
        }
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("查询订单中心流水号qrpay_id/order_no耗时:" + (endTime - startTime));
        return accSeq;
    }


    public Long getQrPayTerminalNo(QrPayOrderVo qrPayOrderVo, DbHelper olDbHelper) throws SQLException {
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
//        logger.debug("查询终端流水号");
        boolean result = true;
        long terminalSeq = 0;
        Object[] values = {qrPayOrderVo.getOrderNo()};
        String sql = "select termina_seq from "
                + FrameDBConstant.COM_OL_P + "ol_qrpay_create where order_no=?";

        result = olDbHelper.getFirstDocument(sql, values);
        if (result) {
            terminalSeq = olDbHelper.getItemLongValue("termina_seq");
        }
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("查询终端流水号qrpay_id/order_no耗时:" + (endTime - startTime));
        return terminalSeq;
    }

    public static Long getBusinessNo(String businessNoType, DbHelper olDbHelper) throws SQLException {
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
        Long accSeq = 0L;
        synchronized (LOCK) {
            boolean result = true;
            Object[] values = {businessNoType};
            String sql = "select business_no+1 business_no from "
                    + FrameDBConstant.COM_OL_P + "ol_business_no where business_type = ?";
            result = olDbHelper.getFirstDocument(sql, values);

            if (result) {
                accSeq = olDbHelper.getItemLongValue("business_no");
                values = new Object[]{accSeq, businessNoType};
                sql = "update " + FrameDBConstant.COM_OL_P + "ol_business_no set business_no = ? where business_type = ? ";
                olDbHelper.executeUpdate(sql, values);
            } else {
                accSeq = 1L;
                values = new Object[]{accSeq, businessNoType};
                sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_business_no(business_no,business_type) values(?,?)";
                olDbHelper.executeUpdate(sql, values);
            }
        }
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("获取中心流水号=" + accSeq + ",类型==" + businessNoType);
        return accSeq;
    }

    public int updateRefundQrPayOrderByCancel(QrPayOrderVo constructVo, DbHelper dbHelper) throws SQLException {
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
//        logger.info("==更新订单退款响应==qrpayId==" + constructVo.getQrpayId());
        int n = 0;
        dbHelper.setAutoCommit(false);
        //增加缓存表机制 zhongzq 20190823
        String sql = "update " + FrameDBConstant.COM_OL_P + "ol_buf_qrpay_order set status=?,refund_fee = ?,update_time = sysdate,last_status=?"
                + " where qrpay_id=? and qrpay_data=? and status=?";
        Object[] values = {constructVo.getStatus(), constructVo.getRefundFee(), constructVo.getLastStatus(), constructVo.getQrpayId(), constructVo.getQrpayData(), "4"};
        n = dbHelper.executeUpdate(sql, values);
        if(n==0){
            sql = "update " + FrameDBConstant.COM_OL_P + "ol_qrpay_order set status=?,refund_fee = ?,update_time = sysdate,last_status=?"
                    + " where qrpay_id=? and qrpay_data=? and status=?";
            n = dbHelper.executeUpdate(sql, values);
        }
        if (n > 1) {
            dbHelper.rollbackTran();
            StringBuffer exceptionMsg = new StringBuffer();
            exceptionMsg.append("更新订单记录大于1,订单记录数:");
            exceptionMsg.append(n);
            exceptionMsg.append(",qrpay_id=");
            exceptionMsg.append(constructVo.getQrpayId());
            exceptionMsg.append(" and qrpay_data=");
            exceptionMsg.append(constructVo.getQrpayData());
            exceptionMsg.append("and status= '4'");
            throw new SQLException(exceptionMsg.toString());
        } else {
            dbHelper.commitTran();
            dbHelper.setAutoCommit(true);
        }
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("更新订单退款" + constructVo.getQrpayId() + "耗时:" + (endTime - startTime));
        return n;
    }

    public int updateQrPayOrderByCancelScene(QrPayOrderVo qrPayOrderVo, DbHelper dbHelper) throws SQLException {
        //增加耗时统计 20190822 zhongzq
        long startTime = System.currentTimeMillis();
//        logger.debug("支付二维码订单更新订单表开始");
        int n = 0;
        dbHelper.setAutoCommit(false);
        //增加缓存表机制 20190823 zhongzq
        String sql = "update " + FrameDBConstant.COM_OL_P + "ol_buf_qrpay_order set  phone_no = ?,pay_date=to_date(?,'yyyymmddhh24miss'), pay_status=?, pay_channel_type=?, pay_channel_code=?,update_time = sysdate"
                + " where qrpay_id=? and qrpay_data=? and status='4'";
        Object[] values = {qrPayOrderVo.getPhoneNo(), qrPayOrderVo.getPayDate(), qrPayOrderVo.getPayStatus(), qrPayOrderVo.getPayChannelType(), qrPayOrderVo.getPayChannelCode(),
                qrPayOrderVo.getQrpayId(), qrPayOrderVo.getQrpayData()};
        n = dbHelper.executeUpdate(sql, values);
        if(n==0){
            sql = "update " + FrameDBConstant.COM_OL_P + "ol_qrpay_order set  phone_no = ?,pay_date=to_date(?,'yyyymmddhh24miss'), pay_status=?, pay_channel_type=?, pay_channel_code=?,update_time = sysdate"
                    + " where qrpay_id=? and qrpay_data=? and status='4'";
            n = dbHelper.executeUpdate(sql, values);
        }
        if (n > 1) {
            dbHelper.rollbackTran();
            StringBuffer exceptionMsg = new StringBuffer();
            exceptionMsg.append("更新订单记录大于1，订单记录数:");
            exceptionMsg.append(n);
            exceptionMsg.append(",qrpay_id=");
            exceptionMsg.append(qrPayOrderVo.getQrpayId());
            exceptionMsg.append(" and qrpay_data=");
            exceptionMsg.append(qrPayOrderVo.getQrpayData());
            exceptionMsg.append("and status=");
            exceptionMsg.append("4");
            throw new SQLException(exceptionMsg.toString());
        } else {
            dbHelper.commitTran();
            dbHelper.setAutoCommit(true);
        }
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("更新订单退款-取消情形耗时:" + (endTime - startTime));
        return n;
    }
}
