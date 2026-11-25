package com.alcatrazescapee.alcatrazcore.util.collections;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.Table.Cell;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ImmutableHashTable<R, C, V> implements Table<R, C, V> {
   private final Map<R, Map<C, V>> TABLE;

   private ImmutableHashTable(Map<R, Map<C, V>> table) {
      this.TABLE = table;
   }

   public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey) {
      if (rowKey != null && columnKey != null) {
         Map<C, V> map = (Map)this.TABLE.get(rowKey);
         return map == null ? false : map.containsKey(columnKey);
      } else {
         return false;
      }
   }

   public boolean containsRow(@Nullable Object rowKey) {
      return this.TABLE.containsKey(rowKey);
   }

   public boolean containsColumn(@Nullable Object columnKey) {
      return this.TABLE.values().stream().anyMatch((x) -> {
         return x.containsKey(columnKey);
      });
   }

   public boolean containsValue(@Nullable Object value) {
      return value == null ? false : this.TABLE.values().stream().flatMap((x) -> {
         return x.values().stream();
      }).anyMatch((x) -> {
         return x.equals(value);
      });
   }

   public V get(@Nullable Object rowKey, @Nullable Object columnKey) {
      if (rowKey != null && columnKey != null) {
         Map<C, V> map = (Map)this.TABLE.get(rowKey);
         return (V)(map == null ? null : map.get(columnKey));
      } else {
         return (V)null;
      }
   }

   public boolean isEmpty() {
      return this.TABLE.isEmpty();
   }

   public int size() {
      return this.TABLE.isEmpty() ? 0 : this.TABLE.values().stream().mapToInt(Map::size).sum();
   }

   /** @deprecated */
   @Deprecated
   public void clear() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Nullable
   @Deprecated
   public V put(R rowKey, C columnKey, V value) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   public void putAll(Table<? extends R, ? extends C, ? extends V> table) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Nullable
   @Deprecated
   public V remove(@Nullable Object rowKey, @Nullable Object columnKey) {
      throw new UnsupportedOperationException();
   }

   public ImmutableMap<C, V> row(R rowKey) {
      Map map = (Map)this.TABLE.get(rowKey);
      return map == null ? ImmutableMap.of() : ImmutableMap.copyOf((Map)this.TABLE.get(rowKey));
   }

   public ImmutableMap<R, V> column(C columnKey) {
      com.google.common.collect.ImmutableMap.Builder<R, V> b = ImmutableMap.builder();
      this.TABLE.forEach((r, c) -> {
         V v = (V)c.get(columnKey);
         if (v != null) {
            b.put(r, v);
         }

      });
      return b.build();
   }

   public ImmutableSet<Cell<R, C, V>> cellSet() {
      return (ImmutableSet)this.TABLE.entrySet().stream().map((r) -> {
         return (Set)((Map)r.getValue()).entrySet().stream().map((c) -> {
            return Tables.immutableCell(r.getKey(), c.getKey(), c.getValue());
         }).collect(Collectors.toSet());
      }).flatMap(Collection::stream).collect(ImmutableSet.toImmutableSet());
   }

   public ImmutableSet<R> rowKeySet() {
      return ImmutableSet.copyOf(this.TABLE.keySet());
   }

   public ImmutableSet<C> columnKeySet() {
      return ImmutableSet.copyOf((Collection)this.TABLE.values().stream().flatMap((x) -> {
         return x.keySet().stream();
      }).collect(ImmutableSet.toImmutableSet()));
   }

   public ImmutableSet<V> values() {
      return (ImmutableSet)this.TABLE.values().stream().flatMap((x) -> {
         return x.values().stream();
      }).collect(ImmutableSet.toImmutableSet());
   }

   public ImmutableMap<R, Map<C, V>> rowMap() {
      return ImmutableMap.copyOf(this.TABLE);
   }

   public ImmutableMap<C, Map<R, V>> columnMap() {
      com.google.common.collect.ImmutableMap.Builder<C, Map<R, V>> b = ImmutableMap.builder();
      UnmodifiableIterator var2 = this.columnKeySet().iterator();

      while(var2.hasNext()) {
         C c = (C)var2.next();
         b.put(c, this.column(c));
      }

      return b.build();
   }

   public static class Builder<R, C, V> {
      private final Map<R, Map<C, V>> TABLE = new HashMap();

      public Builder() {
      }

      @Nullable
      public V put(R rowKey, C columnKey, V value) {
         Map<C, V> map = (Map)this.TABLE.get(rowKey);
         if (map == null) {
            Map<C, V> var6 = new HashMap();
            var6.put(columnKey, value);
            this.TABLE.put(rowKey, var6);
            return (V)null;
         } else {
            V old = (V)map.get(columnKey);
            map.put(columnKey, value);
            return old;
         }
      }

      public void putAll(Table<? extends R, ? extends C, ? extends V> table) {
         table.rowMap().forEach((r, x) -> {
            x.forEach((c, v) -> {
               this.put(r, c, v);
            });
         });
      }

      public ImmutableHashTable<R, C, V> build() {
         return new ImmutableHashTable<R, C, V>(this.TABLE);
      }
   }
}
