/*
 */

package com.fangjian.xj.session.redis;

/**
 */
public interface SerializableValue {
    byte[] getRaw();

    Object deserialize();
}
