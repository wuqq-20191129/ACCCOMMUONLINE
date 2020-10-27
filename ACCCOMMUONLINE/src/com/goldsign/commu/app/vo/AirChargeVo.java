package com.goldsign.commu.app.vo;

import java.util.Date;

/**
 * @datetime 2017-12-6
 * @author lind
 * 空中充值申请实体类
 */

public class AirChargeVo {
    private Long waterNo;

    private String messageId;

    private String msgGenTime;

    private String terminaNo;

    private String samLogicalId;

    private Long terminaSeq;

    private String branchesCode;

    private String issMainCode;

    private String issSubCode;

    private String cardType;

    private String imsi;

    private String imei;

    private String cardLogicalId;

    private String cardPhyId;

    private String isTestFlag;

    private Long onlTranTimes;

    private Long offlTranTimes;

    private String bussType;

    private String valueType;

    private Long chargeFee;

    private Long balance;

    private String mac1;

    private String tkChgeSeq;

    private String lastTranTermno;

    private String lastTranTime;

    private String operatorId;

    private String phoneNo;

    private String paidChannelType;

    private String paidChannelCode;

    private String mac2;

    private String dealTime;

    private Long sysRefNo;

    private String returnCode;

    private String errCode;

    private Date insertDate;
    
    private Long sysRefNoChr;

    public AirChargeVo(Long waterNo, String messageId, String msgGenTime, String terminaNo, String samLogicalId, 
            Long terminaSeq, String branchesCode, String issMainCode, String issSubCode, String cardType, 
            String imsi, String imei, String cardLogicalId, String cardPhyId, String isTestFlag, Long onlTranTimes, Long offlTranTimes, 
            String bussType, String valueType, Long chargeFee, Long balance, String mac1, String tkChgeSeq, String lastTranTermno, String lastTranTime, 
            String operatorId, String phoneNo, String paidChannelType, String paidChannelCode, String mac2, String dealTime, Long sysRefNo, 
            String returnCode, String errCode, Date insertDate, Long sysRefNoChr) {
        this.waterNo = waterNo;
        this.messageId = messageId;
        this.msgGenTime = msgGenTime;
        this.terminaNo = terminaNo;
        this.samLogicalId = samLogicalId;
        this.terminaSeq = terminaSeq;
        this.branchesCode = branchesCode;
        this.issMainCode = issMainCode;
        this.issSubCode = issSubCode;
        this.cardType = cardType;
        this.imsi = imsi;
        this.imei = imei;
        this.cardLogicalId = cardLogicalId;
        this.cardPhyId = cardPhyId;
        this.isTestFlag = isTestFlag;
        this.onlTranTimes = onlTranTimes;
        this.offlTranTimes = offlTranTimes;
        this.bussType = bussType;
        this.valueType = valueType;
        this.chargeFee = chargeFee;
        this.balance = balance;
        this.mac1 = mac1;
        this.tkChgeSeq = tkChgeSeq;
        this.lastTranTermno = lastTranTermno;
        this.lastTranTime = lastTranTime;
        this.operatorId = operatorId;
        this.phoneNo = phoneNo;
        this.paidChannelType = paidChannelType;
        this.paidChannelCode = paidChannelCode;
        this.mac2 = mac2;
        this.dealTime = dealTime;
        this.sysRefNo = sysRefNo;
        this.returnCode = returnCode;
        this.errCode = errCode;
        this.insertDate = insertDate;
        this.sysRefNoChr = sysRefNoChr;
    }

    public Long getSysRefNoChr() {
        return sysRefNoChr;
    }

    public void setSysRefNoChr(Long sysRefNoChr) {
        this.sysRefNoChr = sysRefNoChr;
    }

    public AirChargeVo() {
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

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType == null ? null : cardType.trim();
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

    public Long getOnlTranTimes() {
        return onlTranTimes;
    }

    public void setOnlTranTimes(Long onlTranTimes) {
        this.onlTranTimes = onlTranTimes;
    }

    public Long getOfflTranTimes() {
        return offlTranTimes;
    }

    public void setOfflTranTimes(Long offlTranTimes) {
        this.offlTranTimes = offlTranTimes;
    }

    public String getBussType() {
        return bussType;
    }

    public void setBussType(String bussType) {
        this.bussType = bussType == null ? null : bussType.trim();
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType == null ? null : valueType.trim();
    }

    public Long getChargeFee() {
        return chargeFee;
    }

    public void setChargeFee(Long chargeFee) {
        this.chargeFee = chargeFee;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public String getMac1() {
        return mac1;
    }

    public void setMac1(String mac1) {
        this.mac1 = mac1 == null ? null : mac1.trim();
    }

    public String getTkChgeSeq() {
        return tkChgeSeq;
    }

    public void setTkChgeSeq(String tkChgeSeq) {
        this.tkChgeSeq = tkChgeSeq == null ? null : tkChgeSeq.trim();
    }

    public String getLastTranTermno() {
        return lastTranTermno;
    }

    public void setLastTranTermno(String lastTranTermno) {
        this.lastTranTermno = lastTranTermno == null ? null : lastTranTermno.trim();
    }

    public String getLastTranTime() {
        return lastTranTime;
    }

    public void setLastTranTime(String lastTranTime) {
        this.lastTranTime = lastTranTime == null ? null : lastTranTime.trim();
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId == null ? null : operatorId.trim();
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo == null ? null : phoneNo.trim();
    }

    public String getPaidChannelType() {
        return paidChannelType;
    }

    public void setPaidChannelType(String paidChannelType) {
        this.paidChannelType = paidChannelType == null ? null : paidChannelType.trim();
    }

    public String getPaidChannelCode() {
        return paidChannelCode;
    }

    public void setPaidChannelCode(String paidChannelCode) {
        this.paidChannelCode = paidChannelCode == null ? null : paidChannelCode.trim();
    }

    public String getMac2() {
        return mac2;
    }

    public void setMac2(String mac2) {
        this.mac2 = mac2 == null ? null : mac2.trim();
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
        return "AirChargeVo{" +
                "waterNo=" + waterNo +
                ", messageId='" + messageId + '\'' +
                ", msgGenTime='" + msgGenTime + '\'' +
                ", terminaNo='" + terminaNo + '\'' +
                ", samLogicalId='" + samLogicalId + '\'' +
                ", terminaSeq=" + terminaSeq +
                ", branchesCode='" + branchesCode + '\'' +
                ", issMainCode='" + issMainCode + '\'' +
                ", issSubCode='" + issSubCode + '\'' +
                ", cardType='" + cardType + '\'' +
                ", imsi='" + imsi + '\'' +
                ", imei='" + imei + '\'' +
                ", cardLogicalId='" + cardLogicalId + '\'' +
                ", cardPhyId='" + cardPhyId + '\'' +
                ", isTestFlag='" + isTestFlag + '\'' +
                ", onlTranTimes=" + onlTranTimes +
                ", offlTranTimes=" + offlTranTimes +
                ", bussType='" + bussType + '\'' +
                ", valueType='" + valueType + '\'' +
                ", chargeFee=" + chargeFee +
                ", balance=" + balance +
                ", mac1='" + mac1 + '\'' +
                ", tkChgeSeq='" + tkChgeSeq + '\'' +
                ", lastTranTermno='" + lastTranTermno + '\'' +
                ", lastTranTime='" + lastTranTime + '\'' +
                ", operatorId='" + operatorId + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", paidChannelType='" + paidChannelType + '\'' +
                ", paidChannelCode='" + paidChannelCode + '\'' +
                ", mac2='" + mac2 + '\'' +
                ", dealTime='" + dealTime + '\'' +
                ", sysRefNo=" + sysRefNo +
                ", returnCode='" + returnCode + '\'' +
                ", errCode='" + errCode + '\'' +
                ", insertDate=" + insertDate +
                ", sysRefNoChr=" + sysRefNoChr +
                '}';
    }
}