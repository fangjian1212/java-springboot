/*
 */

package com.fangjian.xj.session.cookie;

import javax.servlet.http.HttpServletRequest;

/**
 */
public interface CookieNameResolver {
    String resolve(HttpServletRequest request);
}
