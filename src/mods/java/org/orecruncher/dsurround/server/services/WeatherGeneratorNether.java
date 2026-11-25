package org.orecruncher.dsurround.server.services;

import javax.annotation.Nonnull;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import org.orecruncher.lib.ReflectedField.BooleanField;

public class WeatherGeneratorNether extends WeatherGenerator {
   private static final BooleanField<WorldProvider> hasSkyLight = new BooleanField(WorldProvider.class, "hasSkyLight", "hasSkyLight");

   public WeatherGeneratorNether(@Nonnull World world) {
      super(world);
   }

   @Nonnull
   public String name() {
      return "NETHER";
   }

   protected void preProcess() {
      hasSkyLight.set(this.world.provider, true);

      try {
         this.world.updateWeatherBody();
      } catch (Throwable var5) {
         ;
      } finally {
         hasSkyLight.set(this.world.provider, false);
      }

   }

   protected void doAmbientThunder() {
   }
}
