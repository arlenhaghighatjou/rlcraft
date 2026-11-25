package ivorius.reccomplex.utils.leveled;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LeveledMap<K, V> {
   protected final Map<K, V> map;
   protected final TObjectIntMap<K> levelMap;
   protected final List<Map<K, V>> levels;

   public LeveledMap(Map<K, V> map, Collection<? extends Map<K, V>> levels) {
      this.levelMap = new TObjectIntHashMap();
      this.levels = new ArrayList();
      this.map = map;
      this.levels.addAll(levels);
   }

   public LeveledMap(int levels) {
      this(new HashMap(), (Collection)IntStream.range(0, levels).mapToObj((i) -> {
         return new HashMap();
      }).collect(Collectors.toList()));
   }

   public int levels() {
      return this.levels.size();
   }

   public boolean has(K k, int level) {
      return ((Map)this.levels.get(level)).containsKey(k);
   }

   public V put(K k, V v, int level) {
      if (level < this.levels() && level >= 0) {
         V prev = (V)((Map)this.levels.get(level)).put(k, v);
         if (level >= this.levelMap.get(k)) {
            this.map.put(k, v);
            this.levelMap.put(k, level);
         }

         return prev;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public V remove(K k, int level) {
      if (level < this.levels() && level >= 0) {
         V prev = (V)((Map)this.levels.get(level)).remove(k);
         if (level == this.levelMap.get(k)) {
            this.putFirst(k, level);
         }

         return prev;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private boolean putFirst(K k, int startLevel) {
      while(true) {
         --startLevel;
         if (startLevel >= 0) {
            V next = (V)((Map)this.levels.get(startLevel)).get(k);
            if (next == null) {
               continue;
            }

            this.map.put(k, next);
            this.levelMap.put(k, startLevel);
            return true;
         }

         this.map.remove(k);
         this.levelMap.remove(k);
         return false;
      }
   }

   public void clear() {
      this.map.clear();
      this.levelMap.clear();
      this.levels.forEach(Map::clear);
   }

   public void clear(int level) {
      if (level < this.levels() && level >= 0) {
         ((Map)this.levels.get(level)).clear();
         this.calculateMap();
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private void calculateMap() {
      this.map.clear();
      this.levelMap.clear();

      for(int l = 0; l < this.levels(); ++l) {
         this.map.putAll((Map)this.levels.get(l));

         for(K k : ((Map)this.levels.get(l)).keySet()) {
            this.levelMap.put(k, l);
         }
      }

   }

   public Map<K, V> getMap() {
      return this.map;
   }

   public Map<K, V> getMap(int level) {
      return (Map)this.levels.get(level);
   }
}
