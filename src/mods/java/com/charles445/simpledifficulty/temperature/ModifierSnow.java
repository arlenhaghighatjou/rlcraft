package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class ModifierSnow extends ModifierBase {
   public ModifierSnow() {
      super("Snow");
   }

   public float getWorldInfluence(World world, BlockPos pos) {
      if (world.isRaining() && world.canSeeSky(pos)) {
         Biome biome = world.getBiome(pos);
         if (biome.getEnableSnow()) {
            return (float)ModConfig.server.temperature.snowValue;
         } else {
            return world.canSnowAt(pos, false) ? (float)ModConfig.server.temperature.snowValue : 0.0F;
         }
      } else {
         return 0.0F;
      }
   }
}
