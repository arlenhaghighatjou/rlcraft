package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public abstract class ModifierBase implements ITemperatureModifier {
   private final String name;
   protected final float defaultTemperature;

   protected ModifierBase(String name) {
      this.name = name;
      this.defaultTemperature = (float)((TemperatureEnum.NORMAL.getUpperBound() + TemperatureEnum.COLD.getUpperBound()) / 2);
   }

   public float getPlayerInfluence(EntityPlayer player) {
      return 0.0F;
   }

   public float getWorldInfluence(World world, BlockPos pos) {
      return 0.0F;
   }

   public String getName() {
      return this.name;
   }

   protected float applyUndergroundEffect(float temperature, World world, BlockPos pos) {
      if (pos.getY() >= 64) {
         return temperature;
      } else if (ModConfig.server.temperature.undergroundEffect && world.provider.isSurfaceWorld()) {
         if (!world.canSeeSky(pos) && !world.canSeeSky(pos.up())) {
            int cutoff = ModConfig.server.temperature.undergroundEffectCutoff;
            return pos.getY() > cutoff && cutoff != 64 ? temperature * (float)(pos.getY() - cutoff) / (64.0F - (float)cutoff) : 0.0F;
         } else {
            return temperature;
         }
      } else {
         return temperature;
      }
   }

   protected float getTempForBiome(Biome biome) {
      return MathHelper.clamp(biome.getDefaultTemperature(), 0.0F, 1.35F) / 1.35F;
   }

   protected float normalizeToPlusMinus(float value) {
      return value * 2.0F - 1.0F;
   }
}
