package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModifierBiome extends ModifierBase {
   public ModifierBiome() {
      super("Biome");
   }

   public float getWorldInfluence(World world, BlockPos pos) {
      float biomeAverage = (this.getTempForBiome(world.getBiome(pos.add(10, 0, 0))) + this.getTempForBiome(world.getBiome(pos.add(-10, 0, 0))) + this.getTempForBiome(world.getBiome(pos.add(0, 0, 10))) + this.getTempForBiome(world.getBiome(pos.add(0, 0, -10))) + this.getTempForBiome(world.getBiome(pos.add(7, 0, 7))) + this.getTempForBiome(world.getBiome(pos.add(7, 0, -7))) + this.getTempForBiome(world.getBiome(pos.add(-7, 0, 7))) + this.getTempForBiome(world.getBiome(pos.add(-7, 0, -7))) + this.getTempForBiome(world.getBiome(pos))) / 9.0F;
      return this.applyUndergroundEffect(this.normalizeToPlusMinus(biomeAverage) * (float)ModConfig.server.temperature.biomeMultiplier, world, pos);
   }
}
