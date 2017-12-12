package com.fangjian.xj.session.shiro;

import com.fangjian.xj.session.client.MixedAuthenticatingHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 */
public class UserFilter extends AccessControlFilter {
    private static final Log LOG = LogFactory.getLog(UserFilter.class);
    private MixedAuthenticatingHandler authenticatingHandler = new MixedAuthenticatingHandler();

    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        Subject subject = getSubject(request, response);
        return subject.getPrincipal() != null;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        return authenticatingHandler.onLoginFailure(null,null,servletRequest,servletResponse);
    }

    @Override
    public void setLoginUrl(String loginUrl) {
        authenticatingHandler.setLoginUrl(loginUrl);
        super.setLoginUrl(loginUrl);
    }

}
