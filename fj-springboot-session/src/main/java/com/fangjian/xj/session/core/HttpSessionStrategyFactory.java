/*
 */
package com.fangjian.xj.session.core;

import org.springframework.session.Session;
import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.session.web.http.MultiHttpSessionStrategy;
import org.springframework.session.web.http.RequestResponsePostProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 */
class HttpSessionStrategyFactory {
    static MultiHttpSessionStrategy createMultiHttpSessionStrategy(HttpSessionStrategy httpSessionStrategy) {
        if (httpSessionStrategy instanceof MultiHttpSessionStrategy) {
            return ((MultiHttpSessionStrategy) httpSessionStrategy);
        }
        return new MultiHttpSessionStrategyAdapter(httpSessionStrategy, null);
    }

    static MultiHttpSessionStrategy createMultiHttpSessionStrategy(RequestResponsePostProcessor requestResponsePostProcessor) {
        if (requestResponsePostProcessor instanceof MultiHttpSessionStrategy) {
            return ((MultiHttpSessionStrategy) requestResponsePostProcessor);
        }
        return new MultiHttpSessionStrategyAdapter(null, requestResponsePostProcessor);
    }

    static class MultiHttpSessionStrategyAdapter implements MultiHttpSessionStrategy {

        private final HttpSessionStrategy httpSessionStrategy;
        private final RequestResponsePostProcessor requestResponsePostProcessor;

        public MultiHttpSessionStrategyAdapter(HttpSessionStrategy httpSessionStrategy, RequestResponsePostProcessor requestResponsePostProcessor) {
            this.httpSessionStrategy = httpSessionStrategy;
            this.requestResponsePostProcessor = requestResponsePostProcessor;
        }

        @Override
        public String getRequestedSessionId(HttpServletRequest request) {
            if (httpSessionStrategy != null) {
                return httpSessionStrategy.getRequestedSessionId(request);
            }
            return null;
        }

        @Override
        public void onNewSession(Session session, HttpServletRequest request, HttpServletResponse response) {
            if (httpSessionStrategy != null) {
                httpSessionStrategy.onNewSession(session, request, response);
            }
        }

        @Override
        public void onInvalidateSession(HttpServletRequest request, HttpServletResponse response) {
            if (httpSessionStrategy != null) {
                httpSessionStrategy.onInvalidateSession(request, response);
            }
        }

        @Override
        public HttpServletRequest wrapRequest(HttpServletRequest request, HttpServletResponse response) {
            if (requestResponsePostProcessor != null) {
                return requestResponsePostProcessor.wrapRequest(request, response);
            } else {
                return request;
            }
        }

        @Override
        public HttpServletResponse wrapResponse(HttpServletRequest request, HttpServletResponse response) {
            if (requestResponsePostProcessor != null) {
                return requestResponsePostProcessor.wrapResponse(request, response);
            } else {
                return response;
            }
        }
    }

}
