package com.securitytest.oauth2.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class SsoClient1Application {
    
    public static void main(String[] args) {
        SpringApplication.run(SsoClient1Application.class, args);
    }
}
