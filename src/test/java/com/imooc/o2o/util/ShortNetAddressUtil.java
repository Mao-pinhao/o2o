//package com.imooc.o2o.util;
//
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class ShortNetAddressUtil {
//
//    public static String getShortURL(String longUrl) {
//        longUrl = java.net.URLEncoder.encode(longUrl);
//        String appkey = APPKEY;
//        String[] sourceArray = appkey.split(",");
//        for (String key : sourceArray) {
//            String shortUrl = sinaShortUrl(key, longUrl);
//            if (shortUrl != null) {
//                return shortUrl;
//            }
//        }
//        return null;
//    }
//}
