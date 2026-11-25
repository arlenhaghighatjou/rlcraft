package rtg.api.util.storage;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterators;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.world.biome.Biome;
import rtg.api.world.biome.IRealisticBiome;

public final class BiomeMap implements IndexedMap.LimitedIndexedMap.IntegerLIMap<Biome, IRealisticBiome> {
   private static final long serialVersionUID = -5276712268351774212L;
   private final transient IndexedMap.IndexMapEntry<Biome, IRealisticBiome, Integer>[] entries = new IndexedMap.IndexMapEntry[this.getCapacity()];
   private boolean locked = false;

   public BiomeMap() {
   }

   public void addBiomes(IRealisticBiome... rbiomes) {
      Arrays.stream(rbiomes).forEach((b) -> {
         this.put(b.baseBiome(), b);
      });
   }

   public int getCapacity() {
      return 256;
   }

   public boolean isStrict() {
      return false;
   }

   public boolean isMutable() {
      return true;
   }

   public boolean areEntriesMutable() {
      return false;
   }

   public Function<Biome, Integer> getIndexer() {
      return Biome::getIdForBiome;
   }

   public Integer getIndex(@Nonnull Biome key) {
      return (Integer)this.getIndexer().apply(key);
   }

   @Nullable
   public Biome getKeyAt(@Nonnull Integer index) {
      if (this.checkBounds(index.intValue())) {
         return null;
      } else {
         return this.entries[index.intValue()] != null ? (Biome)this.entries[index.intValue()].getKey() : null;
      }
   }

   @Nullable
   public IRealisticBiome getValueAt(@Nonnull Integer index) {
      if (this.checkBounds(index.intValue())) {
         return null;
      } else {
         return this.entries[index.intValue()] != null ? (IRealisticBiome)this.entries[index.intValue()].getValue() : null;
      }
   }

   @Nullable
   public IRealisticBiome putAt(@Nonnull Biome key, @Nonnull IRealisticBiome value, Integer index) {
      if (this.locked) {
         return null;
      } else if (this.checkBounds(index.intValue())) {
         return null;
      } else if (this.isMutable()) {
         Objects.requireNonNull(key, "Key cannot be null");
         Objects.requireNonNull(value, "Value cannot be null");
         return this.insert(this.getNewEntry(key, value), index.intValue());
      } else if (this.isStrict()) {
         throw new IllegalArgumentException("Immutable data");
      } else {
         return null;
      }
   }

   @Nullable
   public IRealisticBiome remove(@Nonnull Integer index) {
      if (this.locked) {
         return null;
      } else {
         IRealisticBiome oldValue = this.getValueAt(index);
         this.entries[index.intValue()] = null;
         return oldValue;
      }
   }

   @Nullable
   private IRealisticBiome insert(@Nonnull IndexedMap.IndexMapEntry<Biome, IRealisticBiome, Integer> newEntry, int index) {
      Entry<Biome, IRealisticBiome> oldValue = this.entries[index];
      this.entries[index] = newEntry;
      return oldValue != null ? (IRealisticBiome)oldValue.getValue() : null;
   }

   public int size() {
      return (int)Arrays.stream(this.entries).filter(Objects::nonNull).count();
   }

   public boolean isEmpty() {
      return Arrays.stream(this.entries).noneMatch(Objects::nonNull);
   }

   public boolean containsKey(@Nonnull Object key) {
      return Arrays.stream(this.entries).anyMatch((e) -> {
         return e != null && ((Biome)e.getKey()).equals(key);
      });
   }

   public boolean containsValue(@Nonnull Object value) {
      return Arrays.stream(this.entries).anyMatch((e) -> {
         return e != null && ((IRealisticBiome)e.getValue()).equals(value);
      });
   }

   @Nullable
   public IRealisticBiome get(@Nonnull Object key) {
      return (IRealisticBiome)Arrays.stream(this.entries).filter((e) -> {
         return Objects.nonNull(e) && ((Biome)e.getKey()).equals(key);
      }).findFirst().map(Entry::getValue).orElse((Object)null);
   }

   @Nullable
   public IRealisticBiome put(@Nonnull Biome key, @Nonnull IRealisticBiome value) {
      return this.locked ? null : this.putAt(key, value, (Integer)this.getIndexer().apply(key));
   }

   @Nullable
   public IRealisticBiome remove(@Nonnull Object key) {
      if (this.locked) {
         return null;
      } else {
         for(int i = 0; i < this.entries.length; ++i) {
            if (Objects.equals(this.getKeyAt(Integer.valueOf(i)), key)) {
               return this.remove(Integer.valueOf(i));
            }
         }

         return null;
      }
   }

   public void putAll(@Nonnull Map<? extends Biome, ? extends IRealisticBiome> m) {
      if (!this.locked) {
         m.forEach(this::put);
      }
   }

   public void clear() {
      if (!this.locked) {
         Arrays.fill(this.entries, (Object)null);
      }
   }

   @Nonnull
   public Set<Biome> keySet() {
      return (Set)Arrays.stream(this.entries).filter(Objects::nonNull).map(Entry::getKey).collect(Collectors.toCollection(LinkedHashSet::new));
   }

   @Nonnull
   public Collection<IRealisticBiome> values() {
      return (Collection)Arrays.stream(this.entries).filter(Objects::nonNull).map(Entry::getValue).collect(Collectors.toList());
   }

   @Nonnull
   public Set<Entry<Biome, IRealisticBiome>> entrySet() {
      return (Set)Arrays.stream(this.entries).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new));
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BiomeMap map = (BiomeMap)o;
         return this.getCapacity() == map.getCapacity() && Arrays.equals(this.entries, map.entries);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return 31 * this.getCapacity() + Arrays.hashCode(this.entries);
   }

   public String toString() {
      StringBuilder ret = (new StringBuilder(this.getClass().getSimpleName())).append("[");
      boolean first = true;

      for(Entry<Biome, IRealisticBiome> e : this.entrySet()) {
         if (first) {
            ret.append("{");
         } else {
            ret.append(",{");
         }

         ret.append(e.toString()).append("}");
         first = false;
      }

      return ret.append("]").toString();
   }

   public Stream<Entry<Biome, IRealisticBiome>> stream() {
      return StreamSupport.stream(Spliterators.spliterator(this.entrySet(), 0), false);
   }

   private IndexedMap.IndexMapEntry<Biome, IRealisticBiome, Integer> getNewEntry(@Nonnull Biome key, @Nonnull IRealisticBiome value) {
      return (IndexedMap.IndexMapEntry<Biome, IRealisticBiome, Integer>)(this.areEntriesMutable() ? new IndexedMap.IndexMapEntry.IndexEntryBase.SimpleMutableIndexEntry(key, value, (Comparable)this.getIndexer().apply(key)) : new IndexedMap.IndexMapEntry.IndexEntryBase.SimpleImmutableIndexEntry(key, value, (Comparable)this.getIndexer().apply(key)));
   }

   public void setLocked() {
      this.locked = true;
   }
}
