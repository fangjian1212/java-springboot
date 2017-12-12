/*
 */

package com.fangjian.xj.session.cookie;

import com.fangjian.xj.session.Constants;
import com.fangjian.xj.session.SessionGroup;
import com.fangjian.xj.session.core.SessionGroupResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;

/**
 */
public class SessionGroupCookieNameResolver implements CookieNameResolver {
    private static final Log LOG = LogFactory.getLog(SessionGroupCookieNameResolver.class);

    private SessionGroupResolver sessionGroupResolver;

    public SessionGroupCookieNameResolver(SessionGroupResolver sessionGroupResolver) {
        if (sessionGroupResolver == null) {
            throw new IllegalArgumentException("sessionGroupResolver cannot be null");
        }
        this.sessionGroupResolver = sessionGroupResolver;
    }

    @Override
    public String resolve(HttpServletRequest request) {
        SessionGroup sessionGroup = sessionGroupResolver.resolve(request);
        if (sessionGroup != null) {
            return Constants.DEFAULT_COOKIE_NAME + "." + sessionGroup.name().toLowerCase();
        }
        return null;
    }

}
