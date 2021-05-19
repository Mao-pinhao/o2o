package com.imooc.o2o.util;

import com.alibaba.fastjson.JSONObject;


/**
 * 使用新浪的短链接服务生成短链接
 */


public class ShortNetAddressUtil1 {
    /**
     * 微信根据APPID和APPSECRET获取token
     *
     * @param appid
     * @param appsrcret
     * @return
     */
    public static String getToken(String appId, String appSecret) throws Exception {
        //访问地址
        String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
        String request_url = TOKEN_URL.replace("APPID", appId).replace("APPSECRET", appSecret);
        JSONObject jsonObject = HttpUtils.httpsUtil(request_url, "GET", null);
        if (null != jsonObject) {
            String access_token = jsonObject.getString("access_token");
            return access_token;
        }
        return null;
    }

    /**
     * 将长链接转为短链接(调用的微信短网址API)
     * 需要token
     *
     * @param longUrl   需要转换的长链接url
     * @param appid
     * @param appSecret
     * @return 返回转换后的短链接
     */
    public static String getShortURL(String appId, String appSecret, String longUrl) throws Exception {

        String token = getToken(appId, appSecret);
//        System.out.println(token);
        String uri = "https://api.weixin.qq.com/cgi-bin/shorturl?access_token=ACCESS_TOKEN";
        String url = uri.replace("ACCESS_TOKEN", token);
        String param = "{\"action\":\"long2short\","
                + "\"long_url\":\"" + longUrl + "\"}";
        // 调用接口创建菜单
        JSONObject jsonObject = HttpUtils.httpRequest(url, "POST", param);
        System.out.println(jsonObject.getInteger("errcode"));
        return jsonObject.getString("short_url");

    }

    public static void main(String[] args) throws Exception {
        System.out.println(getShortURL("wx168ac9bfccf7211a", "77baea575864a2f9d572b924ccd99a3b", "www.baidu.com"));
    }


}