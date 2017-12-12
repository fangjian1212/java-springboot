/*
 */
package com.fangjian.xj.session.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 */
public class UserLoginFilter extends PathMatchingFilter {
    private static final Log LOG = LogFactory.getLog(UserLoginFilter.class);
    private MixedAuthenticatingHandler authenticatingHandler = new MixedAuthenticatingHandler();

    @Override
    protected void onMatching(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getUserPrincipal() != null) {
            //已登录
            filterChain.doFilter(request, response);
        } else {
            //未登录
            authenticatingHandler.onLoginFailure(null, null, request, response);
        }
    }

    public void setLoginUrl(String loginUrl) {
        authenticatingHandler.setLoginUrl(loginUrl);
    }

}
