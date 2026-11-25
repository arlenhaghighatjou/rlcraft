package com.blamejared.mtlib.utils;

import com.blamejared.mtlib.helpers.LogHelper;
import java.util.Map;

public abstract class BaseMapRemoval<K, V> extends BaseMapModification<K, V> {
   protected BaseMapRemoval(String name, Map<K, V> map) {
      super(name, map);
   }

   protected BaseMapRemoval(String name, Map<K, V> map, Map<K, V> recipes) {
      this(name, map);
      if (recipes != null) {
         this.recipes.putAll(recipes);
      }

   }

   public void apply() {
      if (!this.recipes.isEmpty()) {
         for(K key : this.recipes.keySet()) {
            V oldValue = (V)this.map.remove(key);
            if (oldValue != null) {
               this.successful.put(key, oldValue);
            } else {
               LogHelper.logError(String.format("Error removing %s Recipe : null object", this.name));
            }
         }

      }
   }

   public String describe() {
      return String.format("Removing %d %s Recipe(s) for %s", this.recipes.size(), this.name, this.getRecipeInfo());
   }
}
