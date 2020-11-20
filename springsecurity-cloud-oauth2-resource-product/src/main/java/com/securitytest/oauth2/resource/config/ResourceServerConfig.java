package com.securitytest.oauth2.resource.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * 资源服务器相关配置类
 */
@Configuration
@EnableResourceServer   //标识为资源服务器,请求服务中的资源，就要带着token过来,找不到token或token无效则访问不了资源
@EnableGlobalMethodSecurity(prePostEnabled = true)  // 开启方法级别权限控制
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    
    public static final String RESOURCE_ID = "product-server";
    
    @Autowired
    private TokenStore tokenStore;
    
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        //当前资源服务器的资源id，认证服务器会认证客户端有没有访问这个资源id的权限，有则可以访问当前服务
        resources.resourceId(RESOURCE_ID)
                .tokenStore(tokenStore);
//                .tokenServices(tokenService());
        
    }
    
//    @Bean
//    public ResourceServerTokenServices tokenService() {
//        //远程认证服务器进行校验token是否有效
//        RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
//        //请求认证服务器校验的地址,默认情况下这个地址在认证服务器是拒绝访问的，要设置为认证通过后可访问
//        remoteTokenServices.setCheckTokenEndpointUrl("http://localhost:8090/auth/oauth/check_token");
//        remoteTokenServices.setClientId("mengxuegu-pc");
//        remoteTokenServices.setClientSecret("mengxuegu-secret");
//        return remoteTokenServices;
//    }
    
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                //SpringSecurity 不会使用也不会创建HttpSession实例
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                //授权规则配置
                //多个antMatchers是按从上到下的顺序判断，一个通过了就不会进入后续的antMatchers
                //相当于在controller中配置的方法级权限控制
                .antMatchers("/product/list").hasAuthority("sys:user:list")
                //所有请求都需要有all范围(scope)
                .antMatchers("/product/**").access("#oauth2.hasScope('PRODUCT_API')");
        
    }
}
