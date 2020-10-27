/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.vo;

/**
 * 充值撤销确认响应
 * 
 * @author zhangjh
 */
public class Message64Vo extends RechargeRspVo {
	/**
	 * 系统参照号
	 */
	private long sysRefNo = 0;

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
