package org.orecruncher.dsurround.server.services;

import javax.annotation.Nonnull;
import net.minecraft.world.World;

public class WeatherGeneratorNone extends WeatherGenerator {
   public WeatherGeneratorNone(@Nonnull World world) {
      super(world);
   }

   @Nonnull
   public String name() {
      return "NULL";
   }

   protected void process() {
      this.data.setRainIntensity(0.0F);
      this.data.setCurrentRainIntensity(0.0F);
      this.data.setThunderTimer(0);
   }
}
