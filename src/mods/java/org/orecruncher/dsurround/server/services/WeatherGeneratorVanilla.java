package org.orecruncher.dsurround.server.services;

import javax.annotation.Nonnull;
import net.minecraft.world.World;

public class WeatherGeneratorVanilla extends WeatherGenerator {
   public WeatherGeneratorVanilla(@Nonnull World world) {
      super(world);
   }

   @Nonnull
   public String name() {
      return "VANILLA";
   }

   protected void doRain() {
      float str = this.world.getRainStrength(1.0F);
      if (!this.worldInfo().isRaining() && str <= 0.0F) {
         this.data.setRainIntensity(0.0F);
         this.data.setCurrentRainIntensity(0.0F);
      } else {
         this.data.setRainIntensity(1.0F);
         this.data.setCurrentRainIntensity(str);
      }

   }
}
