/*
 */
package com.fangjian.xj.session.core;

import com.fangjian.xj.session.SessionGroup;

import javax.servlet.http.HttpServletRequest;

/**
 * 根据HostName来判断SessionGroup
 */
public class HostSessionGroupResolver extends AbstractSessionGroupMappingResolver{
    @Override
    public SessionGroup resolve(HttpServletRequest request) {
        String serverName = request.getServerName();
        return sessionGroupMapping.get(serverName);
    }

}
