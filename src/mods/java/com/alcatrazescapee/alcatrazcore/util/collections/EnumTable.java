package com.alcatrazescapee.alcatrazcore.util.collections;

import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class EnumTable<R extends Enum<R>, C extends Enum<C>, V> implements Table<R, C, V> {
   protected final EnumMap<R, EnumMap<C, V>> table;
   private final Class<R> rowClass;
   private final Class<C> colClass;

   public EnumTable(Class<R> rowClass, Class<C> colClass) {
      this(new EnumMap(rowClass), rowClass, colClass);
   }

   public EnumTable(Table<? extends R, ? extends C, ? extends V> table, Class<R> rowClass, Class<C> colClass) {
      this(rowClass, colClass);
      this.putAll(table);
   }

   public EnumTable(EnumMap<R, EnumMap<C, V>> table, Class<R> rowClass, Class<C> colClass) {
      this.table = table;
      this.colClass = colClass;
      this.rowClass = rowClass;
   }

   public Class<C> getColClass() {
      return this.colClass;
   }

   public Class<R> getRowClass() {
      return this.rowClass;
   }

   public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey) {
      if (rowKey != null && columnKey != null) {
         EnumMap<C, V> map = (EnumMap)this.table.get(rowKey);
         return map == null ? false : map.containsKey(columnKey);
      } else {
         return false;
      }
   }

   public boolean containsRow(@Nullable Object rowKey) {
      return this.table.containsKey(rowKey);
   }

   public boolean containsColumn(@Nullable Object columnKey) {
      return this.table.values().stream().anyMatch((x) -> {
         return x.containsKey(columnKey);
      });
   }

   public boolean containsValue(@Nullable Object value) {
      return value == null ? false : this.table.values().stream().flatMap((x) -> {
         return x.values().stream();
      }).anyMatch((x) -> {
         return x.equals(value);
      });
   }

   public V get(@Nullable Object rowKey, @Nullable Object columnKey) {
      if (rowKey != null && columnKey != null) {
         EnumMap<C, V> map = (EnumMap)this.table.get(rowKey);
         return (V)(map == null ? null : map.get(columnKey));
      } else {
         return (V)null;
      }
   }

   public boolean isEmpty() {
      return this.table.isEmpty();
   }

   public int size() {
      return this.table.isEmpty() ? 0 : this.table.values().stream().mapToInt(EnumMap::size).sum();
   }

   public void clear() {
      this.table.values().forEach(EnumMap::clear);
      this.table.clear();
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

   @Nullable
   public V remove(@Nullable Object rowKey, @Nullable Object columnKey) {
      if (rowKey != null && columnKey != null) {
         EnumMap<C, V> map = (EnumMap)this.table.get(rowKey);
         return (V)(map == null ? null : map.remove(columnKey));
      } else {
         return (V)null;
      }
   }

   @Nonnull
   public Map<C, V> row(R rowKey) {
      EnumMap map = (EnumMap)this.table.get(rowKey);
      return (Map<C, V>)(map == null ? Collections.EMPTY_MAP : map);
   }

   @Nonnull
   public Map<R, V> column(C columnKey) {
      EnumMap<R, V> map = new EnumMap(this.rowClass);
      this.table.forEach((r, c) -> {
         V v = (V)c.get(columnKey);
         if (v != null) {
            map.put(r, v);
         }

      });
      return map;
   }

   public Set<Cell<R, C, V>> cellSet() {
      return (Set)this.table.entrySet().stream().map((r) -> {
         return (Set)((EnumMap)r.getValue()).entrySet().stream().map((c) -> {
            return new EnumTable.EnumCell(r.getKey(), c.getKey(), c.getValue());
         }).collect(Collectors.toSet());
      }).flatMap(Collection::stream).collect(Collectors.toSet());
   }

   public Set<R> rowKeySet() {
      return this.table.keySet();
   }

   public Set<C> columnKeySet() {
      return (Set)this.table.values().stream().flatMap((x) -> {
         return x.keySet().stream();
      }).collect(Collectors.toSet());
   }

   public Set<V> values() {
      return (Set)this.table.values().stream().flatMap((x) -> {
         return x.values().stream();
      }).collect(Collectors.toSet());
   }

   public Map<R, Map<C, V>> rowMap() {
      return Collections.unmodifiableMap(this.table);
   }

   public Map<C, Map<R, V>> columnMap() {
      Map<C, Map<R, V>> map = new EnumMap(this.colClass);

      for(C c : this.columnKeySet()) {
         map.put(c, this.column(c));
      }

      return Collections.unmodifiableMap(map);
   }

   public static class EnumCell<R, C, V> implements Cell<R, C, V> {
      private R rowKey;
      private C colKey;
      private V value;

      public EnumCell(R rowKey, C colKey, V value) {
         this.rowKey = rowKey;
         this.colKey = colKey;
         this.value = value;
      }

      @Nullable
      public R getRowKey() {
         return this.rowKey;
      }

      @Nullable
      public C getColumnKey() {
         return this.colKey;
      }

      @Nullable
      public V getValue() {
         return this.value;
      }
   }
}
