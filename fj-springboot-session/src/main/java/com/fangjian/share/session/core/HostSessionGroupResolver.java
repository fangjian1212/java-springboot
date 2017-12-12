/*
 */
package com.fangjian.share.session.core;

import com.fangjian.share.session.SessionGroup;

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
