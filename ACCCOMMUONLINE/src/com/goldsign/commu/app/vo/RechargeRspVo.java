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
public class RechargeRspVo extends BaseRspVo {

	/**
	 * 票卡逻辑卡号
	 */
	private String tkLogicNo = "00000000000000000000";
	/**
	 * 终端交易序列号

	 */
	private long transationSeq = 0;

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
}
