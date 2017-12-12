/*
 */
package com.fangjian.xj.session.mvc;

import com.fangjian.xj.session.Constants;
import com.fangjian.xj.session.util.JacksonUtils;
import com.fangjian.xj.session.util.WebUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 */
public class UserLoginInterceptor extends HandlerInterceptorAdapter {
    private static final Log LOG = LogFactory.getLog(UserLoginInterceptor.class);

    private String loginUrl;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (WebUtils.isCrossDomainFlightRequest(request)) {
            WebUtils.allowCrossDomian(request,response);
            return false;
        }
        if (request.getUserPrincipal() != null) {
            //已登录
            return true;
        }else {
            //未登录
            onUnauthenticated(request, response);
            return false;
        }
    }

    private void onUnauthenticated(HttpServletRequest request, HttpServletResponse response) {
        if (WebUtils.isAPIRequest(request)) {
            //API请求
            if (WebUtils.isCorsRequest(request)) {
                WebUtils.allowCrossDomian(request,response);
            }
            JacksonUtils.writeJsonToResponse(request, response, Constants.JSON_UNAUTHENTICATED);
        }else{
            //浏览器请求,重定向到登录页
            saveRequestAndRedirectToLogin(request, response);
        }
    }

    private void saveRequestAndRedirectToLogin(HttpServletRequest request, HttpServletResponse response){
        WebUtils.saveLastRequest(request);
        try {
            LOG.debug("重定向到登录页["+loginUrl+"]");
            response.sendRedirect(loginUrl);
        } catch (IOException e) {
            LOG.error("重定向到登录页["+loginUrl+"]异常",e);
        }
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }
}
