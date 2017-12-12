/*
 */

package com.fangjian.share.session.redis;

public interface HashValueDeserializePredicate {
    boolean isDelayed(Object key, byte[] value);
}
