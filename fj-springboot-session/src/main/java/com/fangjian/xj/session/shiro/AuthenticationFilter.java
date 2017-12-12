package com.fangjian.xj.session.shiro;

import com.fangjian.xj.session.client.MixedAuthenticatingHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 */
public class AuthenticationFilter extends AuthenticatingFilter {
    public static final Log LOG = LogFactory.getLog(AuthenticationFilter.class);
    private MixedAuthenticatingHandler authenticatingHandler = new MixedAuthenticatingHandler();

    @Override
    protected boolean isLoginRequest(ServletRequest request, ServletResponse response) {
        return false;
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        HttpSession session = servletRequest.getSession(false);
        if (session != null) {
            String token = session.getId();
            SessionAuthToken sessionAuthToken = new SessionAuthToken(token);
            sessionAuthToken.setServletRequest(servletRequest);
            sessionAuthToken.setServletResponse(servletResponse);
            return sessionAuthToken;
        }
        return null;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        if (servletRequest.getUserPrincipal() == null) {
            authenticatingHandler.onLoginFailure(null, null, servletRequest, ((HttpServletResponse) response));
            return false;
        } else {
            return executeLogin(request, response);
        }

    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        return authenticatingHandler.onLoginSuccess(null, (HttpServletRequest) request, (HttpServletResponse) response);
    }

    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        try {
            return authenticatingHandler.onLoginFailure(null, e, (HttpServletRequest) request, (HttpServletResponse) response);
        } catch (Exception ex) {
            LOG.error("onLoginFailure throw exception", ex);
            return false;
        }
    }

    @Override
    public void setLoginUrl(String loginUrl) {
        this.authenticatingHandler.setLoginUrl(loginUrl);
        super.setLoginUrl(loginUrl);
    }

    @Override
    public void setSuccessUrl(String successUrl) {
        this.authenticatingHandler.setSuccessUrl(successUrl);
        super.setSuccessUrl(successUrl);
    }
}
