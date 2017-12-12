/*
 */

package com.fangjian.share.session.redis;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

/**
 */
public class HashValueDeserializeDelayedRedisTemplate<K, V> extends RedisTemplate<K, V> {
    private final HashValueDeserializePredicate hashValueDeserializePredicate;

    public HashValueDeserializeDelayedRedisTemplate(HashValueDeserializePredicate hashValueDeserializePredicate) {
        this.hashValueDeserializePredicate = hashValueDeserializePredicate;
    }

    @Override
    public <HK, HV> HashOperations<K, HK, HV> opsForHash() {
        HashOperations<K, HK, HV> ops = super.opsForHash();
        return new ValueDeserializeDelayedHashOperations<K, HK, HV>(ops, this, this.hashValueDeserializePredicate);
    }
}
