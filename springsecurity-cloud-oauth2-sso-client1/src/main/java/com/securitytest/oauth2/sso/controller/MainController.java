package com.securitytest.oauth2.sso.controller;

import com.securitytest.base.result.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    
    @Autowired
    private OAuth2RestTemplate oAuth2RestTemplate;
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/member")
    public String member() {
        ResponseEntity<JsonResult> entity = oAuth2RestTemplate.getForEntity("http://localhost:7001/product/list", JsonResult.class);
        JsonResult body = entity.getBody();
        System.out.println("body:" + body);
        return "member";
    }
}
