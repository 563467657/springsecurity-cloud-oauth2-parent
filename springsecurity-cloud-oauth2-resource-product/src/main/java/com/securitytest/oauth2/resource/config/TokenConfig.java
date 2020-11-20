package com.securitytest.oauth2.resource.config;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.io.IOException;


@Configuration
public class TokenConfig {
    
    public static final String SIGNING_KEY = "mengxuegu-key";
    
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        //采用对称加密进行签名令牌，资源服务器也要采用次密钥来进行解密,来校验令牌合法性
        //jwtAccessTokenConverter.setSigningKey(SIGNING_KEY);
        //采用非对称加密jwt，资源服务器使用公钥解密 public.txt
        ClassPathResource resource = new ClassPathResource("public.txt");
        String publicKey = null;
        try {
            publicKey = IOUtils.toString(resource.getInputStream(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        jwtAccessTokenConverter.setVerifierKey(publicKey);
        return jwtAccessTokenConverter;
    }
    
    @Bean
    public TokenStore tokenStore() {
        //JWT管理令牌
        return new JwtTokenStore(jwtAccessTokenConverter());
    }
}
