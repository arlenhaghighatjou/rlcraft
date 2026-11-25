package mezz.jei.collect;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;

public class MultiMap<K, V, T extends Collection<V>> {
   protected final Map<K, T> map;
   private final Function<K, T> collectionMappingFunction;

   public MultiMap(Supplier<T> collectionSupplier) {
      this(new HashMap(), collectionSupplier);
   }

   public MultiMap(Map<K, T> map, Supplier<T> collectionSupplier) {
      this.map = map;
      this.collectionMappingFunction = (k) -> {
         return (void)((Collection)collectionSupplier.get());
      };
   }

   public T get(K key) {
      return (T)(this.map.computeIfAbsent(key, this.collectionMappingFunction));
   }

   public boolean put(K key, V value) {
      return this.get(key).add(value);
   }

   public boolean remove(K key, V value) {
      T collection = this.map.get(key);
      return collection != null && collection.remove(value);
   }

   public boolean containsKey(K key) {
      return this.map.containsKey(key);
   }

   public boolean contains(K key, V value) {
      T collection = this.map.get(key);
      return collection != null && collection.contains(value);
   }

   public Set<Entry<K, T>> entrySet() {
      return this.map.entrySet();
   }

   public int getTotalSize() {
      int size = 0;

      for(T value : this.map.values()) {
         size += value.size();
      }

      return size;
   }

   public ImmutableMultimap<K, V> toImmutable() {
      Builder<K, V> builder = ImmutableMultimap.builder();

      for(Entry<K, T> entry : this.map.entrySet()) {
         K key = (K)entry.getKey();

         for(V value : (Collection)entry.getValue()) {
            builder.put(key, value);
         }
      }

      return builder.build();
   }
}
