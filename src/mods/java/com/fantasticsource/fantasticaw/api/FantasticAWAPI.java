package com.fantasticsource.fantasticaw.api;

import java.lang.reflect.Field;
import net.minecraft.entity.Entity;

public class FantasticAWAPI {
   private static IFantasticAWNatives fantasticAWAPIMethods = null;

   public FantasticAWAPI() {
   }

   public static void refreshRenderModes(Entity entity) {
      if (fantasticAWAPIMethods != null) {
         fantasticAWAPIMethods.refreshRenderModes(entity);
      }

   }

   static {
      try {
         for(Field field : Class.forName("com.fantasticsource.fantasticaw.apinatives.FantasticAWAPI").getDeclaredFields()) {
            if (field.getName().equals("NATIVES")) {
               fantasticAWAPIMethods = (IFantasticAWNatives)field.get((Object)null);
            }
         }
      } catch (IllegalAccessException | ClassNotFoundException var4) {
         var4.printStackTrace();
      }

   }
}
