/*
 */

package com.fangjian.share.session.redis;

import org.springframework.data.redis.serializer.RedisSerializer;

/**
 */
public class RedisSerializableValue implements SerializableValue {
    private final byte[] value;
    private final RedisSerializer serializer;

    public RedisSerializableValue(byte[] value, RedisSerializer serializer) {
        this.value = value;
        this.serializer = serializer;
    }

    @Override
    public byte[] getRaw() {
        return value;
    }

    @Override
    public Object deserialize() {
        if (value == null) {
            return null;
        }
        return serializer.deserialize(value);
    }
}
