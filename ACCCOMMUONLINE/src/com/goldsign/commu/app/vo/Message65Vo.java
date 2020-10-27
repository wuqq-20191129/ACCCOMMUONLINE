/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.vo;

/**
 * 激活响应
 * 
 * @author zhangjh
 */
public class Message65Vo extends BaseRspVo {

	/**
	 * MAC
	 */
	private String mac = "00000000";
	/**
	 * 系统参照号
	 */
	private long sysRefNo = 0;

	/**
	 * @return the mac
	 */
	public String getMac() {
		return mac;
	}

	/**
	 * @param mac
	 *            the mac to set
	 */
	public void setMac(String mac) {
		this.mac = mac;
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
