package com.fangjian.xj.session.client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 */
public interface AuthenticatingHandler {
    boolean onLoginSuccess(AuthenticationToken token, HttpServletRequest request, HttpServletResponse response);

    boolean onLoginFailure(AuthenticationToken token, Exception e, HttpServletRequest request, HttpServletResponse response);
}
