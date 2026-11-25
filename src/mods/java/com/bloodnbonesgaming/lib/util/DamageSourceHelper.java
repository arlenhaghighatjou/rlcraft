package com.bloodnbonesgaming.lib.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.DamageSource;

public class DamageSourceHelper {
   private static final List<DamageSource> damageSources = new ArrayList();

   public DamageSourceHelper() {
   }

   public static DamageSource getDamageSource(String sourceName) {
      for(DamageSource source : damageSources) {
         if (source.getDamageType().equals(sourceName)) {
            return source;
         }
      }

      return null;
   }

   public static void addDamageSource(DamageSource source) {
      damageSources.add(source);
   }
}
