package rtg.api.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Spliterators;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

public final class LimitedArrayCacheMap<K, V> implements Map<K, V>, Serializable {
   private static final long serialVersionUID = 7161452652266833375L;
   private final int capacity;
   private final LimitedArrayCacheMap.ImmutableEntry<K, V>[] entries;
   private int index = 0;

   public LimitedArrayCacheMap(int capacity) {
      this.capacity = capacity;
      this.entries = new LimitedArrayCacheMap.ImmutableEntry[capacity];
   }

   public int getCapacity() {
      return this.capacity;
   }

   public int size() {
      int count = 0;

      for(LimitedArrayCacheMap.ImmutableEntry<K, V> entry : this.entries) {
         if (entry != null) {
            ++count;
         }
      }

      return count;
   }

   public boolean isEmpty() {
      for(LimitedArrayCacheMap.ImmutableEntry<K, V> entry : this.entries) {
         if (entry != null) {
            return false;
         }
      }

      return true;
   }

   public boolean containsKey(Object key) {
      for(LimitedArrayCacheMap.ImmutableEntry<K, V> entry : this.entries) {
         if (entry != null && entry.getKey().equals(key)) {
            return true;
         }
      }

      return false;
   }

   public boolean containsValue(Object value) {
      for(LimitedArrayCacheMap.ImmutableEntry<K, V> entry : this.entries) {
         if (entry != null && entry.getValue().equals(value)) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public V get(Object key) {
      for(LimitedArrayCacheMap.ImmutableEntry<K, V> entry : this.entries) {
         if (entry != null && entry.getKey().equals(key)) {
            return entry.getValue();
         }
      }

      return (V)null;
   }

   public V getOrDefault(Object key, V defaultValue) {
      V ret;
      return (V)((ret = (V)this.get(key)) != null ? ret : defaultValue);
   }

   @Nullable
   public V put(K key, V value) {
      V ret = null;
      if (this.entries[this.index] != null) {
         ret = (V)this.entries[this.index].getValue();
      }

      this.entries[this.index] = new LimitedArrayCacheMap.ImmutableEntry(key, value);
      this.removeDuplicates();
      this.index = this.index == this.capacity - 1 ? 0 : this.index + 1;
      return ret;
   }

   public V remove(Object key) {
      throw new UnsupportedOperationException("Immutable data");
   }

   public void putAll(@Nonnull Map<? extends K, ? extends V> m) {
      m.forEach(this::put);
   }

   public void clear() {
      this.index = 0;

      for(int i = 0; i < this.capacity; ++i) {
         this.entries[i] = null;
      }

   }

   @Nonnull
   public Set<K> keySet() {
      LinkedHashSet<K> keySet = new LinkedHashSet();

      for(LimitedArrayCacheMap.ImmutableEntry<K, V> entry : this.entries) {
         if (entry != null) {
            keySet.add(entry.getKey());
         }
      }

      return keySet;
   }

   @Nonnull
   public Collection<V> values() {
      LinkedHashSet<V> valueSet = new LinkedHashSet();

      for(LimitedArrayCacheMap.ImmutableEntry<K, V> entry : this.entries) {
         if (entry != null) {
            valueSet.add(entry.getValue());
         }
      }

      return valueSet;
   }

   @Nonnull
   public Set<Entry<K, V>> entrySet() {
      LinkedHashSet<Entry<K, V>> entrySet = new LinkedHashSet();

      for(LimitedArrayCacheMap.ImmutableEntry<K, V> entry : this.entries) {
         if (entry != null) {
            entrySet.add(entry);
         }
      }

      return entrySet;
   }

   public LimitedArrayCacheMap<K, V> clone() {
      LimitedArrayCacheMap<K, V> clone = new LimitedArrayCacheMap<K, V>(this.capacity);
      System.arraycopy(this.entries, 0, clone.entries, 0, this.capacity);
      clone.index = this.index;
      return clone;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof LimitedArrayCacheMap)) {
         return false;
      } else {
         LimitedArrayCacheMap<?, ?> other = (LimitedArrayCacheMap)o;
         if (this.getCapacity() != other.getCapacity()) {
            return false;
         } else {
            for(int i = 0; i < this.getCapacity(); ++i) {
               if (this.entries[i] == null && other.entries[i] != null || this.entries[i] != null && !this.entries[i].equals(other.entries[i])) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int hashCode() {
      int ret = 0;

      for(Entry<K, V> entry : this.entries) {
         if (entry != null) {
            ret += entry.hashCode();
         }
      }

      return ret;
   }

   public String toString() {
      StringBuilder s = new StringBuilder();
      s.append("LimitedArrayCacheMap[");
      boolean first = true;

      for(Entry<K, V> e : this.entrySet()) {
         if (first) {
            s.append("{");
         } else {
            s.append(",{");
         }

         s.append(e.toString());
         s.append("}");
         first = false;
      }

      s.append("]");
      return s.toString();
   }

   public Stream<Entry<K, V>> stream() {
      return StreamSupport.stream(Spliterators.spliterator(this.entrySet(), 0), false);
   }

   private void removeDuplicates() {
      for(int i = 0; i < this.capacity; ++i) {
         if (i != this.index && this.entries[i] != null && this.entries[i].equals(this.entries[this.index])) {
            this.entries[i] = null;
         }
      }

   }

   @Immutable
   private static final class ImmutableEntry<K, V> implements Entry<K, V>, Serializable {
      private static final long serialVersionUID = -4241056911694303514L;
      private final K key;
      private final V value;

      private ImmutableEntry(K key, V value) {
         this.key = key;
         this.value = value;
      }

      public K getKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      public V setValue(V v) {
         throw new UnsupportedOperationException("Immutable data");
      }

      public boolean equals(@Nullable Object o) {
         return o != null && o instanceof Entry && this.getKey().equals(((Entry)o).getKey());
      }

      public int hashCode() {
         return this.getKey().hashCode();
      }

      public String toString() {
         return this.getKey() + "=" + this.getValue();
      }
   }
}
