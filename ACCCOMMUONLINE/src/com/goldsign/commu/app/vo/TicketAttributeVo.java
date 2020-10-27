package com.goldsign.commu.app.vo;

/**
 * Description:
 *
 * @author: zhongziqi
 * @Date: 2019-10-08
 * @Time: 9:29
 */
public class TicketAttributeVo {
    private String cardMainId;
    private String cardSubId;
    private long faceValue;
    private long depositFee;
    private long ticketUsedValidDays;
    private long ticketUsedPreValidDays;

    public long getTicketUsedPreValidDays() {
        return ticketUsedPreValidDays;
    }

    public void setTicketUsedPreValidDays(long ticketUsedPreValidDays) {
        this.ticketUsedPreValidDays = ticketUsedPreValidDays;
    }

    private String saleActivateFlag;
    private long chargeMaxLimit;


    public String getCardMainId() {
        return cardMainId;
    }

    public void setCardMainId(String cardMainId) {
        this.cardMainId = cardMainId;
    }

    public String getCardSubId() {
        return cardSubId;
    }

    public void setCardSubId(String cardSubId) {
        this.cardSubId = cardSubId;
    }

    public long getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(long faceValue) {
        this.faceValue = faceValue;
    }

    public long getDepositFee() {
        return depositFee;
    }

    public void setDepositFee(long depositFee) {
        this.depositFee = depositFee;
    }

    public long getTicketUsedValidDays() {
        return ticketUsedValidDays;
    }

    public void setTicketUsedValidDays(long ticketUsedValidDays) {
        this.ticketUsedValidDays = ticketUsedValidDays;
    }

    public long getChargeMaxLimit() {
        return chargeMaxLimit;
    }

    public void setChargeMaxLimit(long chargeMaxLimit) {
        this.chargeMaxLimit = chargeMaxLimit;
    }

    public String getSaleActivateFlag() {
        return saleActivateFlag;
    }

    public void setSaleActivateFlag(String saleActivateFlag) {
        this.saleActivateFlag = saleActivateFlag;
    }
}
