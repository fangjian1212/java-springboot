/*
 */
package com.fangjian.xj.session.core;

import org.apache.commons.lang3.StringUtils;
import org.springframework.session.Session;
import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.session.web.http.MultiHttpSessionStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 */
public class HttpSessionStrategyComposite implements MultiHttpSessionStrategy {
    private List<MultiHttpSessionStrategy> httpSessionStrategies;

    public HttpSessionStrategyComposite(List<MultiHttpSessionStrategy> httpSessionStrategies) {
        this.httpSessionStrategies = httpSessionStrategies;
    }

    @Override
    public String getRequestedSessionId(HttpServletRequest request) {
        String sessionId = null;
        if (httpSessionStrategies != null) {
            for (MultiHttpSessionStrategy httpSessionStrategy : httpSessionStrategies) {
                sessionId = httpSessionStrategy.getRequestedSessionId(request);
                if (StringUtils.isNotBlank(sessionId) && !sessionId.equalsIgnoreCase("null") && !sessionId.equalsIgnoreCase("undefined")) {
                    request.setAttribute(HttpSessionStrategy.class.getName(), httpSessionStrategy);
                    break;
                }
            }
        }
        return sessionId;
    }

    @Override
    public void onNewSession(Session session, HttpServletRequest request, HttpServletResponse response) {
        if (httpSessionStrategies != null) {
            for (MultiHttpSessionStrategy httpSessionStrategy : httpSessionStrategies) {
                httpSessionStrategy.onNewSession(session, request, response);
            }
        }
    }

    @Override
    public void onInvalidateSession(HttpServletRequest request, HttpServletResponse response) {
        if (httpSessionStrategies != null) {
            for (MultiHttpSessionStrategy httpSessionStrategy : httpSessionStrategies) {
                httpSessionStrategy.onInvalidateSession(request, response);
            }
        }
    }

    @Override
    public HttpServletRequest wrapRequest(HttpServletRequest request, HttpServletResponse response) {
        if (httpSessionStrategies != null) {
            for (MultiHttpSessionStrategy httpSessionStrategy : httpSessionStrategies) {
                request = httpSessionStrategy.wrapRequest(request, response);
            }
        }
        return request;
    }

    @Override
    public HttpServletResponse wrapResponse(HttpServletRequest request, HttpServletResponse response) {
        if (httpSessionStrategies != null) {
            for (MultiHttpSessionStrategy httpSessionStrategy : httpSessionStrategies) {
                response = httpSessionStrategy.wrapResponse(request, response);
            }
        }
        return response;
    }
}
