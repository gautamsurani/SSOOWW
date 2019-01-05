package com.serviceonwheel.model;

public class CouponList {
    String sr, couponID, code, msg, expire_msg;

    public String getSr() {
        return sr;
    }

    public void setSr(String sr) {
        this.sr = sr;
    }

    public String getCouponID() {
        return couponID;
    }

    public void setCouponID(String couponID) {
        this.couponID = couponID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getExpire_msg() {
        return expire_msg;
    }

    public void setExpire_msg(String expire_msg) {
        this.expire_msg = expire_msg;
    }
}