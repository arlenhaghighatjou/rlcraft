package com.ferreusveritas.dynamictrees.seasons;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SeasonProviderNull implements ISeasonProvider {
   public SeasonProviderNull() {
   }

   public Float getSeasonValue(World world, BlockPos pos) {
      return null;
   }

   public void updateTick(World world, long worldTicks) {
   }

   public boolean shouldSnowMelt(World world, BlockPos pos) {
      return false;
   }
}
