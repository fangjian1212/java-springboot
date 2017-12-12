/*
 */

package com.fangjian.xj.session.core;

import com.fangjian.xj.session.SessionGroup;

import javax.servlet.http.HttpServletRequest;

/**
 */
public interface SessionGroupResolver {
    SessionGroup resolve(HttpServletRequest request);
}
