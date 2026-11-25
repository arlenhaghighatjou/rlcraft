package nonapi.io.github.classgraph.json;

class ReferenceEqualityKey<K> {
   private final K wrappedKey;

   public ReferenceEqualityKey(K wrappedKey) {
      this.wrappedKey = wrappedKey;
   }

   public int hashCode() {
      K key = this.wrappedKey;
      return key == null ? 0 : key.hashCode();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ReferenceEqualityKey)) {
         return false;
      } else {
         return this.wrappedKey == ((ReferenceEqualityKey)obj).wrappedKey;
      }
   }

   public String toString() {
      K key = this.wrappedKey;
      return key == null ? "null" : key.toString();
   }
}
