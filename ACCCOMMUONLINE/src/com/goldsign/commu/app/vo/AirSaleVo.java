package com.goldsign.commu.app.vo;

import java.util.Date;

/**
 * @datetime 2017-12-6
 * @author lind
 * 空中发售申请实体类
 */

public class AirSaleVo {
    private Long waterNo;

    private String messageId;

    private String msgGenTime;

    private Long sysRefNo;

    private String terminaNo;

    private String samLogicalId;

    private Long terminaSeq;

    private String branchesCode;

    private String cardType;

    private String phoneNo;

    private String imsi;

    private String imei;

    private String productCode = "0991";

    private String cityCode = "8300";

    private String businessCode = "0003";

    private String dealDay = "00000000";

    private String dealDevCode = "15802";

    private String cardVer = "10";

    private String cardDay = "00000000";

    private String cardAppDay = "00000000";

    private String cardAppVer = "10";

    private String cardLogicalId = "0000000000000000";

    private String cardPhyId = "0000000000000000";

    private String expiryDate = "20991230";

    private Long faceValue = 0L;

    private Long depositFee = 0L;

    private String appExpiryStart = "00000000";

    private String appExpiryDay = "99999";

    private String saleActFlag = "1";

    private String isTestFlag = "0";

    private Long chargeLimit = 10000L;

    private String limitMode = "000";

    private String limitEntryStation = "0000";

    private String limitExitStation = "0000";

    private String cardMac = "00000000";

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

    public AirSaleVo(Long waterNo, String messageId, String msgGenTime, Long sysRefNo, String terminaNo, String samLogicalId, Long terminaSeq, String branchesCode, String cardType, String phoneNo, String imsi, String imei, String productCode, String cityCode, String businessCode, String dealDay, String dealDevCode, String cardVer, String cardDay, String cardAppDay, String cardAppVer, String cardLogicalId, String cardPhyId, String expiryDate, Long faceValue, Long depositFee, String appExpiryStart, String appExpiryDay, String saleActFlag, String isTestFlag, Long chargeLimit, String limitMode, String limitEntryStation, String limitExitStation, String cardMac, String returnCode, String errCode, Date insertDate, String appCode) {
        this.waterNo = waterNo;
        this.messageId = messageId;
        this.msgGenTime = msgGenTime;
        this.sysRefNo = sysRefNo;
        this.terminaNo = terminaNo;
        this.samLogicalId = samLogicalId;
        this.terminaSeq = terminaSeq;
        this.branchesCode = branchesCode;
        this.cardType = cardType;
        this.phoneNo = phoneNo;
        this.imsi = imsi;
        this.imei = imei;
        this.productCode = productCode;
        this.cityCode = cityCode;
        this.businessCode = businessCode;
        this.dealDay = dealDay;
        this.dealDevCode = dealDevCode;
        this.cardVer = cardVer;
        this.cardDay = cardDay;
        this.cardAppDay = cardAppDay;
        this.cardAppVer = cardAppVer;
        this.cardLogicalId = cardLogicalId;
        this.cardPhyId = cardPhyId;
        this.expiryDate = expiryDate;
        this.faceValue = faceValue;
        this.depositFee = depositFee;
        this.appExpiryStart = appExpiryStart;
        this.appExpiryDay = appExpiryDay;
        this.saleActFlag = saleActFlag;
        this.isTestFlag = isTestFlag;
        this.chargeLimit = chargeLimit;
        this.limitMode = limitMode;
        this.limitEntryStation = limitEntryStation;
        this.limitExitStation = limitExitStation;
        this.cardMac = cardMac;
        this.returnCode = returnCode;
        this.errCode = errCode;
        this.insertDate = insertDate;
        this.appCode = appCode;
    }

    public AirSaleVo() {
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

    public Long getSysRefNo() {
        return sysRefNo;
    }

    public void setSysRefNo(Long sysRefNo) {
        this.sysRefNo = sysRefNo;
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

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType == null ? null : cardType.trim();
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

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode == null ? null : productCode.trim();
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode == null ? null : cityCode.trim();
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode == null ? null : businessCode.trim();
    }

    public String getDealDay() {
        return dealDay;
    }

    public void setDealDay(String dealDay) {
        this.dealDay = dealDay == null ? null : dealDay.trim();
    }

    public String getDealDevCode() {
        return dealDevCode;
    }

    public void setDealDevCode(String dealDevCode) {
        this.dealDevCode = dealDevCode == null ? null : dealDevCode.trim();
    }

    public String getCardVer() {
        return cardVer;
    }

    public void setCardVer(String cardVer) {
        this.cardVer = cardVer == null ? null : cardVer.trim();
    }

    public String getCardDay() {
        return cardDay;
    }

    public void setCardDay(String cardDay) {
        this.cardDay = cardDay == null ? null : cardDay.trim();
    }

    public String getCardAppDay() {
        return cardAppDay;
    }

    public void setCardAppDay(String cardAppDay) {
        this.cardAppDay = cardAppDay == null ? null : cardAppDay.trim();
    }

    public String getCardAppVer() {
        return cardAppVer;
    }

    public void setCardAppVer(String cardAppVer) {
        this.cardAppVer = cardAppVer == null ? null : cardAppVer.trim();
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

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate == null ? null : expiryDate.trim();
    }

    public Long getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(Long faceValue) {
        this.faceValue = faceValue;
    }

    public Long getDepositFee() {
        return depositFee;
    }

    public void setDepositFee(Long depositFee) {
        this.depositFee = depositFee;
    }

    public String getAppExpiryStart() {
        return appExpiryStart;
    }

    public void setAppExpiryStart(String appExpiryStart) {
        this.appExpiryStart = appExpiryStart == null ? null : appExpiryStart.trim();
    }

    public String getAppExpiryDay() {
        return appExpiryDay;
    }

    public void setAppExpiryDay(String appExpiryDay) {
        this.appExpiryDay = appExpiryDay == null ? null : appExpiryDay.trim();
    }

    public String getSaleActFlag() {
        return saleActFlag;
    }

    public void setSaleActFlag(String saleActFlag) {
        this.saleActFlag = saleActFlag == null ? null : saleActFlag.trim();
    }

    public String getIsTestFlag() {
        return isTestFlag;
    }

    public void setIsTestFlag(String isTestFlag) {
        this.isTestFlag = isTestFlag == null ? null : isTestFlag.trim();
    }

    public Long getChargeLimit() {
        return chargeLimit;
    }

    public void setChargeLimit(Long chargeLimit) {
        this.chargeLimit = chargeLimit;
    }

    public String getLimitMode() {
        return limitMode;
    }

    public void setLimitMode(String limitMode) {
        this.limitMode = limitMode == null ? null : limitMode.trim();
    }

    public String getLimitEntryStation() {
        return limitEntryStation;
    }

    public void setLimitEntryStation(String limitEntryStation) {
        this.limitEntryStation = limitEntryStation == null ? null : limitEntryStation.trim();
    }

    public String getLimitExitStation() {
        return limitExitStation;
    }

    public void setLimitExitStation(String limitExitStation) {
        this.limitExitStation = limitExitStation == null ? null : limitExitStation.trim();
    }

    public String getCardMac() {
        return cardMac;
    }

    public void setCardMac(String cardMac) {
        this.cardMac = cardMac == null ? null : cardMac.trim();
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
        return "AirSaleVo{" +
                "waterNo=" + waterNo +
                ", messageId='" + messageId + '\'' +
                ", msgGenTime='" + msgGenTime + '\'' +
                ", sysRefNo=" + sysRefNo +
                ", terminaNo='" + terminaNo + '\'' +
                ", samLogicalId='" + samLogicalId + '\'' +
                ", terminaSeq=" + terminaSeq +
                ", branchesCode='" + branchesCode + '\'' +
                ", cardType='" + cardType + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", imsi='" + imsi + '\'' +
                ", imei='" + imei + '\'' +
                ", productCode='" + productCode + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", businessCode='" + businessCode + '\'' +
                ", dealDay='" + dealDay + '\'' +
                ", dealDevCode='" + dealDevCode + '\'' +
                ", cardVer='" + cardVer + '\'' +
                ", cardDay='" + cardDay + '\'' +
                ", cardAppDay='" + cardAppDay + '\'' +
                ", cardAppVer='" + cardAppVer + '\'' +
                ", cardLogicalId='" + cardLogicalId + '\'' +
                ", cardPhyId='" + cardPhyId + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", faceValue=" + faceValue +
                ", depositFee=" + depositFee +
                ", appExpiryStart='" + appExpiryStart + '\'' +
                ", appExpiryDay='" + appExpiryDay + '\'' +
                ", saleActFlag='" + saleActFlag + '\'' +
                ", isTestFlag='" + isTestFlag + '\'' +
                ", chargeLimit=" + chargeLimit +
                ", limitMode='" + limitMode + '\'' +
                ", limitEntryStation='" + limitEntryStation + '\'' +
                ", limitExitStation='" + limitExitStation + '\'' +
                ", cardMac='" + cardMac + '\'' +
                ", returnCode='" + returnCode + '\'' +
                ", errCode='" + errCode + '\'' +
                ", insertDate=" + insertDate +
                ", appCode='" + appCode + '\'' +
                '}';
    }
}