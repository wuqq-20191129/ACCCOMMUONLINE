/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.vo;

/**
 * 充值撤销申请响应
 * 
 * @author zhangjh
 */
public class Message63Vo extends RechargeRspVo {

	/**
	 * 系统时间
	 */
	private String sysdate = "00000000000000";
	/**
	 * 系统参照号
	 */
	private long sysRefNo = 0;

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

}
