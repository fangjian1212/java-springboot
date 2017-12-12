/*
 */
package com.fangjian.share.session.client;

import java.io.Serializable;
import java.security.Principal;

/**
 */
public interface RedstarPrincipal extends Principal, Serializable {
    String getOpenId();
}
