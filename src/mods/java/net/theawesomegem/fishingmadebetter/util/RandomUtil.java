package net.theawesomegem.fishingmadebetter.util;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public class RandomUtil {
   public RandomUtil() {
   }

   public static int getRandomInRange(EaglercraftRandom random, int min, int max) {
      return random.nextInt(max - min + 1) + min;
   }
}
