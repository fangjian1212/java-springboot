package com.fangjian.share.session.client;

import com.fangjian.share.session.Constants;
import com.fangjian.share.session.util.JacksonUtils;
import com.fangjian.share.session.util.WebUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 */
public class MixedAuthenticatingHandler implements AuthenticatingHandler {
    public static final Log LOG = LogFactory.getLog(MixedAuthenticatingHandler.class);
    private String loginUrl;
    private String successUrl;

    @Override
    public boolean onLoginSuccess(AuthenticationToken token, HttpServletRequest request, HttpServletResponse response) {
        return true;
    }

    @Override
    public boolean onLoginFailure(AuthenticationToken token, Exception e, HttpServletRequest request, HttpServletResponse response) {
        if (WebUtils.isCrossDomainFlightRequest(request)) {
            //cross domain flight request
            WebUtils.allowCrossDomian(request, response);
        } else if (WebUtils.isAPIRequest(request)) {
            //API请求
            if (WebUtils.isCorsRequest(request)) {
                //对cors跨域支持
                WebUtils.allowCrossDomian(request, response);
            }
            JacksonUtils.writeJsonToResponse(request, response, Constants.JSON_UNAUTHENTICATED);
        } else {
            //浏览器请求
            try {
                WebUtils.saveLastRequest(request);
                LOG.debug("重定向到登录页[" + loginUrl + "]");
                response.sendRedirect(getLoginUrl());
            } catch (IOException ex) {
                LOG.error("重定向到登录页[" + loginUrl + "]异常", ex);
            }

        }
        return false;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }
}
