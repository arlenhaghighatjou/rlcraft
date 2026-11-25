package slimeknights.mantle.configurate.util;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class MapFactories {
   private MapFactories() {
   }

   public static MapFactory unordered() {
      return new MapFactories.EqualsSupplier() {
         public <K, V> ConcurrentMap<K, V> create() {
            return new ConcurrentHashMap();
         }
      };
   }

   public static MapFactory sorted(final Comparator<Object> comparator) {
      return new MapFactories.EqualsSupplier() {
         public <K, V> ConcurrentMap<K, V> create() {
            return new ConcurrentSkipListMap(comparator);
         }
      };
   }

   public static MapFactory sortedNatural() {
      return new MapFactories.EqualsSupplier() {
         public <K, V> ConcurrentMap<K, V> create() {
            return new ConcurrentSkipListMap();
         }
      };
   }

   public static MapFactory insertionOrdered() {
      return new MapFactories.EqualsSupplier() {
         public <K, V> ConcurrentMap<K, V> create() {
            return new MapFactories.SynchronizedWrapper<K, V>(new LinkedHashMap());
         }
      };
   }

   private abstract static class EqualsSupplier implements MapFactory {
      private EqualsSupplier() {
      }

      public boolean equals(Object o) {
         return o.getClass().equals(this.getClass());
      }
   }

   private static class SynchronizedWrapper<K, V> implements ConcurrentMap<K, V> {
      private final Map<K, V> wrapped;

      private SynchronizedWrapper(Map<K, V> wrapped) {
         this.wrapped = wrapped;
      }

      public V putIfAbsent(K k, V v) {
         synchronized(this.wrapped) {
            if (!this.wrapped.containsKey(k)) {
               this.wrapped.put(k, v);
               return (V)null;
            } else {
               return (V)this.wrapped.get(k);
            }
         }
      }

      public boolean remove(Object key, Object expected) {
         synchronized(this.wrapped) {
            if (Objects.equal(expected, this.wrapped.get(key))) {
               return this.wrapped.remove(key) != null;
            } else {
               return false;
            }
         }
      }

      public boolean replace(K key, V old, V replace) {
         synchronized(this.wrapped) {
            if (Objects.equal(old, this.wrapped.get(key))) {
               this.wrapped.put(key, replace);
               return true;
            } else {
               return false;
            }
         }
      }

      public V replace(K k, V v) {
         synchronized(this.wrapped) {
            return (V)(this.wrapped.containsKey(k) ? this.wrapped.put(k, v) : null);
         }
      }

      public int size() {
         synchronized(this.wrapped) {
            return this.wrapped.size();
         }
      }

      public boolean isEmpty() {
         synchronized(this.wrapped) {
            return this.wrapped.isEmpty();
         }
      }

      public boolean containsKey(Object o) {
         synchronized(this.wrapped) {
            return this.wrapped.containsKey(o);
         }
      }

      public boolean containsValue(Object o) {
         synchronized(this.wrapped) {
            return this.wrapped.containsKey(o);
         }
      }

      public V get(Object o) {
         synchronized(this.wrapped) {
            return (V)this.wrapped.get(o);
         }
      }

      public V put(K k, V v) {
         synchronized(this.wrapped) {
            return (V)this.wrapped.put(k, v);
         }
      }

      public V remove(Object o) {
         synchronized(this.wrapped) {
            return (V)this.wrapped.remove(o);
         }
      }

      public void putAll(Map<? extends K, ? extends V> map) {
         synchronized(this.wrapped) {
            this.wrapped.putAll(map);
         }
      }

      public void clear() {
         synchronized(this.wrapped) {
            this.wrapped.clear();
         }
      }

      public Set<K> keySet() {
         synchronized(this.wrapped) {
            return ImmutableSet.copyOf(this.wrapped.keySet());
         }
      }

      public Collection<V> values() {
         synchronized(this.wrapped) {
            return ImmutableSet.copyOf(this.wrapped.values());
         }
      }

      public Set<Entry<K, V>> entrySet() {
         synchronized(this.wrapped) {
            return ImmutableSet.copyOf(this.wrapped.entrySet());
         }
      }
   }
}
