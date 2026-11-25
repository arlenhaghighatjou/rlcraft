package com.creativemd.creativecore.common.utils.type;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class PairList<K, V> extends ArrayList<Pair<K, V>> {
   protected HashMap<K, Integer> keyIndex = new HashMap();
   protected List<V> values = new ArrayList();

   public PairList() {
   }

   public PairList(List<Pair<K, V>> list) {
      super(list);
      this.updateEntireMap();
   }

   protected void updateEntireMap() {
      this.keyIndex.clear();
      this.values.clear();

      for(int i = 0; i < this.size(); ++i) {
         this.keyIndex.put(((Pair)this.get(i)).key, Integer.valueOf(i));
         this.values.add(((Pair)this.get(i)).value);
      }

   }

   public boolean add(Pair<K, V> e) {
      Objects.requireNonNull(e);
      if (this.keyIndex.containsKey(e.key)) {
         throw new IllegalArgumentException("Duplicates are not allowed key: " + e.key);
      } else if (super.add(e)) {
         this.keyIndex.put(e.key, Integer.valueOf(this.size() - 1));
         this.values.add(e.value);
         return true;
      } else {
         return false;
      }
   }

   public void add(int index, Pair<K, V> element) {
      Objects.requireNonNull(element);
      if (this.keyIndex.containsKey(element.key)) {
         throw new IllegalArgumentException("Duplicates are not allowed key: " + element.key);
      } else {
         super.add(index, element);
         this.updateEntireMap();
      }
   }

   public boolean addAll(Collection<? extends Pair<K, V>> c) {
      Objects.requireNonNull(c);

      for(Pair<K, V> pair : c) {
         Objects.requireNonNull(pair);
         if (this.keyIndex.containsKey(pair.key)) {
            throw new IllegalArgumentException("Duplicates are not allowed key: " + pair.key);
         }
      }

      int sizeBefore = this.size();
      if (!super.addAll(c)) {
         return false;
      } else {
         for(int i = 0; i < c.size(); ++i) {
            int index = sizeBefore + i;
            this.keyIndex.put(((Pair)this.get(index)).key, Integer.valueOf(index));
            this.values.add(((Pair)this.get(index)).value);
         }

         return true;
      }
   }

   public boolean addAll(int index, Collection<? extends Pair<K, V>> c) {
      Objects.requireNonNull(c);

      for(Pair<K, V> pair : c) {
         Objects.requireNonNull(pair);
         if (this.keyIndex.containsKey(pair.key)) {
            throw new IllegalArgumentException("Duplicates are not allowed key: " + pair.key);
         }
      }

      if (super.addAll(index, c)) {
         this.updateEntireMap();
         return true;
      } else {
         return false;
      }
   }

   public boolean add(K key, V value) {
      return this.add(new Pair(key, value));
   }

   public void set(K key, V value) {
      Pair<K, V> pair = this.getPair(key);
      if (pair != null) {
         pair.value = value;
      }

   }

   public Pair<K, V> remove(int index) {
      Pair<K, V> pair;
      if ((pair = (Pair)super.remove(index)) != null) {
         this.updateEntireMap();
         return pair;
      } else {
         return null;
      }
   }

   public boolean remove(Object o) {
      if (super.remove(o)) {
         this.updateEntireMap();
         return true;
      } else {
         return false;
      }
   }

   public boolean removeAll(Collection<?> c) {
      if (super.removeAll(c)) {
         this.updateEntireMap();
         return true;
      } else {
         return false;
      }
   }

   public boolean removeIf(Predicate<? super Pair<K, V>> filter) {
      if (super.removeIf(filter)) {
         this.updateEntireMap();
         return true;
      } else {
         return false;
      }
   }

   public boolean retainAll(Collection<?> c) {
      if (super.retainAll(c)) {
         this.updateEntireMap();
         return true;
      } else {
         return false;
      }
   }

   public void clear() {
      super.clear();
      this.keyIndex.clear();
      this.values.clear();
   }

   public Pair<K, V> set(int index, Pair<K, V> element) {
      Objects.requireNonNull(element);
      Integer exisiting = (Integer)this.keyIndex.get(element.key);
      if (exisiting != null && exisiting.intValue() != index) {
         throw new IllegalArgumentException("Duplicates are not allowed key: " + element.key);
      } else {
         Pair<K, V> old = (Pair)super.set(index, element);
         if (old != null) {
            this.keyIndex.remove(old.key);
            this.keyIndex.put(element.key, Integer.valueOf(index));
            this.values.set(index, element.value);
         }

         return old;
      }
   }

   public void sort(Comparator<? super Pair<K, V>> c) {
      super.sort(c);
      this.updateEntireMap();
   }

   public boolean containsKey(K key) {
      return this.keyIndex.containsKey(key);
   }

   public int indexOfKey(K key) {
      return ((Integer)this.keyIndex.getOrDefault(key, Integer.valueOf(-1))).intValue();
   }

   public boolean removeKey(K key) {
      Integer index = (Integer)this.keyIndex.get(key);
      if (index != null) {
         return this.remove(index.intValue()) != null;
      } else {
         return false;
      }
   }

   public List<V> values() {
      return ImmutableList.copyOf(this.values);
   }

   public List<K> keys() {
      List<K> keys = new ArrayList(this.size());

      for(int i = 0; i < this.size(); ++i) {
         keys.add(((Pair)this.get(i)).key);
      }

      return ImmutableList.copyOf(keys);
   }

   public Pair<K, V> getFirst() {
      return this.isEmpty() ? null : (Pair)this.get(0);
   }

   public Pair<K, V> getLast() {
      return this.isEmpty() ? null : (Pair)this.get(this.size() - 1);
   }

   @Nullable
   public V getValue(K key) {
      Integer index = (Integer)this.keyIndex.get(key);
      return (V)(index != null ? ((Pair)this.get(index.intValue())).value : null);
   }

   @Nullable
   public Pair<K, V> getPair(K key) {
      Integer index = (Integer)this.keyIndex.get(key);
      return index != null ? (Pair)this.get(index.intValue()) : null;
   }
}
