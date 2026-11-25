package stanhebben.zenscript.util;

import java.util.Objects;

public class Pair<K, V> {
   private final K key;
   private final V value;

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

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Pair<?, ?> pair = (Pair)o;
         return Objects.equals(this.key, pair.key) && Objects.equals(this.value, pair.value);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.key, this.value});
   }
}
