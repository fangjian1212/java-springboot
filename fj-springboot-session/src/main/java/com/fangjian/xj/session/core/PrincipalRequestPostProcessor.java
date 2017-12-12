/*
 */
package com.fangjian.xj.session.core;

import com.fangjian.xj.session.SessionException;
import com.fangjian.xj.session.client.ObjectPrincipal;
import com.fangjian.xj.session.client.RedstarPrincipalImpl;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.web.http.RequestResponsePostProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;

/**
 */
class PrincipalRequestPostProcessor implements RequestResponsePostProcessor {
    private String principalSessionKey;

    public PrincipalRequestPostProcessor(String principalSessionKey) {
        if(principalSessionKey == null){
            throw new SessionException("principalSessionKey key is null");
        }
        this.principalSessionKey = principalSessionKey;
    }

    @Override
    public HttpServletRequest wrapRequest(HttpServletRequest request, HttpServletResponse response) {
        return new PrincipalRequestWrapper(request);
    }

    @Override
    public HttpServletResponse wrapResponse(HttpServletRequest request, HttpServletResponse response) {
        return response;
    }

    private class PrincipalRequestWrapper extends HttpServletRequestWrapper {
        public PrincipalRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public HttpSession getSession() {
            HttpSession session = super.getSession();
            if (session != null) {
                session = new PrincipalSession(session);
            }
            return session;
        }

        @Override
        public HttpSession getSession(boolean create) {
            HttpSession session = super.getSession(create);
            if (session != null) {
                session = new PrincipalSession(session);
            }
            return session;
        }

        @Override
        public Principal getUserPrincipal() {
            HttpServletRequest request = (HttpServletRequest) getRequest();
            HttpSession session = request.getSession(false);
            if(session != null){
                Object principal = session.getAttribute(principalSessionKey);
                if (principal != null) {
                    if (principal instanceof Principal) {
                        return (Principal) principal;
                    }else if(principal instanceof String){
                        return new RedstarPrincipalImpl((String) principal);
                    }else{
                        return new ObjectPrincipal(principal);
                    }
                }
            }
            return super.getUserPrincipal();
        }
    }

    private class PrincipalSession extends SessionWrapper {

        public PrincipalSession(HttpSession delegate) {
            super(delegate);
        }

        @Override
        public void setAttribute(String name, Object value) {
            if(principalSessionKey.equals(name) && value != null){
                String principalName = null;
                if(value instanceof String){
                    principalName = (String) value;
                }else if(value instanceof Principal){
                    principalName = ((Principal) value).getName();
                }
                if(principalName != null){
                    //如果设置的是用户principal，在redis记录用户<->会话的映射关系
                    super.setAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME, principalName);
                }
            }
            super.setAttribute(name, value);
        }

        @Override
        public void removeAttribute(String name) {
            if(principalSessionKey.equals(name) ){
                super.removeAttribute(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME);
            }
            super.removeAttribute(name);
        }
    }
}
