package com.howl.oauth.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/oauth")
public class OAuthController {

    @GetMapping("/redirect")
    public String redirect(String code){

        System.out.println(code);

        // 请求地址
        String url = "https://github.com/login/oauth/access_token";

        // POST参数必须用这个
        MultiValueMap<String,String> paramMap = new LinkedMultiValueMap();
        paramMap.add("client_id","5fbce9e9f082339a2706");
        paramMap.add("client_secret","d286c3b4eb65177595899b9d95a295f48795984e");
        paramMap.add("code",code);

        // 发送请求
        RestTemplate restTemplate = new RestTemplate();
        String content = restTemplate.postForObject(url,paramMap,String.class);

        // 获取access_token
        int start = content.indexOf("=");
        int end = content.indexOf("&");
        String access_token = content.substring(start+1,end);

        // 获取用户信息
        String getUrl = "https://api.github.com/user?access_token=" + access_token;
        String user = restTemplate.getForObject(getUrl,String.class);

        return user;
    }
}
