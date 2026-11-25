package crafttweaker.mc1120.util;

import crafttweaker.api.util.IRandom;
import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class MCRandom implements IRandom {
   private final Random random;

   public MCRandom(Random random) {
      this.random = random;
   }

   public int nextInt() {
      return this.random.nextInt();
   }

   public int nextInt(int bound) {
      return this.random.nextInt(bound);
   }

   public double nextDouble() {
      return this.random.nextDouble();
   }

   public float nextFloat() {
      return this.random.nextFloat();
   }

   public boolean nextBoolean() {
      return this.random.nextBoolean();
   }

   public int nextInt(int minimum, int maximum) {
      return MathHelper.getInt(this.random, minimum, maximum);
   }

   public float nextFloat(float minimum, float maximum) {
      return MathHelper.nextFloat(this.random, minimum, maximum);
   }

   public double nextDouble(double minimum, double maximum) {
      return MathHelper.nextDouble(this.random, minimum, maximum);
   }

   public String getRandomUUID() {
      return MathHelper.getRandomUUID(this.random).toString();
   }
}
