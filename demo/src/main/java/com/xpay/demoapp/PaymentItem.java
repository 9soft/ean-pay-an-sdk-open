package com.xpay.demoapp;

/**
 * Created with IntelliJ IDEA.
 * @author : 402536196@qq.com
 * Time: 2018-03-20 17:32
 */
public class PaymentItem {
    private String channel;
    private String sub_channel;
    private int amount;
    private String subject;
    private String body;
    private String remark;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSub_channel() {
        return sub_channel;
    }

    public void setSub_channel(String sub_channel) {
        this.sub_channel = sub_channel;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
