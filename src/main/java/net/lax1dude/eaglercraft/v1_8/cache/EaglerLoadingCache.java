/*
 * Copyright (c) 2022-2023 lax1dude, ayunami2000. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.v1_8.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;

public class EaglerLoadingCache<K, V> {

	private final EaglerCacheProvider<K, V> provider;
	private final Map<K, V> cacheMap;
	private final Map<K, Long> writeTimes;
	private final Map<K, Long> accessTimes;
	private final int maximumSize;
	private final long expireAfterWrite;
	private final long expireAfterAccess;

	public EaglerLoadingCache(EaglerCacheProvider<K, V> provider) {
		this(provider, Integer.MAX_VALUE, 0L);
	}

	public EaglerLoadingCache(EaglerCacheProvider<K, V> provider, int maximumSize, long expireAfterWrite) {
		this(provider, maximumSize, expireAfterWrite, 0L);
	}

	public EaglerLoadingCache(EaglerCacheProvider<K, V> provider, int maximumSize, long expireAfterWrite,
			long expireAfterAccess) {
		this.provider = provider;
		this.maximumSize = maximumSize;
		this.expireAfterWrite = expireAfterWrite;
		this.cacheMap = new LinkedHashMap<K, V>(16, 0.75F, true);
		this.writeTimes = expireAfterWrite > 0L ? new LinkedHashMap<K, Long>() : null;
		this.expireAfterAccess = expireAfterAccess;
		this.accessTimes = expireAfterAccess > 0L ? new LinkedHashMap<K, Long>() : null;
	}

	public V get(K key) {
		V etr = getIfPresent(key);
		if(etr == null) {
			etr = provider.create(key);
			put(key, etr);
		}
		return etr;
	}

	public V getIfPresent(K key) {
		V value = cacheMap.get(key);
		if (value != null && (writeTimes != null || accessTimes != null)) {
			long now = EagRuntime.steadyTimeMillis();
			if (isExpired(key, now)) {
				invalidate(key);
				return null;
			}
			if (accessTimes != null) {
				accessTimes.put(key, now);
			}
		}
		return value;
	}

	private boolean isExpired(K key, long now) {
		return writeTimes != null && now - writeTimes.get(key) >= expireAfterWrite
				|| accessTimes != null && now - accessTimes.get(key) >= expireAfterAccess;
	}

	public V getUnchecked(K key) {
		return get(key);
	}

	public void put(K key, V value) {
		cacheMap.put(key, value);
		if(writeTimes != null) {
			writeTimes.put(key, Long.valueOf(EagRuntime.steadyTimeMillis()));
		}
		if (accessTimes != null) {
			accessTimes.put(key, EagRuntime.steadyTimeMillis());
		}
		if(cacheMap.size() > maximumSize) {
			Iterator<K> itr = cacheMap.keySet().iterator();
			if(itr.hasNext()) {
				K remove = itr.next();
				itr.remove();
				if(writeTimes != null) {
					writeTimes.remove(remove);
				}
				if (accessTimes != null) {
					accessTimes.remove(remove);
				}
			}
		}
	}

	public void invalidate(K key) {
		cacheMap.remove(key);
		if(writeTimes != null) {
			writeTimes.remove(key);
		}
		if (accessTimes != null) {
			accessTimes.remove(key);
		}
	}

	public void invalidateAll() {
		cacheMap.clear();
		if(writeTimes != null) {
			writeTimes.clear();
		}
		if (accessTimes != null) {
			accessTimes.clear();
		}
	}

	public void cleanUp() {
		if (writeTimes != null || accessTimes != null) {
			long now = EagRuntime.steadyTimeMillis();
			Iterator<K> itr = cacheMap.keySet().iterator();
			while (itr.hasNext()) {
				K key = itr.next();
				if (isExpired(key, now)) {
					itr.remove();
					if (writeTimes != null) {
						writeTimes.remove(key);
					}
					if (accessTimes != null) {
						accessTimes.remove(key);
					}
				}
			}
		}
	}

}
