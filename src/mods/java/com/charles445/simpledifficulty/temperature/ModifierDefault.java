package com.charles445.simpledifficulty.temperature;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModifierDefault extends ModifierBase {
   public ModifierDefault() {
      super("Default");
   }

   public float getWorldInfluence(World world, BlockPos pos) {
      return this.defaultTemperature;
   }
}
