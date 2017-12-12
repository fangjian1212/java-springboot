/*
 */

package com.fangjian.share.session.core;

import com.fangjian.share.session.SessionGroup;

import javax.servlet.http.HttpServletRequest;

/**
 */
public interface SessionGroupResolver {
    SessionGroup resolve(HttpServletRequest request);
}
