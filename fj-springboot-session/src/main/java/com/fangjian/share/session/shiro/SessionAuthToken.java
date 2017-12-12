package com.fangjian.share.session.shiro;

import org.apache.shiro.authc.AuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 */
public class SessionAuthToken implements AuthenticationToken {
    private String token;
    private String openId;
    private HttpServletRequest servletRequest;
    private HttpServletResponse servletResponse;

    public SessionAuthToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return openId;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    public void setServletRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    public HttpServletResponse getServletResponse() {
        return servletResponse;
    }

    public void setServletResponse(HttpServletResponse servletResponse) {
        this.servletResponse = servletResponse;
    }
}
