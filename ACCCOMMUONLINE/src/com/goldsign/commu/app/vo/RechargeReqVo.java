/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.vo;

/**
 * 充值申请、充值确认、撤销充值申请、撤销充值确认的公共信息
 * 
 * @author zhangjh
 */
public class RechargeReqVo extends BaseVo {

	/**
	 * 终端交易序列号

	 */
	private long transationSeq = 0;
	/**
	 * 网点编码
	 */
	private String branchesCode = "0000000000000000";
	/**
	 * 发行者主编码
	 */
	private String pubMainCode = "0000";
	/**
	 * 发行者子编码
	 */
	private String pubSubCode = "0000";
	/**
	 * 票卡类型
	 */
	private String cardType = "0000";
	/**
	 * 票卡逻辑卡号
	 */
	private String tkLogicNo = "00000000000000000000";
	/**
	 * 票卡物理卡号
	 */
	private String tkPhyNo = "00000000000000000000";
	/**
	 * 是否测试卡

	 */
	private String isTestTk = "0";
	/**
	 * 票卡联机交易计数
	 */
	private Long onlTranTimes = 0l;
	/**
	 * 票卡脱机交易计数
	 */
	private Long offlTranTimes = 0l;
	/**
	 * 业务类型
	 */
	private String bussType = "00";
	/**
	 * 值类型

	 */
	private String valueType = "0";
	/**
	 * 充值金额

	 */
	private Long chargeFee = 0l;
	/**
	 * 票卡余额
	 */
	private Long balance = 0l;

	/**
	 * @return the branchesCode
	 */
	public String getBranchesCode() {
		return branchesCode;
	}

	/**
	 * @param branchesCode
	 *            the branchesCode to set
	 */
	public void setBranchesCode(String branchesCode) {
		this.branchesCode = branchesCode;
	}

	/**
	 * @return the pubMainCode
	 */
	public String getPubMainCode() {
		return pubMainCode;
	}

	/**
	 * @param pubMainCode
	 *            the pubMainCode to set
	 */
	public void setPubMainCode(String pubMainCode) {
		this.pubMainCode = pubMainCode;
	}

	/**
	 * @return the pubSubCode
	 */
	public String getPubSubCode() {
		return pubSubCode;
	}

	/**
	 * @param pubSubCode
	 *            the pubSubCode to set
	 */
	public void setPubSubCode(String pubSubCode) {
		this.pubSubCode = pubSubCode;
	}

	/**
	 * @return the cardType
	 */
	public String getCardType() {
		return cardType;
	}

	/**
	 * @param cardType
	 *            the cardType to set
	 */
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	/**
	 * @return the tkPhyNo
	 */
	public String getTkPhyNo() {
		return tkPhyNo;
	}

	/**
	 * @param tkPhyNo
	 *            the tkPhyNo to set
	 */
	public void setTkPhyNo(String tkPhyNo) {
		this.tkPhyNo = tkLogicNo == null ? null : tkLogicNo.trim();
	}

	/**
	 * @return the bussType
	 */
	public String getBussType() {
		return bussType;
	}

	/**
	 * @param bussType
	 *            the bussType to set
	 */
	public void setBussType(String bussType) {
		this.bussType = bussType;
	}

	/**
	 * @return the valueType
	 */
	public String getValueType() {
		return valueType;
	}

	/**
	 * @param valueType
	 *            the valueType to set
	 */
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	/**
	 * @return the tkLogicNo
	 */
	public String getTkLogicNo() {
		return tkLogicNo;
	}

	/**
	 * @param tkLogicNo
	 *            the tkLogicNo to set
	 */
	public void setTkLogicNo(String tkLogicNo) {
		this.tkLogicNo = tkLogicNo == null ? null : tkLogicNo.trim();
	}

	/**
	 * @return the transationSeq
	 */
	public long getTransationSeq() {
		return transationSeq;
	}

	/**
	 * @param transationSeq
	 *            the transationSeq to set
	 */
	public void setTransationSeq(long transationSeq) {
		this.transationSeq = transationSeq;
	}

	/**
	 * @return the isTestTk
	 */
	public String getIsTestTk() {
		return isTestTk;
	}

	/**
	 * @param isTestTk
	 *            the isTestTk to set
	 */
	public void setIsTestTk(String isTestTk) {
		this.isTestTk = isTestTk;
	}

	/**
	 * @return the onlTranTimes
	 */
	public Long getOnlTranTimes() {
		return onlTranTimes;
	}

	/**
	 * @return the offlTranTimes
	 */
	public Long getOfflTranTimes() {
		return offlTranTimes;
	}

	/**
	 * @return the chargeFee
	 */
	public Long getChargeFee() {
		return chargeFee;
	}

	/**
	 * @param chargeFee
	 *            the chargeFee to set
	 */
	public void setChargeFee(Long chargeFee) {
		this.chargeFee = chargeFee;
	}

	/**
	 * @return the balance
	 */
	public Long getBalance() {
		return balance;
	}

	/**
	 * @param balance
	 *            the balance to set
	 */
	public void setBalance(Long balance) {
		this.balance = balance;
	}

	/**
	 * @param onlTranTimes
	 *            the onlTranTimes to set
	 */
	public void setOnlTranTimes(Long onlTranTimes) {
		this.onlTranTimes = onlTranTimes;
	}

	/**
	 * @param offlTranTimes
	 *            the offlTranTimes to set
	 */
	public void setOfflTranTimes(Long offlTranTimes) {
		this.offlTranTimes = offlTranTimes;
	}

    @Override
    public String toString() {
        return "RechargeReqVo{" + "transationSeq=" + transationSeq + ", branchesCode=" + branchesCode + ", pubMainCode=" + pubMainCode + ", pubSubCode=" + pubSubCode + ", cardType=" + cardType + ", tkLogicNo=" + tkLogicNo + ", tkPhyNo=" + tkPhyNo + ", isTestTk=" + isTestTk + ", onlTranTimes=" + onlTranTimes + ", offlTranTimes=" + offlTranTimes + ", bussType=" + bussType + ", valueType=" + valueType + ", chargeFee=" + chargeFee + ", balance=" + balance + '}';
    }
        
}
