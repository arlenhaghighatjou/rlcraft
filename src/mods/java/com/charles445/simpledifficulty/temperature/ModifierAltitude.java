package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModifierAltitude extends ModifierBase {
   public ModifierAltitude() {
      super("Altitude");
   }

   public float getWorldInfluence(World world, BlockPos pos) {
      return !world.provider.isSurfaceWorld() ? 0.0F : -1.0F * Math.abs((64.0F - (float)pos.getY()) / 64.0F * (float)ModConfig.server.temperature.altitudeMultiplier + 1.0F);
   }
}
