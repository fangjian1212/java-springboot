/*
 */

package com.fangjian.xj.session.redis;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationUtils;

import java.util.*;

/**
 * Redis Hash的一种Map实现
 * <p>
 * 只有在get(Object key)时才会反序列化redis hash的值
 * </p>
 */
class ValueDelayedDeserializeMap<K, V> implements Map<K, V> {
    private final RedisSerializer hashValueSerializer;
    private final RedisSerializer hashKeySerializer;
    private Map<K, byte[]> serialized = new HashMap<K, byte[]>();
    private Map<K, V> cached = new HashMap<>();

    public ValueDelayedDeserializeMap(Map<byte[], byte[]> serializeMap, RedisSerializer hashKeySerializer, RedisSerializer hashValueSerializer) {
        this.hashKeySerializer = hashKeySerializer;
        this.hashValueSerializer = hashValueSerializer;
        if (serializeMap != null) {
            for (Entry<byte[], byte[]> entry : serializeMap.entrySet()) {
                addEntry(entry);
            }
        }
    }

    protected void addEntry(Entry<byte[], byte[]> entry) {
        serialized.put((K) hashKeySerializer.deserialize(entry.getKey()), entry.getValue());
    }

    @Override
    public int size() {
        return serialized.size();
    }

    @Override
    public boolean isEmpty() {
        return serialized.isEmpty();
    }

    @Override
    public V get(Object key) {
        V value = cached.get(key);
        if (value == null) {
            value = (V) hashValueSerializer.deserialize(serialized.get(key));
            cached.put((K) key, value);
        }
        return value;
    }

    @Override
    public boolean containsKey(Object key) {
        return serialized.containsKey(key);
    }

    public V put(K key, V value) {
        V old = cached.put(key, value);
        byte[] bValue = serialized.put(key, hashValueSerializer.serialize(value));
        if (old != null) {
            return old;
        } else {
            return (V) hashValueSerializer.deserialize(bValue);
        }
    }

    @Override
    public V remove(Object key) {
        V old = cached.remove(key);
        byte[] bValue = serialized.remove(key);
        if (old != null) {
            return old;
        } else {
            return (V) hashValueSerializer.deserialize(bValue);
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        serialized.clear();
        cached.clear();
    }

    @Override
    public boolean containsValue(Object value) {
        return values().contains(value);
    }

    @Override
    public Set<K> keySet() {
        return serialized.keySet();
    }

    @Override
    public Collection<V> values() {
        return SerializationUtils.deserialize(serialized.values(), hashValueSerializer);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    private class EntrySet extends AbstractSet<Entry<K, V>> {

        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public int size() {
            return ValueDelayedDeserializeMap.this.size();
        }
    }

    private class EntryIterator implements Iterator<Entry<K, V>> {
        private Iterator<Entry<K, byte[]>> iter;

        public EntryIterator() {
            this.iter = serialized.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public Entry<K, V> next() {
            final Entry<K, byte[]> next = iter.next();
            return new Entry<K, V>() {
                @Override
                public K getKey() {
                    return next.getKey();
                }

                @Override
                public V getValue() {
                    return get(getKey());
                }

                @Override
                public V setValue(V value) {
                    return put(getKey(), value);
                }
            };
        }

        @Override
        public void remove() {
            iter.remove();
            cached.clear();
        }
    }
}
