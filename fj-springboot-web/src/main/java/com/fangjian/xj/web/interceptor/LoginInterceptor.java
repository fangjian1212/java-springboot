package com.fangjian.xj.web.interceptor;

import com.fangjian.xj.service.common.enums.XjServiceCodeEnum;
import com.fangjian.xj.service.common.exception.XjServiceException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

/**
 * @description:
 * @author: fangjian<jian.fang@chinaredstar.com>
 * @date: Create in 11:08 2017/11/30
 * @modified by:
 */
@Configuration
public class LoginInterceptor implements HandlerInterceptor {
    private static Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        logger.info("#####################<<LoginInterceptor Start>>######################");
        String pathInfo = httpServletRequest.getRequestURI().substring(1);
        logger.info("LoginInterceptor pathInfo:[{}]", pathInfo);

        Principal userPrincipal = httpServletRequest.getUserPrincipal();
        if (userPrincipal == null) {
            logger.info("userPrincipal==null");
            throw new XjServiceException(XjServiceCodeEnum.NO_AUTHORIZATION);
        }
        String userId = userPrincipal.getName();
        logger.info("#####################userId:[{}]", userId);
        if (StringUtils.isBlank(userId)) {
            logger.info("userId 为空");
            throw new XjServiceException(XjServiceCodeEnum.NO_AUTHORIZATION);
        }
        logger.info("#####################<<LoginInterceptor  End>>######################");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
