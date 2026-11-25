package com.fantasticsource.tools.datastructures;

public class Pair<K, V> {
   private K key;
   private V value;

   public Pair(K key, V value) {
      this.set(key, value);
   }

   public K getKey() {
      return this.key;
   }

   public Pair<K, V> setKey(K key) {
      this.key = key;
      return this;
   }

   public V getValue() {
      return this.value;
   }

   public Pair<K, V> setValue(V value) {
      this.value = value;
      return this;
   }

   public Pair<K, V> set(K key, V value) {
      this.setKey(key);
      this.setValue(value);
      return this;
   }

   public boolean equals(Object other) {
      if (!(other instanceof Pair)) {
         return false;
      } else {
         Pair otherPair = (Pair)other;
         return this.equal(this.key, otherPair.key) && this.equal(this.value, otherPair.value);
      }
   }

   private boolean equal(Object obj1, Object obj2) {
      return obj1 == null ? obj2 == null : obj1.equals(obj2);
   }

   public int hashCode() {
      if (this.key == null && this.value == null) {
         return 0;
      } else if (this.value == null) {
         return this.key.hashCode();
      } else {
         return this.key == null ? -this.value.hashCode() : this.key.hashCode() - this.value.hashCode();
      }
   }
}
