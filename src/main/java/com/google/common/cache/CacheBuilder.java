package com.google.common.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.lax1dude.eaglercraft.v1_8.cache.EaglerCacheProvider;
import net.lax1dude.eaglercraft.v1_8.cache.EaglerLoadingCache;

public final class CacheBuilder
{
    private int maximumSize = Integer.MAX_VALUE;
    private long expireAfterWrite = 0L;
    private long expireAfterAccess = 0L;

    private CacheBuilder()
    {
    }

    public static CacheBuilder newBuilder()
    {
        return new CacheBuilder();
    }

    public CacheBuilder maximumSize(long size)
    {
        this.maximumSize = size >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)size;
        return this;
    }

    public CacheBuilder expireAfterWrite(long duration, TimeUnit unit)
    {
        this.expireAfterWrite = unit.toMillis(duration);
        return this;
    }

	public CacheBuilder expireAfterAccess(long duration, TimeUnit unit)
	{
		this.expireAfterAccess = unit.toMillis(duration);
		return this;
	}

    public <K, V> EaglerLoadingCache<K, V> build(final CacheLoader<K, V> loader)
    {
        return new EaglerLoadingCache<K, V>(new EaglerCacheProvider<K, V>()
        {
            public V create(K key)
            {
                try
                {
                    return loader.load(key);
                }
                catch (Exception ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        }, maximumSize, expireAfterWrite, expireAfterAccess);
    }

    public <K, V> Cache<K, V> build()
    {
        return new LocalCache<K, V>(maximumSize, expireAfterWrite, expireAfterAccess);
    }

    private static class LocalCache<K, V> implements Cache<K, V>
    {
		private final EaglerLoadingCache<K, V> cache;

        private LocalCache(int maximumSize, long expireAfterWrite, long expireAfterAccess)
        {
			this.cache = new EaglerLoadingCache<>(key -> null, maximumSize, expireAfterWrite, expireAfterAccess);
        }

        public V getIfPresent(K key)
        {
            return this.cache.getIfPresent(key);
        }

        public void put(K key, V value)
        {
            this.cache.put(key, value);
        }

        public void invalidate(K key)
        {
            this.cache.invalidate(key);
        }

        public void invalidateAll()
        {
            this.cache.invalidateAll();
        }
    }
}
