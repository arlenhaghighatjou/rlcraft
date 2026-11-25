package com.alcatrazescapee.alcatrazcore.util.collections;

import java.util.Map.Entry;

public class ImmutablePair<K, V> implements Entry<K, V> {
   private final K key;
   private final V value;

   public static <K, V> ImmutablePair<K, V> of(K key, V value) {
      return new ImmutablePair<K, V>(key, value);
   }

   private ImmutablePair(K key, V value) {
      this.key = key;
      this.value = value;
   }

   public K getKey() {
      return this.key;
   }

   public V getValue() {
      return this.value;
   }

   public V setValue(V value) {
      throw new UnsupportedOperationException("Tried to set value in ImmutablePair");
   }

   public int hashCode() {
      return this.value.hashCode() + 65536 * this.key.hashCode();
   }

   public boolean equals(Object obj) {
      if (!(obj instanceof ImmutablePair)) {
         return false;
      } else {
         ImmutablePair other = (ImmutablePair)obj;
         return this.key.equals(other.key) && this.value.equals(other.value);
      }
   }
}
