package com.fangjian.xj.session;

/**
 */
public class Constants {
    public static final String DEFAULT_COOKIE_NAME = "SESSION";
    public static final String JSON_UNAUTHENTICATED = "{\"code\":-401,\"message\":\"未登录\"}";
    //    public static final String DEFAULT_OPENID_SESSION_KEY = "uc.user.openId";
//    public static final String DEFAULT_USERID_SESSION_KEY = "uc.user.userId";
    public static final String DEFAULT_ROLE_SESSION_KEY = "uc.user.roles";
    public static final String DEFAULT_PERMISSION_SESSION_KEY = "uc.user.permissions";
    public static final String DEFAULT_PRINCIPAL_SESSION_KEY = "uc.user.principal";
    public static final String PRINCIPAL_NAME_INDEX_NAME = "PRINCIPAL_NAME_INDEX";
    public static final String DEFAULT_COOKIE_DOMAIN_PATTERN = "^.+?\\.(\\w+\\.[a-z]+)$";
}
