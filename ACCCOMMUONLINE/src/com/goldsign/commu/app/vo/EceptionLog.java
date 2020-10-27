/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.vo;

import java.util.Date;

/**
 * 
 * @author zhangjh
 */
public class EceptionLog {

	/**
     *
     */
	private long id;
	/**
	 * ip地址
	 */
	private String ip;
	/**
	 * 异常信息对应的id标识
	 */
	private String excpId;
	/**
	 * 详细的异常信息
	 */
	private String excpType;
	/**
	 * 发生异常的类名
	 */
	private String className;
	/**
	 * 详细异常信息
	 */
	private String excpDesc;
	/**
     *
     */
	private Date insertDate;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the excpId
	 */
	public String getExcpId() {
		return excpId;
	}

	/**
	 * @param excpId
	 *            the excpId to set
	 */
	public void setExcpId(String excpId) {
		this.excpId = excpId;
	}

	/**
	 * @return the excpType
	 */
	public String getExcpType() {
		return excpType;
	}

	/**
	 * @param excpType
	 *            the excpType to set
	 */
	public void setExcpType(String excpType) {
		this.excpType = excpType;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className
	 *            the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the insertDate
	 */
	public Date getInsertDate() {
		return insertDate;
	}

	/**
	 * @param insertDate
	 *            the insertDate to set
	 */
	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	/**
	 * @return the excpDesc
	 */
	public String getExcpDesc() {
		return excpDesc;
	}

	/**
	 * @param excpDesc
	 *            the excpDesc to set
	 */
	public void setExcpDesc(String excpDesc) {
		this.excpDesc = excpDesc;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip
	 *            the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}
}
