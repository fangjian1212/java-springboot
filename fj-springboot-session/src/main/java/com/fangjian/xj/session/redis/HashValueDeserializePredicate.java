/*
 */

package com.fangjian.xj.session.redis;

public interface HashValueDeserializePredicate {
    boolean isDelayed(Object key, byte[] value);
}
