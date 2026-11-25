package com.creativemd.creativecore.common.utils.type;

import java.util.Map.Entry;

public class Pair<K, V> implements Entry<K, V> {
   public final K key;
   public V value;

   public Pair(K key, V value) {
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
      this.value = value;
      return value;
   }

   public int hashCode() {
      return this.key.hashCode();
   }

   public boolean is(K key) {
      if (this.key != null) {
         return this.key.equals(key);
      } else {
         return this.key == key;
      }
   }

   public boolean equals(Object obj) {
      return obj instanceof Pair ? this.key.equals(((Pair)obj).key) : false;
   }

   public String toString() {
      return "[" + this.key + "=" + this.value + "]";
   }
}
