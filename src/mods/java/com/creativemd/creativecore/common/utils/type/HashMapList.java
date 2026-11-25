package com.creativemd.creativecore.common.utils.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class HashMapList<K, V> {
   private LinkedHashMap<K, ArrayList<V>> keys;

   public HashMapList() {
      this.keys = new LinkedHashMap();
   }

   public HashMapList(HashMapList<K, V> object) {
      this();
      this.addAll(object);
   }

   public ArrayList<V> getValues(K key) {
      return (ArrayList)this.keys.get(key);
   }

   public List<V> getValuesOrEmpty(K key) {
      List<V> result = (List)this.keys.get(key);
      return result != null ? result : Collections.EMPTY_LIST;
   }

   public K getKey(V search) {
      for(Entry<K, ArrayList<V>> entry : this.keys.entrySet()) {
         if (((ArrayList)entry.getValue()).contains(search)) {
            return (K)entry.getKey();
         }
      }

      return (K)null;
   }

   public Set<K> keySet() {
      return this.keys.keySet();
   }

   public Collection<ArrayList<V>> values() {
      return this.keys.values();
   }

   public Set<Entry<K, ArrayList<V>>> entrySet() {
      return this.keys.entrySet();
   }

   public boolean contains(V value) {
      for(ArrayList<V> type : this.values()) {
         if (type.contains(value)) {
            return true;
         }
      }

      return false;
   }

   public boolean contains(K key, V value) {
      ArrayList<V> list = this.getValues(key);
      return list != null ? list.contains(value) : false;
   }

   public void addAll(HashMapList<K, V> map) {
      for(Entry<K, ArrayList<V>> entry : map.entrySet()) {
         this.add(entry.getKey(), (List)entry.getValue());
      }

   }

   public void add(K key, V[] values) {
      this.add(key, new ArrayList(Arrays.asList(values)));
   }

   public void add(K key, List<V> values) {
      ArrayList<V> list = this.getValues(key);
      if (list == null) {
         this.keys.put(key, new ArrayList(values));
      } else {
         list.addAll(values);
      }

   }

   public void add(K key, V value) {
      ArrayList<V> list = this.getValues(key);
      if (list == null) {
         list = new ArrayList();
         list.add(value);
         this.keys.put(key, list);
      } else {
         list.add(value);
      }

   }

   public boolean removeKey(K key) {
      return this.keys.remove(key) != null;
   }

   public boolean removeValue(K key, V value) {
      ArrayList<V> values = this.getValues(key);
      if (values != null && values.remove(value)) {
         if (values.isEmpty()) {
            this.removeKey(key);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean removeValue(V value) {
      Iterator<ArrayList<V>> iterator = this.keys.values().iterator();

      while(iterator.hasNext()) {
         ArrayList<V> values = (ArrayList)iterator.next();
         if (values.remove(value)) {
            if (values.isEmpty()) {
               iterator.remove();
            }

            return true;
         }
      }

      return false;
   }

   public int sizeOfValues() {
      int size = 0;

      for(ArrayList<V> values : this.keys.values()) {
         size += values.size();
      }

      return size;
   }

   public int size() {
      return this.keys.size();
   }

   public void clear() {
      this.keys.clear();
   }

   public String toString() {
      return this.keys.toString();
   }

   public boolean isEmpty() {
      return this.sizeOfValues() == 0;
   }

   public V getFirst() {
      if (this.size() > 0) {
         for(ArrayList<V> list : this.values()) {
            if (list.size() > 0) {
               return (V)list.get(0);
            }
         }
      }

      return (V)null;
   }

   public Iterator<V> iterator() {
      return new Iterator<V>() {
         int index = 0;
         Iterator<ArrayList<V>> iterator = HashMapList.this.values().iterator();
         ArrayList<V> currentList;

         public boolean hasNext() {
            while(this.currentList == null || this.currentList.size() <= this.index) {
               if (!this.iterator.hasNext()) {
                  return false;
               }

               this.currentList = (ArrayList)this.iterator.next();
               this.index = 0;
            }

            return true;
         }

         public V next() {
            V value = (V)this.currentList.get(this.index);
            ++this.index;
            return value;
         }

         public void remove() {
            this.currentList.remove(this.index - 1);
         }
      };
   }
}
