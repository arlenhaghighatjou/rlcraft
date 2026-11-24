package com.google.common.cache;

public interface Cache<K, V>
{
    V getIfPresent(K key);

    void put(K key, V value);

    void invalidate(K key);

    void invalidateAll();
}
