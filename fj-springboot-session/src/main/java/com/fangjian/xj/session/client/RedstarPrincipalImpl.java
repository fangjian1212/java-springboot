/*
 */
package com.fangjian.xj.session.client;

/**
 */
public class RedstarPrincipalImpl implements RedstarPrincipal {
    private String openId;

    public RedstarPrincipalImpl(String openId) {
        this.openId = openId;
    }

    @Override
    public String getOpenId() {
        return openId;
    }

    @Override
    public String getName() {
        return openId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RedstarPrincipalImpl that = (RedstarPrincipalImpl) o;

        return getOpenId() != null ? getOpenId().equals(that.getOpenId()) : that.getOpenId() == null;

    }

    @Override
    public int hashCode() {
        return getOpenId() != null ? getOpenId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return openId;
    }
}
