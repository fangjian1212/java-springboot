package com.fangjian.share.session.core;

import com.fangjian.share.session.SessionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.session.web.http.RequestResponsePostProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 */
class SessionResponsePostProcessor implements RequestResponsePostProcessor {
    public static final Log LOG = LogFactory.getLog(SessionResponsePostProcessor.class);

    private boolean allowCreateSession;
    private boolean allowManualTimeout;

    public SessionResponsePostProcessor(boolean allowCreateSession, boolean allowManualTimeout) {
        this.allowCreateSession = allowCreateSession;
        this.allowManualTimeout = allowManualTimeout;
    }

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
            if(create && !allowCreateSession){
                throw new SessionException("don't allow to create session, you can set the 'allowCreateSession' of @EnableSession as true");
            }
            HttpSession session = super.getSession(create);
            if(session != null) {
                return new TimeoutSessionWrapper(session);
            }else {
                return null;
            }
        }

        @Override
        public HttpSession getSession() {
            if(!allowCreateSession){
                throw new SessionException("don't allow to create session, you can set the 'allowCreateSession' of @EnableSession as true");
            }
            HttpSession session = super.getSession();
            if(session != null) {
                return new TimeoutSessionWrapper(session);
            }else {
                return null;
            }
        }
    }

    private class TimeoutSessionWrapper extends SessionWrapper {

        public TimeoutSessionWrapper(HttpSession delegate) {
            super(delegate);
        }

        @Override
        public void setMaxInactiveInterval(int interval) {
            if(allowManualTimeout) {
                super.setMaxInactiveInterval(interval);
            }else{
                LOG.warn("don't allow to set timeout time manually, you can set the 'allowManualTimeout' of @EnableSession as true");
            }
        }

        @Override
        public Enumeration<String> getAttributeNames() {

            Enumeration<String> attributeNames = super.getAttributeNames();
            Set<String> names = new HashSet<>();
            while (attributeNames.hasMoreElements()) {
                names.add(attributeNames.nextElement());
            }
            return Collections.enumeration(names);
        }
    }
}
