package cn.ding.fastjson;

import com.alibaba.fastjson.JSONObject;

public class Main {
    public static void main(String[] args) {
        String str = "{\"r_code\":\"code\"}";
        Bean bean = JSONObject.parseObject(str, Bean.class);
        System.out.println(bean.getRCode());
        /**
         * fastjson-1.1.27 输出内容：
         * method:setRCode()
         * code
         *
         * fastjson-1.1.33 输出内容：
         * null
         */
    }
}