package com.blamejared.mtlib.utils;

import com.blamejared.mtlib.helpers.LogHelper;
import java.util.HashMap;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

public abstract class BaseMapAddition<K, V> extends BaseMapModification<K, V> {
   protected final HashMap<K, V> overwritten;

   protected BaseMapAddition(String name, Map<K, V> map) {
      super(name, map);
      this.overwritten = new HashMap();
   }

   protected BaseMapAddition(String name, Map<K, V> map, Map<K, V> recipes) {
      this(name, map);
      this.recipes.putAll(recipes);
   }

   public void apply() {
      if (!this.recipes.isEmpty()) {
         for(Entry<K, V> entry : this.recipes.entrySet()) {
            K key = (K)entry.getKey();
            V value = (V)entry.getValue();
            V oldValue = (V)this.map.put(key, value);
            if (oldValue != null) {
               LogHelper.logInfo(String.format("Overwritten %s Recipe for %s", this.name, this.getRecipeInfo(new SimpleEntry(entry.getKey(), value))));
               this.overwritten.put(key, oldValue);
            }

            this.successful.put(key, value);
         }

      }
   }

   public String describe() {
      return String.format("Adding %d %s Recipe(s) for %s", this.recipes.size(), this.name, this.getRecipeInfo());
   }
}
