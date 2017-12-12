/*
 */
package com.fangjian.xj.session.core;

import com.fangjian.xj.session.SessionGroup;

import java.util.List;

/**
 */
//@ConfigurationProperties(prefix = "session",locations = "classpath:session.properties")
public class SessionProperties {
    private SessionGroup sessionGroup;
    private int maxInactiveIntervalInSeconds = 1800;
    private boolean allowCreateSession = true;
    private boolean allowManualTimeout = false;
    private String principalSessionKey;
    private Redis redis = new Redis();

    public SessionGroup getSessionGroup() {
        return sessionGroup;
    }

    public void setSessionGroup(SessionGroup sessionGroup) {
        this.sessionGroup = sessionGroup;
    }

    public int getMaxInactiveIntervalInSeconds() {
        return maxInactiveIntervalInSeconds;
    }

    public void setMaxInactiveIntervalInSeconds(int maxInactiveIntervalInSeconds) {
        this.maxInactiveIntervalInSeconds = maxInactiveIntervalInSeconds;
    }

    public boolean isAllowCreateSession() {
        return allowCreateSession;
    }

    public void setAllowCreateSession(boolean allowCreateSession) {
        this.allowCreateSession = allowCreateSession;
    }

    public boolean isAllowManualTimeout() {
        return allowManualTimeout;
    }

    public void setAllowManualTimeout(boolean allowManualTimeout) {
        this.allowManualTimeout = allowManualTimeout;
    }

    public Redis getRedis() {
        return redis;
    }

    public void setRedis(Redis redis) {
        this.redis = redis;
    }

    public String getPrincipalSessionKey() {
        return principalSessionKey;
    }

    public void setPrincipalSessionKey(String principalSessionKey) {
        this.principalSessionKey = principalSessionKey;
    }

    public static class Redis {
        private Boolean userPool;
        private Integer database;
        private String master = "mymaster";
        private List<String> nodes;

        public Boolean getUserPool() {
            return userPool;
        }

        public void setUserPool(Boolean userPool) {
            this.userPool = userPool;
        }

        public Integer getDatabase() {
            return database;
        }

        public void setDatabase(Integer database) {
            this.database = database;
        }

        public List<String> getNodes() {
            return nodes;
        }

        public void setNodes(List<String> nodes) {
            this.nodes = nodes;
        }

        public String getMaster() {
            return master;
        }

        public void setMaster(String master) {
            this.master = master;
        }
    }
}
