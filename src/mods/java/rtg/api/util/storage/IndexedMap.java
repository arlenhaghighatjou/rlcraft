package rtg.api.util.storage;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IndexedMap<K, V, I extends Comparable<I>> extends Map<K, V>, Serializable {
   boolean isStrict();

   boolean isMutable();

   boolean areEntriesMutable();

   Function<K, I> getIndexer();

   I getIndex(@Nonnull K var1);

   @Nullable
   V putAt(@Nonnull K var1, @Nonnull V var2, @Nullable I var3);

   @Nullable
   K getKeyAt(@Nonnull I var1);

   @Nullable
   V getValueAt(@Nonnull I var1);

   @Nullable
   V remove(@Nonnull I var1);

   public interface IndexMapEntry<K, V, I extends Comparable<I>> extends Entry<K, V>, Comparable<IndexedMap.IndexMapEntry<K, V, I>>, Serializable {
      I getIndex();

      default int compareTo(@Nonnull IndexedMap.IndexMapEntry<K, V, I> other) {
         return this.getIndex().compareTo(other.getIndex());
      }

      public abstract static class IndexEntryBase<K, V, I extends Comparable<I>> implements IndexedMap.IndexMapEntry<K, V, I> {
         private static final long serialVersionUID = -5154097056428766455L;
         private final K key;
         private final I index;
         private V value;

         IndexEntryBase(@Nonnull K key, @Nonnull V value, @Nonnull I index) {
            this.key = key;
            this.value = value;
            this.index = index;
         }

         public K getKey() {
            return this.key;
         }

         public V getValue() {
            return this.value;
         }

         public I getIndex() {
            return this.index;
         }

         V putValue(@Nonnull V newValue) {
            V ret = (V)this.getValue();
            this.value = newValue;
            return ret;
         }

         public boolean equals(Object o) {
            if (this == o) {
               return true;
            } else if (!(o instanceof IndexedMap.IndexMapEntry.IndexEntryBase)) {
               return false;
            } else {
               IndexedMap.IndexMapEntry.IndexEntryBase<?, ?, ?> other = (IndexedMap.IndexMapEntry.IndexEntryBase)o;
               return Objects.equals(this.getKey(), other.getKey()) && Objects.equals(this.getValue(), other.getValue()) && Objects.equals(this.getIndex(), other.getIndex());
            }
         }

         public int hashCode() {
            return this.getIndex().hashCode() ^ this.getKey().hashCode() ^ this.getValue().hashCode();
         }

         public String toString() {
            return this.getClass().getSimpleName() + "[" + this.getIndex() + "[" + this.getKey() + "=" + this.getValue() + "]]";
         }

         public static class SimpleImmutableIndexEntry<K, V, I extends Comparable<I>> extends IndexedMap.IndexMapEntry.IndexEntryBase<K, V, I> {
            private static final long serialVersionUID = 2533506876382702762L;

            public SimpleImmutableIndexEntry(@Nonnull K key, @Nonnull V value, @Nonnull I index) {
               super(key, value, index);
            }

            public SimpleImmutableIndexEntry(@Nonnull K key, @Nonnull V value, @Nonnull Function<K, I> indexer) {
               super(key, value, (Comparable)indexer.apply(key));
            }

            public V setValue(@Nonnull V newValue) {
               throw new UnsupportedOperationException("Immutable data");
            }
         }

         public static class SimpleMutableIndexEntry<K, V, I extends Comparable<I>> extends IndexedMap.IndexMapEntry.IndexEntryBase<K, V, I> {
            private static final long serialVersionUID = -2225795238508933466L;

            public SimpleMutableIndexEntry(@Nonnull K key, @Nonnull V value, @Nonnull I index) {
               super(key, value, index);
            }

            public SimpleMutableIndexEntry(@Nonnull K key, @Nonnull V value, @Nonnull Function<K, I> indexer) {
               super(key, value, (Comparable)indexer.apply(key));
            }

            public V setValue(@Nonnull V newValue) {
               return (V)this.putValue(newValue);
            }
         }
      }
   }

   public interface LimitedIndexedMap<K, V, I extends Comparable<I>> extends IndexedMap<K, V, I> {
      int getCapacity();

      default boolean checkBounds(int index) {
         if (index >= 0 && index < this.getCapacity()) {
            return false;
         } else if (this.isStrict()) {
            throw new IndexOutOfBoundsException("Index is out of bounds for map entry: " + index);
         } else {
            return true;
         }
      }

      public interface ByteLIMap<K, V> extends IndexedMap.LimitedIndexedMap<K, V, Byte> {
      }

      public interface IntegerLIMap<K, V> extends IndexedMap.LimitedIndexedMap<K, V, Integer> {
      }

      public interface LongLIMap<K, V> extends IndexedMap.LimitedIndexedMap<K, V, Long> {
      }

      public interface ShortLIMap<K, V> extends IndexedMap.LimitedIndexedMap<K, V, Short> {
      }
   }
}
