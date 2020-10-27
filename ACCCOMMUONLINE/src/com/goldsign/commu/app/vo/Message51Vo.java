/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.vo;

/**
 * 充值申请
 * 
 * @author zhangjh
 */
public class Message51Vo extends RechargeReqVo {

	/**
	 * MAC1
	 */
	private String mac1 = "00000000";
	/**
	 * 卡片充值随机数
	 */
	private String tkChgeSeq = "00000000";
	/**
	 * 上次交易终端编号
	 */
	private String lastTranTermNo = "0000000000000000";
	/**
	 * 上次交易时间
	 */
	private String lastDealTime = "00000000000000";
	/**
	 * 操作员编码
	 */
	private String operatorId = "000000";

	/**
	 * @return the mac1
	 */
	public String getMac1() {
		return mac1;
	}

	/**
	 * @param mac1
	 *            the mac1 to set
	 */
	public void setMac1(String mac1) {
		this.mac1 = mac1;
	}

	/**
	 * @return the tkChgeSeq
	 */
	public String getTkChgeSeq() {
		return tkChgeSeq;
	}

	/**
	 * @param tkChgeSeq
	 *            the tkChgeSeq to set
	 */
	public void setTkChgeSeq(String tkChgeSeq) {
		this.tkChgeSeq = tkChgeSeq;
	}

	/**
	 * @return the lastTranTermNo
	 */
	public String getLastTranTermNo() {
		return lastTranTermNo;
	}

	/**
	 * @param lastTranTermNo
	 *            the lastTranTermNo to set
	 */
	public void setLastTranTermNo(String lastTranTermNo) {
		this.lastTranTermNo = lastTranTermNo;
	}

	/**
	 * @return the lastDealTime
	 */
	public String getLastDealTime() {
		return lastDealTime;
	}

	/**
	 * @param lastDealTime
	 *            the lastDealTime to set
	 */
	public void setLastDealTime(String lastDealTime) {
		this.lastDealTime = lastDealTime;
	}

	/**
	 * @return the operatorId
	 */
	public String getOperatorId() {
		return operatorId;
	}

	/**
	 * @param operatorId
	 *            the operatorId to set
	 */
	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}
}
