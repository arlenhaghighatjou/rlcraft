package net.theawesomegem.fishingmadebetter.util;

import java.util.Random;

public class RandomUtil {
   public RandomUtil() {
   }

   public static int getRandomInRange(Random random, int min, int max) {
      return random.nextInt(max - min + 1) + min;
   }
}
