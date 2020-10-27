/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.goldsign.commu.app.vo;

/**
 * @datetime 2018-1-4 18:11:09
 * @author lind
 * 加密机参数、结果类
 */
public class EncryptorVo {
    
    private String mac = "00000000";//mac码
    private String errCode = "00";//错误码
    private String returnCode = "00";//响应码 00正常 01不符合业务
    private String chargeMsg = "";//发送指令
    
    //业务参数
    private String cardLogicalId;//票卡逻辑卡号
    private Long onlTranTimes;//钱包联机交易序号
    private String tkChgeSeq;//伪随机数
    private String samLogicalId;//Sam卡逻辑卡号
    private Long chargeFee;//交易金额
    private Long balance;//钱包余额
    private String dealTime;//交易时间
    private String randomNum = "00000000";//随机数

    public String getRandomNum() {
        return randomNum;
    }

    public void setRandomNum(String randomNum) {
        this.randomNum = randomNum;
    }
	

    public String getCardLogicalId() {
        return cardLogicalId;
    }

    public void setCardLogicalId(String cardLogicalId) {
        this.cardLogicalId = cardLogicalId;
    }

    public Long getOnlTranTimes() {
        return onlTranTimes;
    }

    public void setOnlTranTimes(Long onlTranTimes) {
        this.onlTranTimes = onlTranTimes;
    }

    public String getTkChgeSeq() {
        return tkChgeSeq;
    }

    public void setTkChgeSeq(String tkChgeSeq) {
        this.tkChgeSeq = tkChgeSeq;
    }

    public String getSamLogicalId() {
        return samLogicalId;
    }

    public void setSamLogicalId(String samLogicalId) {
        this.samLogicalId = samLogicalId;
    }

    public Long getChargeFee() {
        return chargeFee;
    }

    public void setChargeFee(Long chargeFee) {
        this.chargeFee = chargeFee;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public String getDealTime() {
        return dealTime;
    }

    public void setDealTime(String dealTime) {
        this.dealTime = dealTime;
    }
    
    

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getChargeMsg() {
        return chargeMsg;
    }

    public void setChargeMsg(String chargeMsg) {
        this.chargeMsg = chargeMsg;
    }

    public static void main(String[] args) {
        EncryptorVo encoderVo = new EncryptorVo();
        System.out.println(encoderVo.getMac());
    }
}
