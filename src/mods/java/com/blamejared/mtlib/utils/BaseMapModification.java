package com.blamejared.mtlib.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class BaseMapModification<K, V> extends BaseAction {
   protected final HashMap<K, V> recipes;
   protected final HashMap<K, V> successful;
   protected Map<K, V> map;

   protected BaseMapModification(String name, Map<K, V> map) {
      super(name);
      this.map = map;
      this.recipes = new HashMap();
      this.successful = new HashMap();
   }

   protected String getRecipeInfo() {
      if (!this.recipes.isEmpty()) {
         StringBuilder sb = new StringBuilder();

         for(Entry<K, V> recipe : this.recipes.entrySet()) {
            if (recipe != null) {
               sb.append(this.getRecipeInfo(recipe)).append(", ");
            }
         }

         if (sb.length() > 0) {
            sb.setLength(sb.length() - 2);
         }

         return sb.toString();
      } else {
         return super.getRecipeInfo();
      }
   }

   protected abstract String getRecipeInfo(Entry<K, V> var1);
}
