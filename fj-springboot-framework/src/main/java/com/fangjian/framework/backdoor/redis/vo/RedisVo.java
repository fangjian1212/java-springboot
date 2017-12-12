package com.fangjian.framework.backdoor.redis.vo;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Created by fangjian on 2016/11/3.
 */
public class RedisVo implements Serializable{

    private static final long serialVersionUID = 3374934985319237842L;

    private String keyPattern;

    private Set<String> keys;

    private String message;

    private boolean success = false;

    private Map data;//缓存数据，k-v

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getKeyPattern() {
        return keyPattern;
    }

    public void setKeyPattern(String keyPattern) {
        this.keyPattern = keyPattern;
    }

    public Set<String> getKeys() {
        return keys;
    }

    public void setKeys(Set<String> keys) {
        this.keys = keys;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
