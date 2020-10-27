/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.dao;

import com.goldsign.commu.app.vo.QrCodeOrderVo;
import com.goldsign.commu.app.vo.QrCodeVo;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.commu.frame.util.ThreadLocalUtil;
import com.goldsign.lib.db.util.DbHelper;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author lind
 * 二维码HCE
 * @datetime 2018-3-15 10:04:33
 */
public class QrCodeHceDao {

    private final static Logger logger = Logger.getLogger(QrCodeHceDao.class);

    /*
    二维码取票码请求入库
    */
    public void insertQrCode(QrCodeVo qrCodeVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("二维码取票码请求入库开始");
        //增加耗时统计 20190821 zhongzq
        long startTime = System.currentTimeMillis();
        Object[] values = {qrCodeVo.getWaterNo(), qrCodeVo.getMessageId(),
                qrCodeVo.getMsgGenTime(), qrCodeVo.getTerminalNo(),
                qrCodeVo.getSamLogicalId(), qrCodeVo.getTerminaSeq(),
                qrCodeVo.getAccSeq(), qrCodeVo.getOrderNo(),
                qrCodeVo.getPhoneNo(), qrCodeVo.getSaleFee(),
                qrCodeVo.getSaleTimes(), qrCodeVo.getDealFee(),
                qrCodeVo.getStartStation(), qrCodeVo.getEndStation()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrcode_tkcode"
                + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, acc_seq, order_no, phone_no, sale_fee, sale_times, deal_fee, insert_date,start_station,end_station)"
                + "values (?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("36入库耗时:" + (endTime - startTime));
//        logger.info("二维码取票码请求-36-入库业务表耗时:" + (endTime - startTime));
    }

    /*
    二维码取票码请求响应入库
    */
    public int insertQrCodeRsp(QrCodeVo qrCodeVo, DbHelper olDbHelper) throws SQLException {
        //增加耗时统计 20190821 zhongzq
        long startTime = System.currentTimeMillis();
//        logger.debug("二维码取票码请求响应开始");
        Object[] values = {"46",
                qrCodeVo.getMsgGenTime(), qrCodeVo.getTerminalNo(),
                qrCodeVo.getSamLogicalId(), qrCodeVo.getTerminaSeq(),
                qrCodeVo.getAccSeq(), qrCodeVo.getReturnCode(), qrCodeVo.getErrCode(),
                qrCodeVo.getOrderNo(), qrCodeVo.getPhoneNo(), qrCodeVo.getValidTime(), qrCodeVo.getQrcode()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrcode_tkcode"
                + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, acc_seq, return_code, err_code, order_no, phone_no, valid_time, qrcode, insert_date)"
                + "values (" + FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrcode_tkcode.nextval,"
                + "?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("46入库耗时:" + (endTime - startTime));
//        logger.info("二维码取票码请求响应-46-入库业务表耗时:" + (endTime - startTime));
        return olDbHelper.executeUpdate(sql, values);
    }

    /*
    二维码订单入库
    */
    public void insertQrCodeOrder(QrCodeOrderVo qrCodeOrderVo, QrCodeVo qrCodeVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("二维码订单入库开始");
        //增加耗时统计 20190821 zhongzq
        long startTime = System.currentTimeMillis();
        Object[] values = {qrCodeVo.getOrderNo(), qrCodeVo.getPhoneNo(), qrCodeVo.getSaleFee(), qrCodeVo.getSaleTimes(), qrCodeVo.getDealFee(),
                qrCodeVo.getTkStatus(), qrCodeOrderVo.getQrcode(), qrCodeVo.getValidTime(), qrCodeOrderVo.getTkcode(), qrCodeVo.getStartStation(), qrCodeVo.getEndStation()
        };
        //入库至缓存表 20190821 zhongzq
//        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrcode_order (water_no, order_no, phone_no, insert_date, sale_fee_total, sale_times_total, deal_fee_total, "
//                + "ticket_status, qrcode, valid_time, tkcode,start_station,end_station)"
//                + "values (" + FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrcode_order.nextval, ?, ?, sysdate, ?, ?, ?, ?, ?, to_date(?,'yyyymmddhh24miss'), ?,?,?)";
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_buf_qrcode_order (water_no, order_no, phone_no, insert_date, sale_fee_total, sale_times_total, deal_fee_total, "
                + "ticket_status, qrcode, valid_time, tkcode,start_station,end_station)"
                + "values (" + FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrcode_order.nextval, ?, ?, sysdate, ?, ?, ?, ?, ?, to_date(?,'yyyymmddhh24miss'), ?,?,?)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("二维码订单入库耗时:" + (endTime - startTime));
//        logger.info("二维码订单入库耗时:" + (endTime - startTime));
    }


    /*
    查询二维码认证请求
    */
//    public boolean qrCode(QrCodeVo qrCodeVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("查询二维码认证请求开始");
//        boolean result = true;
//        Object[] values = {qrCodeVo.getOrderNo(), qrCodeVo.getAccSeq()};
//        String sql = "select 1 from "
//                + FrameDBConstant.COM_OL_P + "ol_qrcode_afc where order_no=? and acc_seq=? and return_code='00' ";
//        result = olDbHelper.getFirstDocument(sql, values);
//        return result;
//    }

    /*
    查询二维码订单状态
    */
    public QrCodeOrderVo qrCodeOrder(QrCodeVo qrCodeVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("查询二维码订单状态开始");
        //增加耗时统计 20190821 zhongzq
        long startTime = System.currentTimeMillis();
        QrCodeOrderVo order = null;
        boolean result = true;
        Object[] values = {qrCodeVo.getOrderNo()};
        //增加缓存表
//        String sql = "select order_no, phone_no, nvl(sale_fee,0) sale_fee, nvl(sale_times,0) sale_times, nvl(deal_fee,0) deal_fee, "
//                + " update_date, insert_date, start_station, end_station, nvl(deal_time,'00000000000000') deal_time, "
//                + "nvl(sale_fee_total,0) sale_fee_total, nvl(sale_times_total,0) sale_times_total, nvl(deal_fee_total,0) deal_fee_total,"
//                + " ticket_status, qrcode, valid_time，start_station,end_station from "
//                + FrameDBConstant.COM_OL_P + "OL_QRCODE_ORDER where order_no=?";
//        result = olDbHelper.getFirstDocument(sql, values);
        String sql = "select order_no, phone_no, nvl(sale_fee,0) sale_fee, nvl(sale_times,0) sale_times, nvl(deal_fee,0) deal_fee, "
                + " update_date, insert_date, start_station, end_station, nvl(deal_time,'00000000000000') deal_time, "
                + "nvl(sale_fee_total,0) sale_fee_total, nvl(sale_times_total,0) sale_times_total, nvl(deal_fee_total,0) deal_fee_total,"
                + " ticket_status, qrcode, valid_time，start_station,end_station from "
                + FrameDBConstant.COM_OL_P + "OL_BUF_QRCODE_ORDER where order_no=?";
        result = olDbHelper.getFirstDocument(sql, values);
        if (!result) {
            sql = "select order_no, phone_no, nvl(sale_fee,0) sale_fee, nvl(sale_times,0) sale_times, nvl(deal_fee,0) deal_fee, "
                    + " update_date, insert_date, start_station, end_station, nvl(deal_time,'00000000000000') deal_time, "
                    + "nvl(sale_fee_total,0) sale_fee_total, nvl(sale_times_total,0) sale_times_total, nvl(deal_fee_total,0) deal_fee_total,"
                    + " ticket_status, qrcode, valid_time，start_station,end_station from "
                    + FrameDBConstant.COM_OL_P + "OL_QRCODE_ORDER where order_no=?";
            result = olDbHelper.getFirstDocument(sql, values);
        }
        if (result) {
            order = new QrCodeOrderVo();
            order.setOrderNo(olDbHelper.getItemValue("order_no"));
            order.setPhoneNo(olDbHelper.getItemValue("phone_no"));
            order.setSaleFee(olDbHelper.getItemLongValue("sale_fee"));
            order.setSaleTimes(olDbHelper.getItemLongValue("sale_times"));
            order.setDealFee(olDbHelper.getItemLongValue("deal_fee"));
            order.setStartStation(olDbHelper.getItemValue("start_station"));
            order.setEndStation(olDbHelper.getItemValue("end_station"));
            order.setInsertDate(olDbHelper.getItemValue("insert_date"));
            order.setSaleFeeTotal(olDbHelper.getItemLongValue("sale_fee_total"));
            order.setSaleTimesTotal(olDbHelper.getItemLongValue("sale_times_total"));
            order.setDealFeeTotal(olDbHelper.getItemLongValue("deal_fee_total"));
            order.setQrcode(olDbHelper.getItemValue("qrcode"));
            order.setTicketStatus(olDbHelper.getItemValue("ticket_status"));
            order.setValidTime(olDbHelper.getItemDateTimeValue("valid_time"));
            order.setDealTime(olDbHelper.getItemValue("deal_time"));
        }
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("HCE查询二维码订单耗时:" + (endTime - startTime));
//        logger.info("HCE查询二维码订单耗时:" + (endTime - startTime));
        return order;
    }

    /*
    二维码状态请求入库
    */
    public void insertQrTkStatus(QrCodeVo qrCodeVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("二维码状态请求入库开始");
        //增加耗时统计 20190821 zhongzq
        long startTime = System.currentTimeMillis();
        Object[] values = {qrCodeVo.getWaterNo(), qrCodeVo.getMessageId(),
                qrCodeVo.getMsgGenTime(), qrCodeVo.getTerminalNo(),
                qrCodeVo.getSamLogicalId(), qrCodeVo.getTerminaSeq(),
                qrCodeVo.getAccSeq(), qrCodeVo.getOrderNo()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrcode_tkstatus "
                + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, acc_seq, order_no, insert_date) values (?, ?, ?, ?, ?, ?, ?, ?, sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
//        logger.info("37入库耗时:" + (endTime - startTime));
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("37入库耗时:" + (endTime - startTime));
    }

    /*
    二维码状态请求响应入库
    */
    public void insertQrTkStatusRsp(QrCodeVo qrCodeVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("二维码状态请求响应入库开始");
        //增加耗时统计 20190821 zhongzq
        long startTime = System.currentTimeMillis();
        Object[] values = {"47", qrCodeVo.getMsgGenTime(), qrCodeVo.getTerminalNo(),
                qrCodeVo.getSamLogicalId(), qrCodeVo.getTerminaSeq(), qrCodeVo.getAccSeq(),
                qrCodeVo.getTkStatus(), qrCodeVo.getOrderNo(), qrCodeVo.getSaleTimes(), qrCodeVo.getTakeTimes(), qrCodeVo.getSaleFee(), qrCodeVo.getValidTime(),
                qrCodeVo.getReturnCode(), qrCodeVo.getErrCode(), qrCodeVo.getDealTime()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrcode_tkstatus"
                + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, acc_seq, "
                + "tk_status, order_no, sale_times, take_times, sale_fee, valid_time, return_code, err_code, deal_time, insert_date)"
                + "values (" + FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrcode_tkstatus.nextval,"
                + "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
//        logger.info("二维码状态请求响应-47-入库业务表耗时:" + (endTime - startTime));
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("47入库耗时:" + (endTime - startTime));
    }

    /*
    二维码订单取消请求入库
    */
    public void insertQrCancel(QrCodeVo qrCodeVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("二维码状态请求入库开始");
        //增加耗时统计 20190821 zhongzq
        long startTime = System.currentTimeMillis();
        Object[] values = {qrCodeVo.getWaterNo(), qrCodeVo.getMessageId(),
                qrCodeVo.getMsgGenTime(), qrCodeVo.getTerminalNo(),
                qrCodeVo.getSamLogicalId(), qrCodeVo.getTerminaSeq(),
                qrCodeVo.getAccSeq(), qrCodeVo.getOrderNo(), qrCodeVo.getDealTime()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrcode_cancel "
                + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, acc_seq, order_no, deal_time, insert_date)"
                + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
//        logger.info("取消-38-入库业务表耗时:" + (endTime - startTime));
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("38入库耗时:" + (endTime - startTime));
    }

    /*
    二维码订单取消请求响应入库
    */
    public int insertQrCancelRsp(QrCodeVo qrCodeVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("二维码状态请求响应入库开始");
        //增加耗时统计 20190821 zhongzq
        long startTime = System.currentTimeMillis();
        Object[] values = {"48", qrCodeVo.getMsgGenTime(), qrCodeVo.getTerminalNo(),
                qrCodeVo.getSamLogicalId(), qrCodeVo.getTerminaSeq(), qrCodeVo.getAccSeq(),
                qrCodeVo.getTkStatus(), qrCodeVo.getOrderNo(), qrCodeVo.getReturnCode(), qrCodeVo.getErrCode()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrcode_cancel"
                + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, acc_seq, tk_status, order_no, return_code, err_code, insert_date)"
                + "values (" + FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrcode_cancel.nextval,"
                + "?,?,?,?,?,?,?,?,?,?,sysdate)";
        long endTime = System.currentTimeMillis();
//        logger.info("取消响应-48-入库耗时:" + (endTime - startTime));
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("48入库耗时:" + (endTime - startTime));
        return olDbHelper.executeUpdate(sql, values);
    }

    /*
    更新订单为取消状态
    */
    public int updateQrOrder(QrCodeVo qrCodeVo, DbHelper dbHelper)
            throws SQLException {
//        logger.debug("二维码订单取消更新订单表开始");
        //增加耗时统计 20190821 zhongzq
        long startTime = System.currentTimeMillis();
        int result = 0;
        //增加缓存表机制  20190821
        Object[] values = {"02", qrCodeVo.getDealTime(), qrCodeVo.getOrderNo()};
//        String sql = "update " + FrameDBConstant.COM_OL_P + "ol_qrcode_order set update_date = sysdate, ticket_status = ? ,deal_time = ? "
//                + " where order_no = ?";

        String sql = "update " + FrameDBConstant.COM_OL_P + "ol_buf_qrcode_order set update_date = sysdate, ticket_status = ? ,deal_time = ? "
                + " where order_no = ?";
        result = dbHelper.executeUpdate(sql, values);
        if (result == 0) {
            sql = "update " + FrameDBConstant.COM_OL_P + "ol_qrcode_order set update_date = sysdate, ticket_status = ? ,deal_time = ? "
                    + " where order_no = ?";
            result = dbHelper.executeUpdate(sql, values);
        }
        long endTime = System.currentTimeMillis();
//        logger.info("取消更新耗时:" + (endTime - startTime));
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("取消订单更新耗时:" + (endTime - startTime));
        return result;
    }

    public Long getQrCodeyAccSeq(QrCodeVo qrCodeVo, DbHelper olDbHelper) throws SQLException {
        //增加耗时统计 20190821 zhongzq
        long startTime = System.currentTimeMillis();
        boolean result = false;
        long accSeq = 0;
        String sql = "select acc_seq from "
                + FrameDBConstant.COM_OL_P + "ol_qrcode_tkcode where order_no=?";

        Object[] values = {qrCodeVo.getOrderNo()};
        result = olDbHelper.getFirstDocument(sql, values);
        if ("00000000000000".equals(qrCodeVo.getOrderNo())) {
            sql = "select a.acc_seq from "
                    + FrameDBConstant.COM_OL_P + "ol_qrcode_tkcode a where a.order_no = (select t.order_no from "
                    + FrameDBConstant.COM_OL_P + "ol_buf_qrcode_order t where t.tkcode = ?)";
            values = new Object[]{qrCodeVo.getQrcode()};
//        }
            result = olDbHelper.getFirstDocument(sql, values);
            if (!result) {
                sql = "select a.acc_seq from "
                        + FrameDBConstant.COM_OL_P + "ol_qrcode_tkcode a where a.order_no = (select t.order_no from "
                        + FrameDBConstant.COM_OL_P + "ol_qrcode_order t where t.tkcode = ?)";
                values = new Object[]{qrCodeVo.getQrcode()};
            }
        }
        if (result) {
            accSeq = olDbHelper.getItemLongValue("acc_seq");
        }
//        logger.debug("查询订单中心流水号为" + accSeq);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("订单中心流水-" + accSeq + "-耗时:" + (endTime - startTime));
        return accSeq;
    }
}
