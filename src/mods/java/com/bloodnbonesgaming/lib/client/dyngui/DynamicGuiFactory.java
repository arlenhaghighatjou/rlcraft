package com.bloodnbonesgaming.lib.client.dyngui;

import com.google.common.collect.Maps;
import java.util.Map;

public class DynamicGuiFactory {
   public static DynamicGuiFactory INSTANCE = new DynamicGuiFactory();
   private final Map<Class<?>, DynamicGuiScreen> generatedScreens = Maps.newIdentityHashMap();

   public DynamicGuiFactory() {
   }

   public boolean generateScreen(Class<?> clazz) {
      if (!this.generatedScreens.containsKey(clazz)) {
         this.generatedScreens.put(clazz, new DynamicGuiScreen());
         return true;
      } else {
         return false;
      }
   }

   public DynamicGuiScreen getScreen(Class<?> clazz) {
      this.generateScreen(clazz);
      return (DynamicGuiScreen)this.generatedScreens.get(clazz);
   }
}
