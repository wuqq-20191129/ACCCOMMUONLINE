/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.vo;

/**
 * 基础消息类

 * 
 * @author zhangjh
 */
public class BaseVo {

	/**
	 * 消息类型
	 */
	private String messageId;
	/**
	 * 消息生成时间
	 */
	private String msgGenTime;
	/**
	 * 终端编号
	 */
	private String terminalNo = "000000000";
	/**
	 * Sam卡逻辑卡号
	 */
	private String samLogicalId = "0000000000000000";
	/**
	 * 主键ID
	 */
	private long waterNo;

	/**
	 * @return the terminalNo
	 */
	public String getTerminalNo() {
		return terminalNo;
	}

	/**
	 * @param terminalNo
	 *            the terminalNo to set
	 */
	public void setTerminalNo(String terminalNo) {
		this.terminalNo = terminalNo;
	}

	/**
	 * @return the messageId
	 */
	public String getMessageId() {
		return messageId;
	}

	/**
	 * @param messageId
	 *            the messageId to set
	 */
	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	/**
	 * @return the msgGenTime
	 */
	public String getMsgGenTime() {
		return msgGenTime;
	}

	/**
	 * @param msgGenTime
	 *            the msgGenTime to set
	 */
	public void setMsgGenTime(String msgGenTime) {
		this.msgGenTime = msgGenTime;
	}

	/**
	 * @return the samLogicalId
	 */
	public String getSamLogicalId() {
		return samLogicalId;
	}

	/**
	 * @param samLogicalId
	 *            the samLogicalId to set
	 */
	public void setSamLogicalId(String samLogicalId) {
		this.samLogicalId = samLogicalId == null ? null : samLogicalId.trim();
	}

	/**
	 * @return the waterNo
	 */
	public long getWaterNo() {
		return waterNo;
	}

	/**
	 * @param waterNo
	 *            the waterNo to set
	 */
	public void setWaterNo(long waterNo) {
		this.waterNo = waterNo;
	}
}
