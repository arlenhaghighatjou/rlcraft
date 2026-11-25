package com.ferreusveritas.dynamictrees.seasons;

import com.ferreusveritas.dynamictrees.api.seasons.ISeasonManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SeasonHelper {
   public static final float SPRING = 0.0F;
   public static final float SUMMER = 1.0F;
   public static final float AUTUMN = 2.0F;
   public static final float WINTER = 3.0F;
   public static final float DRY = 1.0F;
   public static final float WET = 3.0F;
   private static ISeasonManager seasonManager = new SeasonManager();

   public SeasonHelper() {
   }

   public static ISeasonManager getSeasonManager() {
      return seasonManager;
   }

   public static void setSeasonManager(ISeasonManager manager) {
      seasonManager = manager;
   }

   public static void updateTick(World world, long worldTicks) {
      seasonManager.updateTick(world, worldTicks);
   }

   public static float globalSeasonalGrowthFactor(World world, BlockPos rootPos) {
      return globalSeasonalGrowthFactor(world, rootPos, 0.0F);
   }

   public static float globalSeasonalGrowthFactor(World world, BlockPos rootPos, float offset) {
      return seasonManager.getGrowthFactor(world, rootPos, offset);
   }

   public static float globalSeasonalSeedDropFactor(World world, BlockPos pos) {
      return globalSeasonalSeedDropFactor(world, pos, 0.0F);
   }

   public static float globalSeasonalSeedDropFactor(World world, BlockPos pos, float offset) {
      return seasonManager.getSeedDropFactor(world, pos, offset);
   }

   public static float globalSeasonalFruitProductionFactor(World world, BlockPos pos) {
      return globalSeasonalFruitProductionFactor(world, pos, 0.0F);
   }

   public static float globalSeasonalFruitProductionFactor(World world, BlockPos pos, float offset) {
      return seasonManager.getFruitProductionFactor(world, pos, offset);
   }

   public static Float getSeasonValue(World world, BlockPos pos) {
      return seasonManager.getSeasonValue(world, pos);
   }

   public static boolean isTropical(World world, BlockPos pos) {
      return seasonManager.isTropical(world, pos);
   }

   public static boolean isSeasonBetween(float testValue, float SeasonA, float SeasonB) {
      testValue = testValue % 4.0F;
      SeasonA = SeasonA % 4.0F;
      SeasonB = SeasonB % 4.0F;
      if (SeasonA <= SeasonB) {
         return testValue > SeasonA && testValue < SeasonB;
      } else {
         return testValue < SeasonB || testValue > SeasonA;
      }
   }

   public static boolean shouldSnowMelt(World world, BlockPos pos) {
      return seasonManager.shouldSnowMelt(world, pos);
   }
}
