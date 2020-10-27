/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.vo;

import com.goldsign.commu.app.dao.QrPayDao;
import com.goldsign.commu.frame.constant.FrameDBConstant;
import com.goldsign.lib.db.util.DbHelper;

import java.sql.SQLException;

/**
 * @datetime 2018-5-8
 * @author lind
 * 支付二维码订单实体类
 */
public class QrPayOrderVo extends BaseRspVo {
    
    private String orderNo="00000000000000";
    private String phoneNo;
    private Long saleFee = 0L;
    private Long saleTimes = 0L;
    private Long dealFee = 0L;
    private String status = "0";
    private String orderDate = "00000000000000";
    private String insertDate;
    private String cardType = "0000";
    private String payStatus;
    private String payDate;
    private Long saleFeeTotal = 0L;
    private Long saleTimesTotal = 0L;
    private Long dealFeeTotal = 0L;
    private String cardTypeTotal = "0000";
    private String qrpayId = "0000000000";
    private String qrpayData = "00000000000000-0000000000-00000000";
    private String payChannelType ="00";
    private String payChannelCode ="0000";
    private Long accSeq;
    private Long terminaSeq;
    private String dealTime;
    private String orderIp;
    
    private String imsi;
    private String imei;
    private String appCode;

    //add by zhongzq 20181228 有效时间
    private String validTime ;
    //add by zhongziqi 20190102
    // 下发73小消息标志
    private boolean construct73Flag = false;
    //退款通知作用于订单表
    private String updateFlag = "0";
    //退款时间
    private String refundDate;
    //退款金额
    private Long refundFee;
    //退款原因
    private String refundReason;
    //中心处理时间
    private  String accHandleTime;
    //订单取消时间
    private String orderCancelTime;
    //订单取消原因
    private String getOrderCancelReason;
    /**
     * 上次订单状态
     */
    private String lastStatus;

    @Override
    public String toString() {
        return "QrPayOrderVo{" +
                "orderNo='" + orderNo + '\'' +
                ", saleFee=" + saleFee +
                ", saleTimes=" + saleTimes +
                ", dealFee=" + dealFee +
                ", lastStatus='" + lastStatus + '\'' +
                ", status='" + status + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", cardType='" + cardType + '\'' +
                ", payStatus='" + payStatus + '\'' +
                ", payDate='" + payDate + '\'' +
                ", saleFeeTotal=" + saleFeeTotal +
                ", saleTimesTotal=" + saleTimesTotal +
                ", dealFeeTotal=" + dealFeeTotal +
                ", cardTypeTotal='" + cardTypeTotal + '\'' +
                ", qrpayId='" + qrpayId + '\'' +
                ", qrpayData='" + qrpayData + '\'' +
                ", payChannelType='" + payChannelType + '\'' +
                ", payChannelCode='" + payChannelCode + '\'' +
                ", accSeq=" + accSeq +
                ", terminaSeq=" + terminaSeq +
                ", dealTime='" + dealTime + '\'' +
                ", appCode='" + appCode + '\'' +
                ", validTime='" + validTime + '\'' +
                ", refundDate='" + refundDate + '\'' +
                ", refundFee=" + refundFee +
                ", refundReason='" + refundReason + '\'' +
                ", orderCancelTime='" + orderCancelTime + '\'' +
                ", getOrderCancelReason='" + getOrderCancelReason + '\'' +
                '}';
    }

    public String getCardTypeTotal() {
        return cardTypeTotal;
    }

    public void setCardTypeTotal(String cardTypeTotal) {
        this.cardTypeTotal = cardTypeTotal;
    }

    public String getOrderIp() {
        return orderIp;
    }

    public void setOrderIp(String orderIp) {
        this.orderIp = orderIp;
    }

    public String getDealTime() {
        return dealTime;
    }

    public void setDealTime(String dealTime) {
        this.dealTime = dealTime;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public Long getTerminaSeq() {
        return terminaSeq;
    }

    public void setTerminaSeq(Long terminaSeq) {
        this.terminaSeq = terminaSeq;
    }

    public Long getAccSeq() {
        return accSeq;
    }

    public void setAccSeq(Long accSeq) {
        this.accSeq = accSeq;
    }

    public String getPayChannelType() {
        return payChannelType;
    }

    public void setPayChannelType(String payChannelType) {
        this.payChannelType = payChannelType;
    }

    public String getPayChannelCode() {
        return payChannelCode;
    }

    public void setPayChannelCode(String payChannelCode) {
        this.payChannelCode = payChannelCode;
    }
    
    public String getQrpayData() {
        return qrpayData;
    }

    public void setQrpayData(String qrpayData) {
        this.qrpayData = qrpayData;
    }


    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }
    
    public String getQrpayId() {
        return qrpayId;
    }

    public void setQrpayId(String qrpayId) {
        this.qrpayId = qrpayId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public Long getSaleFee() {
        return saleFee==null?0:saleFee;
    }

    public void setSaleFee(Long saleFee) {
        this.saleFee = saleFee;
    }

    public Long getSaleTimes() {
        return saleTimes==null?0:saleTimes;
    }

    public void setSaleTimes(Long saleTimes) {
        this.saleTimes = saleTimes;
    }

    public Long getDealFee() {
        return dealFee==null?0:dealFee;
    }

    public void setDealFee(Long dealFee) {
        this.dealFee = dealFee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public Long getSaleFeeTotal() {
        return saleFeeTotal==null?0:saleFeeTotal;
    }

    public void setSaleFeeTotal(Long saleFeeTotal) {
        this.saleFeeTotal = saleFeeTotal;
    }

    public Long getSaleTimesTotal() {
        return saleTimesTotal==null?0:saleTimesTotal;
    }

    public void setSaleTimesTotal(Long saleTimesTotal) {
        this.saleTimesTotal = saleTimesTotal;
    }

    public Long getDealFeeTotal() {
        return dealFeeTotal==null?0:dealFeeTotal;
    }

    public void setDealFeeTotal(Long dealFeeTotal) {
        this.dealFeeTotal = dealFeeTotal;
    }

    public String getValidTime() {
        return validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    public boolean isConstruct73Flag() {
        return construct73Flag;
    }

    public void setConstruct73Flag(boolean construct73Flag) {
        this.construct73Flag = construct73Flag;
    }

    public String getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(String refundDate) {
        this.refundDate = refundDate;
    }

    public Long getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(Long refundFee) {
        this.refundFee = refundFee;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public String getAccHandleTime() {
        return accHandleTime;
    }

    public void setAccHandleTime(String accHandleTime) {
        this.accHandleTime = accHandleTime;
    }

    public String getOrderCancelTime() {
        return orderCancelTime;
    }

    public void setOrderCancelTime(String orderCancelTime) {
        this.orderCancelTime = orderCancelTime;
    }

    public String getGetOrderCancelReason() {
        return getOrderCancelReason;
    }

    public void setGetOrderCancelReason(String getOrderCancelReason) {
        this.getOrderCancelReason = getOrderCancelReason;
    }

    public String getUpdateFlag() {
        return updateFlag;
    }

    public void setUpdateFlag(String updateFlag) {
        this.updateFlag = updateFlag;
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(String lastStatus) {
        this.lastStatus = lastStatus;
    }
}
