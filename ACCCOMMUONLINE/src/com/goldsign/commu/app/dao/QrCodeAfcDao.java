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

import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * @author lind
 * 二维码取票AFC
 * @datetime 2017-9-4 10:04:33
 */
public class QrCodeAfcDao {

    private final static Logger logger = Logger.getLogger(QrCodeAfcDao.class);

    /*
    二维码认证请求入库
    */
    public void insertQrCode(QrCodeVo qrCodeVo, DbHelper olDbHelper) throws SQLException {
        //增加耗时统计 20190821 zhongzq
//        logger.debug("二维码取票入库开始");
        long startTime = System.currentTimeMillis();
        //modify by zhongzq 20181106 增加remark字段信息
        Object[] values = {qrCodeVo.getWaterNo(), qrCodeVo.getMessageId(),
                qrCodeVo.getMsgGenTime(), qrCodeVo.getTerminalNo(),
                qrCodeVo.getSamLogicalId(), qrCodeVo.getTerminaSeq(),
                qrCodeVo.getAccSeq(), qrCodeVo.getQrcode(), qrCodeVo.getRemark()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrcode_afc"
                + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, acc_seq, qrcode, insert_date,remark)"
                + "values (?,?,?,?,?,?,?,?,sysdate,?)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
//        logger.info("二维码取票-56-入库业务表耗时:" + (endTime - startTime));
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("56入库耗时:" + (endTime - startTime));
    }

    /*
    二维码认证请求响应入库
    */
    public void insertQrCodeRsp(QrCodeVo qrCodeVo, DbHelper olDbHelper) throws SQLException {
        //增加耗时统计 20190821 zhongzq
        long startTime = System.currentTimeMillis();
//        logger.debug("二维码认证请求响应开始");

        Object[] values = {"66",
                qrCodeVo.getMsgGenTime(), qrCodeVo.getTerminalNo(),
                qrCodeVo.getSamLogicalId(), qrCodeVo.getTerminaSeq(),
                qrCodeVo.getAccSeq(), qrCodeVo.getReturnCode(), qrCodeVo.getOrderNo(),
                qrCodeVo.getPhoneNo(), qrCodeVo.getSaleFee(),
                qrCodeVo.getSaleTimes(), qrCodeVo.getDealFee()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrcode_afc"
                + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, acc_seq, return_code, order_no, phone_no, sale_fee, sale_times, deal_fee, insert_date)"
                + "values (" + FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrcode_afc.nextval,"
                + "?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
//        logger.info("二维码认证请求响应-66-入库业务表耗时:" + (endTime - startTime));
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("66入库耗时:" + (endTime - startTime));
    }

    /*
    二维码支付执行结果请求入库
    */
    public void insertQrCodeResult(QrCodeVo qrCodeVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("二维码支付执行结果请求开始");
        //增加耗时统计 20190821 zhongzq
        long startTime = System.currentTimeMillis();
        Object[] values = {qrCodeVo.getWaterNo(), qrCodeVo.getMessageId(),
                qrCodeVo.getMsgGenTime(), qrCodeVo.getTerminalNo(),
                qrCodeVo.getSamLogicalId(), qrCodeVo.getTerminaSeq(),
                qrCodeVo.getAccSeq(), qrCodeVo.getOrderNo(),
                qrCodeVo.getPhoneNo(), qrCodeVo.getSaleFee(),
                qrCodeVo.getSaleTimes(), qrCodeVo.getDealFee(),
                qrCodeVo.getResultCode(), qrCodeVo.getDealTime()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrcode_afc"
                + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, acc_seq, order_no, phone_no,"
                + " sale_fee, sale_times, deal_fee, result_code, deal_time, insert_date)"
                + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
        olDbHelper.executeUpdate(sql, values);
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("57入库耗时:" + (endTime - startTime));
    }

    /*
    二维码支付执行结果响应入库
    */
    public int insertQrCodeResultRsp(QrCodeVo qrCodeVo, DbHelper olDbHelper) throws SQLException {
//        logger.debug("二维码支付执行结果响应开始");
        //增加耗时统计 20190821 zhongzq
        long startTime = System.currentTimeMillis();
        Object[] values = {"67",
                qrCodeVo.getMsgGenTime(), qrCodeVo.getTerminalNo(),
                qrCodeVo.getSamLogicalId(), qrCodeVo.getTerminaSeq(),
                qrCodeVo.getAccSeq(), qrCodeVo.getReturnCode()
        };
        String sql = "insert into " + FrameDBConstant.COM_OL_P + "ol_qrcode_afc"
                + "(water_no, message_id, msg_gen_time, termina_no, sam_logical_id, termina_seq, acc_seq, return_code, insert_date)"
                + "values (" + FrameDBConstant.COM_OL_P + "seq_" + FrameDBConstant.TABLE_PREFIX + "ol_qrcode_afc.nextval,"
                + "?,?,?,?,?,?,?,sysdate)";
        long endTime = System.currentTimeMillis();
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("67入库耗时:" + (endTime - startTime));
        return olDbHelper.executeUpdate(sql, values);

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
        //modify by zhongzq 20190321
//        boolean result = true;
        boolean result = false;
        if (qrCodeVo.getQrcode() != null) {
            //56消息  增加缓存表 先查缓存表 再查总表 modify by zhongzq 20190820
            Object[] values = {qrCodeVo.getQrcode()};
//            String sql = "select order_no, phone_no, nvl(sale_fee,0) sale_fee, nvl(sale_times,0) sale_times, nvl(deal_fee,0) deal_fee, "
//                    + "status, update_date, insert_date, start_station, end_station, "
//                    + "nvl(sale_fee_total,0) sale_fee_total, nvl(sale_times_total,0) sale_times_total, nvl(deal_fee_total,0) deal_fee_total, ticket_status, qrcode, tkcode, valid_time from "
//                    + FrameDBConstant.COM_OL_P + "OL_QRCODE_ORDER where tkcode=?";
//            result = olDbHelper.getFirstDocument(sql, values);
            String sql = "select order_no, phone_no, nvl(sale_fee,0) sale_fee, nvl(sale_times,0) sale_times, nvl(deal_fee,0) deal_fee, "
                    + "status, update_date, insert_date, start_station, end_station, "
                    + "nvl(sale_fee_total,0) sale_fee_total, nvl(sale_times_total,0) sale_times_total, nvl(deal_fee_total,0) deal_fee_total, ticket_status, qrcode, tkcode, valid_time from "
                    + FrameDBConstant.COM_OL_P + "OL_BUF_QRCODE_ORDER where tkcode=?";
            result = olDbHelper.getFirstDocument(sql, values);
            if (!result) {
                sql = "select order_no, phone_no, nvl(sale_fee,0) sale_fee, nvl(sale_times,0) sale_times, nvl(deal_fee,0) deal_fee, "
                        + "status, update_date, insert_date, start_station, end_station, "
                        + "nvl(sale_fee_total,0) sale_fee_total, nvl(sale_times_total,0) sale_times_total, nvl(deal_fee_total,0) deal_fee_total, ticket_status, qrcode, tkcode, valid_time from "
                        + FrameDBConstant.COM_OL_P + "OL_QRCODE_ORDER where tkcode=?";
                result = olDbHelper.getFirstDocument(sql, values);
            }
        } else if (!"00000000000000".equals(qrCodeVo.getOrderNo()) && qrCodeVo.getOrderNo() != null) {
            //57消息 modify by zhongzq 增加缓存表
            Object[] values = {qrCodeVo.getOrderNo()};
//            String sql = "select order_no, phone_no, nvl(sale_fee,0) sale_fee, nvl(sale_times,0) sale_times, nvl(deal_fee,0) deal_fee, "
//                    + "status, update_date, insert_date, start_station, end_station, "
//                    + "nvl(sale_fee_total,0) sale_fee_total, nvl(sale_times_total,0) sale_times_total, nvl(deal_fee_total,0) deal_fee_total, ticket_status, qrcode, tkcode, valid_time from "
//                    + FrameDBConstant.COM_OL_P + "OL_QRCODE_ORDER where order_no=?";
//            result = olDbHelper.getFirstDocument(sql, values);
            String sql = "select order_no, phone_no, nvl(sale_fee,0) sale_fee, nvl(sale_times,0) sale_times, nvl(deal_fee,0) deal_fee, "
                    + "status, update_date, insert_date, start_station, end_station, "
                    + "nvl(sale_fee_total,0) sale_fee_total, nvl(sale_times_total,0) sale_times_total, nvl(deal_fee_total,0) deal_fee_total, ticket_status, qrcode, tkcode, valid_time from "
                    + FrameDBConstant.COM_OL_P + "OL_BUF_QRCODE_ORDER where order_no=?";
            result = olDbHelper.getFirstDocument(sql, values);
            if (!result) {
                sql = "select order_no, phone_no, nvl(sale_fee,0) sale_fee, nvl(sale_times,0) sale_times, nvl(deal_fee,0) deal_fee, "
                        + "status, update_date, insert_date, start_station, end_station, "
                        + "nvl(sale_fee_total,0) sale_fee_total, nvl(sale_times_total,0) sale_times_total, nvl(deal_fee_total,0) deal_fee_total, ticket_status, qrcode, tkcode, valid_time from "
                        + FrameDBConstant.COM_OL_P + "OL_QRCODE_ORDER where order_no=?";
                result = olDbHelper.getFirstDocument(sql, values);
            }
        }
        if (result) {
            order = new QrCodeOrderVo();
            order.setStatus(olDbHelper.getItemValue("status"));
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
            order.setTkcode(olDbHelper.getItemValue("tkcode"));
        }
        long endTime = System.currentTimeMillis();
//        logger.info("二维码订单状态查询耗时:" + (endTime - startTime));
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("ITM订单查询耗时:" + (endTime - startTime));
        return order;
    }

    /*
    更新订单
    */
    public int updateMsg(QrCodeVo qrCodeVo, DbHelper dbHelper)
            throws SQLException {
//        logger.debug("更新订单开始");
        //增加耗时统计 20190821 zhongzq
        long startTime = System.currentTimeMillis();
        int result = 0;
        String tkStatus = "00";
        if (qrCodeVo.getResultCode().equals("00")) {
            tkStatus = "81";
        }
        if (qrCodeVo.getResultCode().equals("01")) {
            tkStatus = "01";
        }
        if (!"00".equals(tkStatus)) {
            Object[] values = {qrCodeVo.getSaleFee(), qrCodeVo.getSaleTimes(),
                    qrCodeVo.getDealFee(), tkStatus, qrCodeVo.getDealTime(), qrCodeVo.getOrderNo()};
            String sql = "update " + FrameDBConstant.COM_OL_P + "OL_BUF_QRCODE_ORDER set sale_fee = ?, sale_times = nvl(sale_times,0)+?, deal_fee = nvl(deal_fee,0)+?, "
                    + " update_date = sysdate, ticket_status = ? , deal_time=? "
                    + " where order_no = ?";
            result = dbHelper.executeUpdate(sql, values);
            if (result == 0) {
                sql = "update " + FrameDBConstant.COM_OL_P + "OL_QRCODE_ORDER set sale_fee = ?, sale_times = nvl(sale_times,0)+?, deal_fee = nvl(deal_fee,0)+?, "
                        + " update_date = sysdate, ticket_status = ? , deal_time=? "
                        + " where order_no = ?";
                result = dbHelper.executeUpdate(sql, values);
            }
        }
        long endTime = System.currentTimeMillis();
//        logger.info("二维码取票订单更新耗时:" + (endTime - startTime));
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("订单更新耗时:" + (endTime - startTime));
        return result;
    }

    /*
    锁定/解锁订单
    */
    public int lockOrder(QrCodeVo qrCodeVo, DbHelper dbHelper)
            throws SQLException {
        //增加耗时统计 20190821 zhongzq
        long startTime = System.currentTimeMillis();
        int result = 0;
//        logger.debug("锁定/解锁订单开始");
        Object[] values = {qrCodeVo.getTerminalNo(), qrCodeVo.getTkStatus(), qrCodeVo.getOrderNo()};
        String sql = "update " + FrameDBConstant.COM_OL_P + "OL_BUF_QRCODE_ORDER set lock_dev = ?, update_date = sysdate, ticket_status = ? "
                + " where order_no = ?";
        result = dbHelper.executeUpdate(sql, values);
        if (result == 0) {
            sql = "update " + FrameDBConstant.COM_OL_P + "OL_QRCODE_ORDER set lock_dev = ?, update_date = sysdate, ticket_status = ? "
                    + " where order_no = ?";
            result = dbHelper.executeUpdate(sql, values);
        }
        long endTime = System.currentTimeMillis();
//        logger.info("二维码取票订单锁定/解锁订单耗时:" + (endTime - startTime));
        ThreadLocalUtil.LOG_THREAD_LOCAL.get().add("取票锁定/解锁耗时:" + (endTime - startTime));
        return result;

    }

}
