package com.bloodnbonesgaming.lib.util.script;

import java.math.BigDecimal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

public class ArgTransformer {
   public ArgTransformer() {
   }

   public static Integer toInt(Object arg) {
      return arg instanceof BigDecimal ? ((BigDecimal)arg).intValueExact() : null;
   }

   public static Long toLong(Object arg) {
      return arg instanceof BigDecimal ? ((BigDecimal)arg).longValueExact() : null;
   }

   public static Float toFloat(Object arg) {
      return arg instanceof BigDecimal ? ((BigDecimal)arg).floatValue() : null;
   }

   public static Double toDouble(Object arg) {
      return arg instanceof BigDecimal ? ((BigDecimal)arg).doubleValue() : null;
   }

   public static String toString(Object arg) {
      if (arg instanceof String) {
         return (String)arg;
      } else {
         return arg instanceof Long ? ((Long)arg).toString() : null;
      }
   }

   public static Integer toBiomeInt(Object arg) {
      if (arg instanceof BigDecimal) {
         return ((BigDecimal)arg).intValueExact();
      } else if (arg instanceof String) {
         Biome biome = (Biome)Biome.REGISTRY.getObject(new ResourceLocation((String)arg));
         return biome != null ? Biome.getIdForBiome(biome) : null;
      } else {
         return null;
      }
   }

   public static int[] toBiomeIntArray(Object arg) {
      if (arg instanceof BigDecimal) {
         return new int[]{((BigDecimal)arg).intValueExact()};
      } else if (arg instanceof String) {
         Biome biome = (Biome)Biome.REGISTRY.getObject(new ResourceLocation((String)arg));
         return biome != null ? new int[]{Biome.getIdForBiome(biome)} : null;
      } else if (arg instanceof int[]) {
         return (int[])arg;
      } else {
         if (arg instanceof String[]) {
            String[] array = (String[])arg;
            if (array.length > 0) {
               int[] intArray = new int[array.length];

               for(int i = 0; i < array.length; ++i) {
                  Biome biome = (Biome)Biome.REGISTRY.getObject(new ResourceLocation(array[i]));
                  if (biome == null) {
                     return null;
                  }

                  intArray[i] = Biome.getIdForBiome(biome);
               }

               return intArray;
            }
         }

         return null;
      }
   }
}
