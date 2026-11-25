package xzeroair.trinkets.util.handlers.mana;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.math.BlockPos;

public class ManaSphere {
   private final BlockPos center;
   private final float radius;
   private float currentMana = 0.0F;

   public ManaSphere(BlockPos center, float radius) {
      this.center = center;
      this.radius = radius;
   }

   public float getCurrentMana() {
      return this.currentMana;
   }

   public void setCurrentMana(float currentMana) {
      this.currentMana = currentMana;
   }

   public BlockPos getCenter() {
      return this.center;
   }

   public float getRadius() {
      return this.radius;
   }

   public static boolean isCenterChunk(Long seed, int chunkX, int chunkZ) {
      EaglercraftRandom random = new EaglercraftRandom(seed.longValue() + (long)chunkX * 1766557063L + (long)chunkZ * 21766558031L);
      return random.nextFloat() < 0.03F;
   }

   public static float getRadius(Long seed, int chunkX, int chunkZ) {
      EaglercraftRandom random = new EaglercraftRandom(seed.longValue() + (long)chunkX * 31766435083L + (long)chunkZ * 655987873L);
      return random.nextFloat() * 40.0F + 20.0F;
   }

   public static int getRandomYOffset(Long seed, int chunkX, int chunkZ) {
      EaglercraftRandom random = new EaglercraftRandom(seed.longValue() + (long)chunkX * 3556692499L + (long)chunkZ * 2998604447L);
      return random.nextInt(60) + 40;
   }
}
