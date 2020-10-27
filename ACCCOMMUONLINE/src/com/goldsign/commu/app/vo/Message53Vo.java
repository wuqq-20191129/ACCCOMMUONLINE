/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.goldsign.commu.app.vo;

/**
 *
 * @author zhangjh
 */
public class Message53Vo extends RechargeReqVo {

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
     *
     */
    private String operatorId = "000000";

    /**
     * @return 上次交易终端编号
     */
    public String getLastTranTermNo() {
        return lastTranTermNo;
    }

    /**
     * @param lastTranTermNo 设置上次交易终端编号
     */
    public void setLastTranTermNo(String lastTranTermNo) {
        this.lastTranTermNo = lastTranTermNo;
    }

    /**
     * @return the 上次交易时间
     */
    public String getLastDealTime() {
        return lastDealTime;
    }

    /**
     * @param lastDealTime 设置上次交易时间
     */
    public void setLastDealTime(String lastDealTime) {
        this.lastDealTime = lastDealTime;
    }

    /**
     * @return 操作员编码
     *
     */
    public String getOperatorId() {
        return operatorId;
    }

    /**
     * @param operatorId 设置操作员编码
     *
     */
    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

}
