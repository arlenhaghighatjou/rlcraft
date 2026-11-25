package com.alcatrazescapee.alcatrazcore.util.collections;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.Table.Cell;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ImmutableEnumTable<R extends Enum<R>, C extends Enum<C>, V> extends EnumTable<R, C, V> {
   private ImmutableEnumTable(EnumMap<R, EnumMap<C, V>> table, Class<R> rowClass, Class<C> colClass) {
      super(table, rowClass, colClass);
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

   @Nonnull
   public ImmutableMap<C, V> row(R rowKey) {
      EnumMap map = (EnumMap)this.table.get(rowKey);
      return map == null ? ImmutableMap.of() : ImmutableMap.copyOf(map);
   }

   @Nonnull
   public ImmutableMap<R, V> column(C columnKey) {
      com.google.common.collect.ImmutableMap.Builder<R, V> b = ImmutableMap.builder();
      this.table.forEach((r, c) -> {
         V v = (V)c.get(columnKey);
         if (v != null) {
            b.put(r, v);
         }

      });
      return b.build();
   }

   public ImmutableSet<Cell<R, C, V>> cellSet() {
      return (ImmutableSet)this.table.entrySet().stream().map((r) -> {
         return (Set)((EnumMap)r.getValue()).entrySet().stream().map((c) -> {
            return Tables.immutableCell(r.getKey(), c.getKey(), c.getValue());
         }).collect(Collectors.toSet());
      }).flatMap(Collection::stream).collect(ImmutableSet.toImmutableSet());
   }

   public ImmutableSet<R> rowKeySet() {
      return ImmutableSet.copyOf(this.table.keySet());
   }

   public ImmutableSet<C> columnKeySet() {
      return ImmutableSet.copyOf((Collection)this.table.values().stream().flatMap((x) -> {
         return x.keySet().stream();
      }).collect(ImmutableSet.toImmutableSet()));
   }

   public ImmutableSet<V> values() {
      return (ImmutableSet)this.table.values().stream().flatMap((x) -> {
         return x.values().stream();
      }).collect(ImmutableSet.toImmutableSet());
   }

   public ImmutableMap<R, Map<C, V>> rowMap() {
      return ImmutableMap.copyOf(this.table);
   }

   public ImmutableMap<C, Map<R, V>> columnMap() {
      com.google.common.collect.ImmutableMap.Builder<C, Map<R, V>> b = ImmutableMap.builder();
      UnmodifiableIterator var2 = this.columnKeySet().iterator();

      while(var2.hasNext()) {
         C c = var2.next();
         b.put(c, this.column(c));
      }

      return b.build();
   }

   public static class Builder<R extends Enum<R>, C extends Enum<C>, V> {
      private final EnumMap<R, EnumMap<C, V>> table;
      private final Class<C> colClass;
      private final Class<R> rowClass;

      public Builder(Class<R> rowClass, Class<C> colClass) {
         this.colClass = colClass;
         this.rowClass = rowClass;
         this.table = new EnumMap(rowClass);
      }

      @Nullable
      public V put(R rowKey, C columnKey, V value) {
         EnumMap<C, V> map = (EnumMap)this.table.get(rowKey);
         if (map == null) {
            map = new EnumMap(this.colClass);
            map.put(columnKey, value);
            this.table.put(rowKey, map);
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

      public ImmutableEnumTable<R, C, V> build() {
         return new ImmutableEnumTable<R, C, V>(this.table, this.rowClass, this.colClass);
      }
   }
}
