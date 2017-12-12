/*
 */
package com.fangjian.xj.session.core;

import com.fangjian.xj.session.SessionGroup;

import java.util.HashMap;
import java.util.Map;

/**
 */
public abstract class AbstractSessionGroupMappingResolver implements SessionGroupResolver {
    protected Map<String, SessionGroup> sessionGroupMapping = new HashMap<>();

    public void setSessionGroupMapping(Map<String, String> sessionGroupMap) {
        for (String key : sessionGroupMap.keySet()) {
            String sessionGroupName = sessionGroupMap.get(key);
            SessionGroup sessionGroup = SessionGroup.valueOf(sessionGroupName);
            sessionGroupMapping.put(key, sessionGroup);
        }
    }

    public void register(String key, SessionGroup sessionGroup) {
        if (sessionGroup == null) {
            throw new IllegalArgumentException("sessionGroup cannot be null");
        }
        sessionGroupMapping.put(key, sessionGroup);
    }
}
