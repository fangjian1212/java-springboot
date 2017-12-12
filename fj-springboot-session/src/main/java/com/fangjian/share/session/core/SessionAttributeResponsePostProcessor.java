/*
 */

package com.fangjian.share.session.core;

import com.fangjian.share.session.redis.SerializableValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.session.web.http.RequestResponsePostProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 */
class SessionAttributeResponsePostProcessor implements RequestResponsePostProcessor {
    public static final Log LOG = LogFactory.getLog(SessionAttributeResponsePostProcessor.class);

    @Override
    public HttpServletRequest wrapRequest(HttpServletRequest request, HttpServletResponse response) {
        return new SessionRequestWrapper(request);
    }

    @Override
    public HttpServletResponse wrapResponse(HttpServletRequest request, HttpServletResponse response) {
        return response;
    }

    private class SessionRequestWrapper extends HttpServletRequestWrapper {

        public SessionRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public HttpSession getSession(boolean create) {
            HttpSession session = super.getSession(create);
            if(session != null) {
                return new AttributeSessionWrapper(session);
            }else {
                return null;
            }
        }

        @Override
        public HttpSession getSession() {
            HttpSession session = super.getSession();
            if(session != null) {
                return new AttributeSessionWrapper(session);
            }else {
                return null;
            }
        }
    }

    private class AttributeSessionWrapper extends SessionWrapper {

        public AttributeSessionWrapper(HttpSession delegate) {
            super(delegate);
        }

        @Override
        public Object getAttribute(String name) {
            Object attr = super.getAttribute(name);
            if(attr instanceof SerializableValue){
                return ((SerializableValue) attr).deserialize();
            }
            return attr;
        }
    }
}
