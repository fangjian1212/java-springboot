/*
 */

package com.fangjian.share.session.redis;

/**
 */
public interface SerializableValue {
    byte[] getRaw();

    Object deserialize();
}
