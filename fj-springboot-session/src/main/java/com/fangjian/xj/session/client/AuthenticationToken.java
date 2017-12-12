package com.fangjian.xj.session.client;

import java.io.Serializable;

public interface AuthenticationToken extends Serializable {

    /**
     * 用户标识
     */
    Object getPrincipal();

    /**
     * 用户凭证
     */
    Object getCredentials();

}
