/*
 */

package com.fangjian.share.session.cookie;

import javax.servlet.http.HttpServletRequest;

/**
 */
public class FixedCookieNameResolver implements CookieNameResolver {
    private String cookieName;

    public FixedCookieNameResolver(String cookieName) {
        if (cookieName == null) {
            throw new IllegalArgumentException("cookieName cannot be null");
        }
        this.cookieName = cookieName;
    }

    @Override
    public String resolve(HttpServletRequest request) {
        return cookieName;
    }
}
