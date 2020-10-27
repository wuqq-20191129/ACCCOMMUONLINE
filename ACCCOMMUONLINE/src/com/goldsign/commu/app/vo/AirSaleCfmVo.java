package com.goldsign.commu.app.vo;

import java.util.Date;

/**
 * @datetime 2017-12-6
 * @author lind
 * 空中发售确认实体类
 */

public class AirSaleCfmVo {
    private Long waterNo;

    private String messageId;

    private String msgGenTime;

    private String terminaNo;

    private String samLogicalId;

    private Long terminaSeq;

    private String branchesCode;

    private String issMainCode;

    private String issSubCode;

    private String phoneNo;

    private String imsi;

    private String imei;

    private String cardType;

    private String cardLogicalId;

    private String cardPhyId;

    private String isTestFlag;

    private String resultCode;

    private String dealTime;

    private Long sysRefNo;

    private String returnCode;

    private String errCode;

    private Date insertDate;
    
    private String appCode;
    
    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public AirSaleCfmVo(Long waterNo, String messageId, String msgGenTime, String terminaNo, String samLogicalId, Long terminaSeq, String branchesCode, String issMainCode, String issSubCode, String phoneNo, String imsi, String imei, String cardType, String cardLogicalId, String cardPhyId, String isTestFlag, String resultCode, String dealTime, Long sysRefNo, String returnCode, String errCode, Date insertDate, String appCode) {
        this.waterNo = waterNo;
        this.messageId = messageId;
        this.msgGenTime = msgGenTime;
        this.terminaNo = terminaNo;
        this.samLogicalId = samLogicalId;
        this.terminaSeq = terminaSeq;
        this.branchesCode = branchesCode;
        this.issMainCode = issMainCode;
        this.issSubCode = issSubCode;
        this.phoneNo = phoneNo;
        this.imsi = imsi;
        this.imei = imei;
        this.cardType = cardType;
        this.cardLogicalId = cardLogicalId;
        this.cardPhyId = cardPhyId;
        this.isTestFlag = isTestFlag;
        this.resultCode = resultCode;
        this.dealTime = dealTime;
        this.sysRefNo = sysRefNo;
        this.returnCode = returnCode;
        this.errCode = errCode;
        this.insertDate = insertDate;
        this.appCode = appCode;
    }

    public AirSaleCfmVo() {
        super();
    }

    public Long getWaterNo() {
        return waterNo;
    }

    public void setWaterNo(Long waterNo) {
        this.waterNo = waterNo;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId == null ? null : messageId.trim();
    }

    public String getMsgGenTime() {
        return msgGenTime;
    }

    public void setMsgGenTime(String msgGenTime) {
        this.msgGenTime = msgGenTime == null ? null : msgGenTime.trim();
    }

    public String getTerminaNo() {
        return terminaNo;
    }

    public void setTerminaNo(String terminaNo) {
        this.terminaNo = terminaNo == null ? null : terminaNo.trim();
    }

    public String getSamLogicalId() {
        return samLogicalId;
    }

    public void setSamLogicalId(String samLogicalId) {
        this.samLogicalId = samLogicalId == null ? null : samLogicalId.trim();
    }

    public Long getTerminaSeq() {
        return terminaSeq;
    }

    public void setTerminaSeq(Long terminaSeq) {
        this.terminaSeq = terminaSeq;
    }

    public String getBranchesCode() {
        return branchesCode;
    }

    public void setBranchesCode(String branchesCode) {
        this.branchesCode = branchesCode == null ? null : branchesCode.trim();
    }

    public String getIssMainCode() {
        return issMainCode;
    }

    public void setIssMainCode(String issMainCode) {
        this.issMainCode = issMainCode == null ? null : issMainCode.trim();
    }

    public String getIssSubCode() {
        return issSubCode;
    }

    public void setIssSubCode(String issSubCode) {
        this.issSubCode = issSubCode == null ? null : issSubCode.trim();
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo == null ? null : phoneNo.trim();
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi == null ? null : imsi.trim();
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei == null ? null : imei.trim();
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType == null ? null : cardType.trim();
    }

    public String getCardLogicalId() {
        return cardLogicalId;
    }

    public void setCardLogicalId(String cardLogicalId) {
        this.cardLogicalId = cardLogicalId == null ? null : cardLogicalId.trim();
    }

    public String getCardPhyId() {
        return cardPhyId;
    }

    public void setCardPhyId(String cardPhyId) {
        this.cardPhyId = cardPhyId == null ? null : cardPhyId.trim();
    }

    public String getIsTestFlag() {
        return isTestFlag;
    }

    public void setIsTestFlag(String isTestFlag) {
        this.isTestFlag = isTestFlag == null ? null : isTestFlag.trim();
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode == null ? null : resultCode.trim();
    }

    public String getDealTime() {
        return dealTime;
    }

    public void setDealTime(String dealTime) {
        this.dealTime = dealTime == null ? null : dealTime.trim();
    }

    public Long getSysRefNo() {
        return sysRefNo;
    }

    public void setSysRefNo(Long sysRefNo) {
        this.sysRefNo = sysRefNo;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode == null ? null : returnCode.trim();
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode == null ? null : errCode.trim();
    }

    public Date getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Date insertDate) {
        this.insertDate = insertDate;
    }

    @Override
    public String toString() {
        return "AirSaleCfmVo{" +
                "waterNo=" + waterNo +
                ", messageId='" + messageId + '\'' +
                ", msgGenTime='" + msgGenTime + '\'' +
                ", terminaNo='" + terminaNo + '\'' +
                ", samLogicalId='" + samLogicalId + '\'' +
                ", terminaSeq=" + terminaSeq +
                ", branchesCode='" + branchesCode + '\'' +
                ", issMainCode='" + issMainCode + '\'' +
                ", issSubCode='" + issSubCode + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", imsi='" + imsi + '\'' +
                ", imei='" + imei + '\'' +
                ", cardType='" + cardType + '\'' +
                ", cardLogicalId='" + cardLogicalId + '\'' +
                ", cardPhyId='" + cardPhyId + '\'' +
                ", isTestFlag='" + isTestFlag + '\'' +
                ", resultCode='" + resultCode + '\'' +
                ", dealTime='" + dealTime + '\'' +
                ", sysRefNo=" + sysRefNo +
                ", returnCode='" + returnCode + '\'' +
                ", errCode='" + errCode + '\'' +
                ", insertDate=" + insertDate +
                ", appCode='" + appCode + '\'' +
                '}';
    }
}