package com.ferreusveritas.dynamictrees.util;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class MathHelper {
   public MathHelper() {
   }

   public static int selectRandomFromDistribution(EaglercraftRandom random, int[] distMap) {
      int distSize = 0;

      for(int i = 0; i < distMap.length; ++i) {
         distSize += distMap[i];
      }

      if (distSize <= 0) {
         return -1;
      } else {
         int rnd = random.nextInt(distSize) + 1;

         for(int i = 0; i < 6; ++i) {
            if (rnd <= distMap[i]) {
               return i;
            }

            rnd -= distMap[i];
         }

         return 0;
      }
   }

   public static float shortDegreesDist(float ang1, float ang2) {
      float max = 360.0F;
      float da = (ang2 - ang1) % max;
      return 2.0F * da % max - da;
   }

   public static float angleDegreesInterpolate(float ang1, float ang2, float t) {
      return ang1 + shortDegreesDist(ang1, ang2) * t;
   }
}
