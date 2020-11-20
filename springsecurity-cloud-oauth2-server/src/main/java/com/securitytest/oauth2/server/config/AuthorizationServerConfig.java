package com.securitytest.oauth2.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.sql.DataSource;

/**
 * 认证服务器配置类
 */
@Configuration
@EnableAuthorizationServer  //开启了认证服务器功能
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired  //刷新令牌
    private UserDetailsService customUserDetailsService;
    
    @Autowired  //token管理方式，在TokenConfig类中已经添加到容器中了
    private TokenStore tokenStore;
    
    @Autowired
    private DataSource dataSource;
    
    @Bean
    public ClientDetailsService jdbcClientDetailsService() {
        return new JdbcClientDetailsService(dataSource);
    }
    
    /**
     * 配置被允许访问次认证服务器的客户端信息
     * 1.内存方式
     * 2.数据库方式
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //通过内存方式管理客户端信息
//        clients.inMemory()
//                .withClient("mengxuegu-pc")   //客户端id
//                .secret(passwordEncoder.encode("mengxuegu-secret")) //加密，客户端密码
//                .resourceIds("product-server")  //资源id,针对的是微服务名称,如果不配置则都可以访问
//                .authorizedGrantTypes("authorization_code", "password", "implicit", "client_credentials", "refresh_token")
//                .scopes("all")  //授权范围标识，哪部分资源可访问(all只是标识，不是说所有资源)
//                .autoApprove(false) //false 跳转到一个授权页面，手动点击授权,true不需要手动点授权，直接响应一个授权码
//                .redirectUris("http://www.mengxuegu.com/")  //客户端回调地址
//                .accessTokenValiditySeconds(60 * 60 * 8)   //访问令牌有效时长 默认为12小时
//                .refreshTokenValiditySeconds(60 * 60 * 24 * 60);    //刷新令牌有效时长，默认是30天
//                .and().withClient()   //可配置多个客户端,客户端id不能相同
        //通过jdbc管理客户端信息
        clients.withClientDetails(jdbcClientDetailsService());
        //clients.jdbc(dataSource);//和上面的代码是一样的效果
    
    }
    
    
    //授权码管理策略
    @Bean
    public AuthorizationCodeServices jdbcAuthorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }
    
    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    
    /**
     * 关于认证服务器端点配置
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        //password模式要这个实例
        endpoints.authenticationManager(authenticationManager);
        //刷新令牌时需要使用
        endpoints.userDetailsService(customUserDetailsService);
        //令牌的管理方式
        endpoints.tokenStore(tokenStore);
        //如果使用jwt令牌，则主要指定对应的转换器，否则不需要此行代码
        endpoints.accessTokenConverter(jwtAccessTokenConverter);
        //授权码管理策略,会把产生的授权码放到oauth_code表中,如果这个授权码已经使用了，则对应这个表中的数据就会被删除
        endpoints.authorizationCodeServices(jdbcAuthorizationCodeServices());
    }
    
    /**
     * 令牌端点的安全配置
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //括号中的内容来自SecurityExpressionRoot
        //所有人可以访问 /oauth/token_key 后面需要获取公钥，默认拒绝访问
        security.tokenKeyAccess("permitAll()");
        //所有人可以访问 /oauth/check_token 默认拒绝访问
        security.checkTokenAccess("isAuthenticated()");
    }
}
