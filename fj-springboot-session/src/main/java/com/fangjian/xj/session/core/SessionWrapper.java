/*
 */

package com.fangjian.xj.session.core;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

class SessionWrapper implements HttpSession {
    private HttpSession delegate;

    public SessionWrapper(HttpSession delegate) {
        this.delegate = delegate;
    }

    @Override
    public long getCreationTime() {
        return delegate.getCreationTime();
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public long getLastAccessedTime() {
        return delegate.getLastAccessedTime();
    }

    @Override
    public ServletContext getServletContext() {
        return delegate.getServletContext();
    }

    @Override
    public void setMaxInactiveInterval(int interval) {
        delegate.setMaxInactiveInterval(interval);
    }

    @Override
    public int getMaxInactiveInterval() {
        return delegate.getMaxInactiveInterval();
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return delegate.getSessionContext();
    }

    @Override
    public Object getAttribute(String name) {
        return delegate.getAttribute(name);
    }

    @Override
    public Object getValue(String name) {
        return delegate.getValue(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return delegate.getAttributeNames();
    }

    @Override
    public String[] getValueNames() {
        return delegate.getValueNames();
    }

    @Override
    public void setAttribute(String name, Object value) {
        delegate.setAttribute(name, value);
    }

    @Override
    public void putValue(String name, Object value) {
        delegate.putValue(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        delegate.removeAttribute(name);
    }

    @Override
    public void removeValue(String name) {
        delegate.removeValue(name);
    }

    @Override
    public void invalidate() {
        delegate.invalidate();
    }

    @Override
    public boolean isNew() {
        return delegate.isNew();
    }

    public HttpSession getSession() {
        return delegate;
    }
}