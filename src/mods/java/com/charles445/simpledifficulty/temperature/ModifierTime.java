package com.charles445.simpledifficulty.temperature;

import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModifierTime extends ModifierBase {
   public ModifierTime() {
      super("Time");
   }

   public float getWorldInfluence(World world, BlockPos pos) {
      if (!world.provider.isSurfaceWorld()) {
         return 0.0F;
      } else {
         long time = world.getWorldTime() % 24000L;
         if (time < 12000L && !ModConfig.server.temperature.timeTemperatureDay) {
            return 0.0F;
         } else if (time >= 12000L && !ModConfig.server.temperature.timeTemperatureNight) {
            return 0.0F;
         } else {
            float timetemperature = (Math.abs(((float)time % 12000.0F - 6000.0F) / 6000.0F) - 1.0F) * (float)ModConfig.server.temperature.timeMultiplier;
            if (time < 12000L) {
               timetemperature *= -1.0F;
            }

            float biomeMultiplier = 1.0F + Math.abs(this.normalizeToPlusMinus(this.getTempForBiome(world.getBiome(pos)))) * ((float)ModConfig.server.temperature.timeBiomeMultiplier - 1.0F);
            timetemperature = timetemperature * biomeMultiplier;
            int shadeConf = ModConfig.server.temperature.timeTemperatureShade;
            if (timetemperature > 0.0F && shadeConf != 0 && !world.canSeeSky(pos) && !world.canSeeSky(pos.up())) {
               timetemperature = Math.max(0.0F, timetemperature + (float)shadeConf);
            }

            return this.applyUndergroundEffect(timetemperature, world, pos);
         }
      }
   }
}
