/*
 */

package com.fangjian.xj.session.redis;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Redis Hash 操作类
 * <p>
 * <p>
 * 主要提供entries方法的延迟反序列化
 */
class ValueDeserializeDelayedHashOperations<K, HK, HV> implements HashOperations<K, HK, HV> {

    private final HashOperations<K, HK, HV> ops;
    private final RedisTemplate<K, Object> template;
    private final HashValueDeserializePredicate hashValueDeserializePredicate;

    public ValueDeserializeDelayedHashOperations(HashOperations<K, HK, HV> ops, RedisTemplate<K, ?> template, HashValueDeserializePredicate hashValueDeserializePredicate) {
        this.ops = ops;
        this.template = (RedisTemplate<K, Object>) template;
        this.hashValueDeserializePredicate = hashValueDeserializePredicate;
    }

    @Override
    public void delete(K key, Object... hashKeys) {
        ops.delete(key, hashKeys);
    }

    @Override
    public Boolean hasKey(K key, Object hashKey) {
        return ops.hasKey(key, hashKey);
    }

    @Override
    public HV get(K key, Object hashKey) {
        return ops.get(key, hashKey);
    }

    @Override
    public List<HV> multiGet(K key, Collection<HK> hashKeys) {
        return ops.multiGet(key, hashKeys);
    }

    @Override
    public Long increment(K key, HK hashKey, long delta) {
        return ops.increment(key, hashKey, delta);
    }

    @Override
    public Double increment(K key, HK hashKey, double delta) {
        return ops.increment(key, hashKey, delta);
    }

    @Override
    public Set<HK> keys(K key) {
        return ops.keys(key);
    }

    @Override
    public Long size(K key) {
        return ops.size(key);
    }

    @Override
    public void putAll(K key, Map<? extends HK, ? extends HV> m) {
        ops.putAll(key, m);
    }

    @Override
    public void put(K key, HK hashKey, HV value) {
        ops.put(key, hashKey, value);
    }

    @Override
    public Boolean putIfAbsent(K key, HK hashKey, HV value) {
        return ops.putIfAbsent(key, hashKey, value);
    }

    @Override
    public List<HV> values(K key) {
        return ops.values(key);
    }

    @Override
    public Map<HK, HV> entries(K key) {
        final byte[] rawKey = rawKey(key);

        Map<byte[], byte[]> entries = template.execute(new RedisCallback<Map<byte[], byte[]>>() {

            public Map<byte[], byte[]> doInRedis(RedisConnection connection) {
                return connection.hGetAll(rawKey);
            }
        }, true);

//		return new ValueDelayedDeserializeMap<HK, HV>(entries, template.getHashKeySerializer(), template.getHashValueSerializer());
        return deserializeHashMap(entries);
    }

    @Override
    public RedisOperations<K, ?> getOperations() {
        return ops.getOperations();
    }

    @Override
    public Cursor<Map.Entry<HK, HV>> scan(K key, ScanOptions options) {
        return ops.scan(key, options);
    }

    @SuppressWarnings("unchecked")
    byte[] rawKey(Object key) {
        Assert.notNull(key, "non null key required");
        if (template.getKeySerializer() == null && key instanceof byte[]) {
            return (byte[]) key;
        }
        return ((RedisSerializer) template.getKeySerializer()).serialize(key);
    }

    Map<HK, HV> deserializeHashMap(Map<byte[], byte[]> entries) {
        // connection in pipeline/multi mode
        if (entries == null) {
            return null;
        }

        Map<HK, HV> map = new LinkedHashMap<>(entries.size());

        for (Map.Entry<byte[], byte[]> entry : entries.entrySet()) {
            HK key = (HK) deserializeHashKey(entry.getKey());
            byte[] value = entry.getValue();
            if (hashValueDeserializePredicate.isDelayed(key, value)) {
                map.put(key, (HV) new RedisSerializableValue(value, template.getHashValueSerializer()));
            } else {
                map.put(key, (HV) deserializeHashValue(value));
            }
        }

        return map;
    }

    <HK> HK deserializeHashKey(byte[] value) {
        if (template.getHashKeySerializer() == null) {
            return (HK) value;
        }
        return (HK) template.getHashKeySerializer().deserialize(value);
    }

    <HV> HV deserializeHashValue(byte[] value) {
        if (template.getHashValueSerializer() == null) {
            return (HV) value;
        }
        return (HV) template.getHashValueSerializer().deserialize(value);
    }

}
