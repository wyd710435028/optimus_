package com.unisound.optimus_visual.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class HttpUtils {

    /**
     * get请求
     * @param url
     * @return
     */
    public static String httpGet(String url){
        String response = null;
        if (url!=null){
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
            response = forEntity.getBody();
        }
        return response;
    }

    /**
     * post请求
     * @param url 请求地址
     * @param requestBody 请求体
     * @return
     */
    public static String httpPost(String url, Map<String,Object> requestBody){
        String response = null;
        if (url!=null){
            RestTemplate restTemplate = new RestTemplate();
            //定义请求头
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> mapHttpEntity = new HttpEntity<>(requestBody, requestHeaders);
            response = restTemplate.postForObject(url,mapHttpEntity,String.class);

        }
        return response;
    }
}
