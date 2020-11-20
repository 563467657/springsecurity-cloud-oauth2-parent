package com.securitytest.oauth2.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 请求资源前，先通过此过滤器进行用户信息解析和校验转发
 */
@Component
@Slf4j
public class AuthenticationFilter extends ZuulFilter {
    
    @Override
    public String filterType() {
        return "pre";
    }
    
    @Override
    public int filterOrder() {
        return 0;
    }
    
    @Override
    public boolean shouldFilter() {
        return true;
    }
    
    @Override
    public Object run() throws ZuulException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //如果解析到令牌就会封装到OAuth2Authentication对象
        if (!(authentication instanceof OAuth2Authentication)) {
            return null;
        }
        log.info("网关获取到的认证对象：" + authentication);
        //用户名，没有其他用户信息
        Object principal = authentication.getPrincipal();
        //获取用户所拥有的权限
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Set<String> authoritySet = AuthorityUtils.authorityListToSet(authorities);
        //请求详情
        Object details = authentication.getDetails();
    
        Map<String, Object> result = new HashMap<>();
        result.put("principal", principal);
        result.put("authorities", authoritySet);
        result.put("details", details);
        //获取当前请求上下文
        RequestContext currentContext = RequestContext.getCurrentContext();
        //将用户信息和权限信息转成json，再通过base64进行编码
        String base64 = Base64Utils.encodeToString(JSON.toJSONString(result).getBytes());
        //添加到请求头
        currentContext.addZuulRequestHeader("auth-token", base64);
        return null;
    }
}
