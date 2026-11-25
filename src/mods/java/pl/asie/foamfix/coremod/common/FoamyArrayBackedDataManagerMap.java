package pl.asie.foamfix.coremod.common;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

public class FoamyArrayBackedDataManagerMap<V> implements Map<Integer, V> {
   private final Collection<Object> objects = new ArrayList();
   private Object[] keys = new Object[32];
   private int size = 0;
   private final IntOpenHashSet keySet = new IntOpenHashSet();

   public FoamyArrayBackedDataManagerMap() {
   }

   public Set<Integer> keySet() {
      return this.keySet;
   }

   public Set<Entry<Integer, V>> entrySet() {
      HashSet<Entry<Integer, V>> e = new HashSet();
      IntIterator ii = this.keySet.iterator();

      while(ii.hasNext()) {
         final int i = ii.nextInt();
         e.add(new Entry<Integer, V>() {
            public Integer getKey() {
               return i;
            }

            public V getValue() {
               return (V)FoamyArrayBackedDataManagerMap.this.get(i);
            }

            public V setValue(V v) {
               return (V)FoamyArrayBackedDataManagerMap.this.put(Integer.valueOf(i), v);
            }
         });
      }

      return (V)e;
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public boolean containsKey(Object o) {
      return this.get(o) != null;
   }

   public boolean containsKey(int i) {
      return this.get(i) != null;
   }

   public boolean containsValue(Object o) {
      return this.objects.contains(o);
   }

   public V get(int i) {
      return (V)(i >= 0 && i < this.keys.length ? this.keys[i] : null);
   }

   public V get(Object o) {
      int i = ((Integer)o).intValue();
      return (V)(i >= 0 && i < this.keys.length ? this.keys[i] : null);
   }

   public V put(Integer integer, V v) {
      int i = integer.intValue();
      if (i >= 0 && i < 256) {
         if (i >= this.keys.length) {
            int newSize;
            for(newSize = this.keys.length; newSize <= i; newSize *= 2) {
               ;
            }

            Object[] newKeys = new Object[newSize];
            System.arraycopy(this.keys, 0, newKeys, 0, this.keys.length);
            this.keys = newKeys;
         }

         Object old = this.keys[i];
         this.keys[i] = v;
         if (old == null) {
            this.keySet.add(i);
            ++this.size;
         } else {
            this.objects.remove(old);
         }

         this.objects.add(v);
         return (V)old;
      } else {
         throw new RuntimeException("EntityDataManager entry outside of the [0, 255] range (" + i + ") was attempted to be added. Please disable EntityDataManager-related optimizations and report this to the FoamFix developers.");
      }
   }

   public V remove(Object o) {
      int i = ((Integer)o).intValue();
      if (i >= 0 && i < this.keys.length) {
         Object old = this.keys[i];
         this.keys[i] = null;
         this.keySet.rem(i);
         this.objects.remove(old);
         if (old != null) {
            --this.size;
         }

         return (V)old;
      } else {
         return (V)null;
      }
   }

   public void putAll(Map<? extends Integer, ? extends V> map) {
      map.forEach(this::put);
   }

   public void clear() {
      int i;
      for(Iterator var1 = this.keySet().iterator(); var1.hasNext(); this.keys[i] = null) {
         i = ((Integer)var1.next()).intValue();
      }

      this.keySet().clear();
      this.size = 0;
   }

   public Collection<V> values() {
      return this.objects;
   }
}
