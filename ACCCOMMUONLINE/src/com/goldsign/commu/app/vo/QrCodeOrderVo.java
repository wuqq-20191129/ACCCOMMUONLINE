/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.vo;

import java.util.Date;

/**
 * @author lind
 * 二维码订单实体类
 * @datetime 2017-9-4 18:17:42
 */
public class QrCodeOrderVo {

    private Long waterNo;
    private String orderNo;
    private String phoneNo;
    private Long saleFee;
    private Long saleTimes;
    private Long dealFee;
    private String status;
    private String updateDate;
    private String insertDate;
    private String startStation;
    private String endStation;
    private Long saleFeeTotal;
    private Long saleTimesTotal;
    private Long dealFeeTotal;
    private String ticketStatus;
    private String qrcode;
    private String tkcode;
    private Date validTime;
    private String dealTime;


    @Override
    public String toString() {
        return "QrCodeOrderVo{" +
                ", orderNo='" + orderNo + '\'' +
                ", saleFee=" + saleFee +
                ", saleTimes=" + saleTimes +
                ", dealFee=" + dealFee +
                ", startStation='" + startStation + '\'' +
                ", endStation='" + endStation + '\'' +
                ", saleFeeTotal=" + saleFeeTotal +
                ", saleTimesTotal=" + saleTimesTotal +
                ", dealFeeTotal=" + dealFeeTotal +
                ", ticketStatus='" + ticketStatus + '\'' +
                ", qrcode='" + qrcode + '\'' +
                ", tkcode='" + tkcode + '\'' +
                ", validTime=" + validTime +
                ", dealTime='" + dealTime + '\'' +
                '}';
    }

    public String getDealTime() {
        return dealTime;
    }

    public void setDealTime(String dealTime) {
        this.dealTime = dealTime;
    }

    public String getTkcode() {
        return tkcode;
    }

    public void setTkcode(String tkcode) {
        this.tkcode = tkcode;
    }


    public Date getValidTime() {
        return validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public Long getWaterNo() {
        return waterNo;
    }

    public void setWaterNo(Long waterNo) {
        this.waterNo = waterNo;
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
        return saleFee == null ? 0 : saleFee;
    }

    public void setSaleFee(Long saleFee) {
        this.saleFee = saleFee;
    }

    public Long getSaleTimes() {
        return saleTimes == null ? 0 : saleTimes;
    }

    public void setSaleTimes(Long saleTimes) {
        this.saleTimes = saleTimes;
    }

    public Long getDealFee() {
        return dealFee == null ? 0 : dealFee;
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

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public String getStartStation() {
        return startStation;
    }

    public void setStartStation(String startStation) {
        this.startStation = startStation;
    }

    public String getEndStation() {
        return endStation;
    }

    public void setEndStation(String endStation) {
        this.endStation = endStation;
    }

    public Long getSaleFeeTotal() {
        return saleFeeTotal == null ? 0 : saleFeeTotal;
    }

    public void setSaleFeeTotal(Long saleFeeTotal) {
        this.saleFeeTotal = saleFeeTotal;
    }

    public Long getSaleTimesTotal() {
        return saleTimesTotal == null ? 0 : saleTimesTotal;
    }

    public void setSaleTimesTotal(Long saleTimesTotal) {
        this.saleTimesTotal = saleTimesTotal;
    }

    public Long getDealFeeTotal() {
        return dealFeeTotal == null ? 0 : dealFeeTotal;
    }

    public void setDealFeeTotal(Long dealFeeTotal) {
        this.dealFeeTotal = dealFeeTotal;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }


}
