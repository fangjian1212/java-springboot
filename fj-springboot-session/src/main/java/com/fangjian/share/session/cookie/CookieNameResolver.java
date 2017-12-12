/*
 */

package com.fangjian.share.session.cookie;

import javax.servlet.http.HttpServletRequest;

/**
 */
public interface CookieNameResolver {
    String resolve(HttpServletRequest request);
}
