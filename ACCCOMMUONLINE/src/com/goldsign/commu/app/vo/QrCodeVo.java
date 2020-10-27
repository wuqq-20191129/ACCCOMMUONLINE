/*
 * To change this license header; choose License Headers in Project Properties.
 * To change this template file; choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.vo;

/**
 * @datetime 2017-9-2 14:28:20
 * @author lind
 * 二维码实体类
 */
public class QrCodeVo extends BaseRspVo {
    private Long terminaSeq;
    private Long accSeq;
    private String qrcode;
    private String resultCode = "00";
    private String orderNo = "00000000000000";
    private String phoneNo = "00000000000";
    private Long saleFee = 0L;
    private Long saleTimes = 0L;
    private Long dealFee = 0L;
    private Long takeTimes = 0L;
    private String tkStatus = "00";
    private String validTime = "00000000000000";
    private String insertDate;
    private String dealTime = "00000000000000";
    //add by zhongzq 20181106 增加remark字段
    private String remark ="";
    //add by zhongzq 20190315
    private String startStation ="";
    private String endStation ="";
    private Long hasTakeNum=0L;//已取数量

//    @Override
//    public String toString() {
//        return "QrCodeVo{" + "terminaSeq=" + terminaSeq + ", accSeq=" + accSeq + ", qrcode=" + qrcode + ", resultCode="
//                + resultCode + ", orderNo=" + orderNo + ", phoneNo=" + phoneNo + ", saleFee=" + saleFee + ", saleTimes="
//                + saleTimes + ", dealFee=" + dealFee + ", takeTimes=" + takeTimes+ ", validTime=" + validTime + ", tkStatus="
//               + tkStatus + ", insertDate=" + insertDate + ", dealTime=" + dealTime + '}';
//    }

    @Override
    public String toString() {
        return "QrCodeVo{" +
                "terminaSeq=" + terminaSeq +
                ", accSeq=" + accSeq +
                ", resultCode='" + resultCode + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", saleFee=" + saleFee +
                ", saleTimes=" + saleTimes +
                ", dealFee=" + dealFee +
                ", takeTimes=" + takeTimes +
                ", tkStatus='" + tkStatus + '\'' +
                ", validTime='" + validTime + '\'' +
                ", dealTime='" + dealTime + '\'' +
                ", remark='" + remark + '\'' +
                ", startStation='" + startStation + '\'' +
                ", endStation='" + endStation + '\'' +
                ", hasTakeNum='" + hasTakeNum + '\'' +
                '}';
    }

    public String getDealTime() {
        return dealTime;
    }

    public void setDealTime(String dealTime) {
        this.dealTime = dealTime;
    }

    public String getValidTime() {
        return validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }

    public Long getTakeTimes() {
        return takeTimes;
    }

    public void setTakeTimes(Long takeTimes) {
        this.takeTimes = takeTimes;
    }

    public String getTkStatus() {
        return tkStatus;
    }

    public void setTkStatus(String tkStatus) {
        this.tkStatus = tkStatus;
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

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
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
        return saleFee;
    }

    public void setSaleFee(Long saleFee) {
        this.saleFee = saleFee;
    }

    public Long getSaleTimes() {
        return saleTimes;
    }

    public void setSaleTimes(Long saleTimes) {
        this.saleTimes = saleTimes;
    }

    public Long getDealFee() {
        return dealFee;
    }

    public void setDealFee(Long dealFee) {
        this.dealFee = dealFee;
    }

    public String getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }
    //add by zhongzq 20181106 增加remark字段
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public Long getHasTakeNum() {
        return hasTakeNum;
    }

    public void setHasTakeNum(Long hasTakeNum) {
        this.hasTakeNum = hasTakeNum;
    }
}
