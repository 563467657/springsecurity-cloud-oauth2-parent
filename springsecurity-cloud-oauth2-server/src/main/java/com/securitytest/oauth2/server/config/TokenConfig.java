package com.securitytest.oauth2.server.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;

@Configuration
public class TokenConfig {
    
//    @Autowired  //采用redis管理token
//    private RedisConnectionFactory redisConnectionFactory;
    
    //jdbc管理token
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource dataSource() {
        return new DruidDataSource();
    }
    
    public static final String SIGNING_KEY = "mengxuegu-key";
    
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        //采用对称加密进行签名令牌，资源服务器也要采用次密钥来进行解密,来校验令牌合法性
        //jwtAccessTokenConverter.setSigningKey(SIGNING_KEY);
        //采用非对称加密jwt
        //第一个参数就是秘钥证书文件，第二个参数是秘钥库口令, 私钥进行签名
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(new ClassPathResource("oauth2.jks"), "oauth2".toCharArray());
        jwtAccessTokenConverter.setKeyPair(factory.getKeyPair("oauth2"));
        return jwtAccessTokenConverter;
    }
    
    @Bean
    public TokenStore tokenStore() {
        //通过redis管理令牌
        //return new RedisTokenStore(redisConnectionFactory);
        //通过jdbc管理令牌
        //return new JdbcTokenStore(dataSource());
        //JWT管理令牌
        return new JwtTokenStore(jwtAccessTokenConverter());
    }
}
