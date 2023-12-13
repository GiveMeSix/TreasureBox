package cn.ding.fastjson;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class Bean implements Serializable {

    @JSONField(name = "r_code")
    private String rCode;

    public String getRCode() {
        return rCode;
    }

    public void setRCode(String rCode) {
        System.out.println("method:setRCode()");
        this.rCode = rCode;
    }

    /**
     * 默认生成的方法
     *
     * @param rCode
     */
    public void setrCode(String rCode) {
        System.out.println("method:setrCode()");
        this.rCode = rCode;
    }

    public void setrcode(String rCode) {
        System.out.println("method:setRCode()");
        this.rCode = rCode;
    }

    public void setRcode(String rCode) {
        System.out.println("method:setRCode()");
        this.rCode = rCode;
    }
}
