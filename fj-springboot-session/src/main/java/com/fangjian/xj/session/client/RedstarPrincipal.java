/*
 */
package com.fangjian.xj.session.client;

import java.io.Serializable;
import java.security.Principal;

/**
 */
public interface RedstarPrincipal extends Principal, Serializable {
    String getOpenId();
}
