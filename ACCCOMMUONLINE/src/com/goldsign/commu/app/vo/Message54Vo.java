/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.vo;

/**
 * 充值撤销确认
 * 
 * @author zhangjh
 */
public class Message54Vo extends RechargeReqVo {

	/**
	 * TAC
	 */
	private String tac = "00000000";
	/**
	 * 写卡结果
	 */
	private String writeRslt = "0";
	/**
	 * 操作员编码
	 */
	private String operatorId = "000000";
	/**
	 * 系统参照号
	 */
	private long sysRefNo = 0;
	/**
	 * 系统时间
	 */
	private String sysdate = "00000000000000";

	/**
	 * @return the tac
	 */
	public String getTac() {
		return tac;
	}

	/**
	 * @param tac
	 *            the tac to set
	 */
	public void setTac(String tac) {
		this.tac = tac;
	}

	/**
	 * @return the writeRslt
	 */
	public String getWriteRslt() {
		return writeRslt;
	}

	/**
	 * @param writeRslt
	 *            the writeRslt to set
	 */
	public void setWriteRslt(String writeRslt) {
		this.writeRslt = writeRslt;
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

	/**
	 * @return the sysRefNo
	 */
	public long getSysRefNo() {
		return sysRefNo;
	}

	/**
	 * @param sysRefNo
	 *            the sysRefNo to set
	 */
	public void setSysRefNo(long sysRefNo) {
		this.sysRefNo = sysRefNo;
	}

	/**
	 * @return the sysdate
	 */
	public String getSysdate() {
		return sysdate;
	}

	/**
	 * @param sysdate
	 *            the sysdate to set
	 */
	public void setSysdate(String sysdate) {
		this.sysdate = sysdate;
	}
}
