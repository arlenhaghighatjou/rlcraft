package com.lycanitesmobs.api;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.function.BiFunction;
import net.minecraft.entity.Entity;

public class Targeting {
   private static Set<BiFunction<Entity, Entity, Boolean>> callbacks = Sets.newHashSet();

   public Targeting() {
   }

   public static void registerCallback(BiFunction<Entity, Entity, Boolean> callback) {
      callbacks.add(callback);
   }

   public static boolean isValidTarget(Entity caster, Entity target) {
      if (callbacks.isEmpty()) {
         return true;
      } else {
         for(BiFunction<Entity, Entity, Boolean> callback : callbacks) {
            if (((Boolean)callback.apply(caster, target)).booleanValue()) {
               return true;
            }
         }

         return false;
      }
   }
}
