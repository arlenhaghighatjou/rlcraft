package org.orecruncher.dsurround.client.handlers.fog;

import javax.annotation.Nonnull;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.weather.Weather;

@SideOnly(Side.CLIENT)
public class WeatherFogRangeCalculator extends VanillaFogRangeCalculator {
   protected static final float START_IMPACT = 0.9F;
   protected static final float END_IMPACT = 0.4F;
   protected final FogResult cache = new FogResult();

   public WeatherFogRangeCalculator() {
   }

   @Nonnull
   public String getName() {
      return "WeatherFogRangeCalculator";
   }

   @Nonnull
   public FogResult calculate(@Nonnull RenderFogEvent event) {
      this.cache.set(event);
      float rainStr = Weather.getIntensityLevel();
      if (rainStr > 0.0F) {
         float startScale = 1.0F - 0.9F * rainStr;
         float endScale = 1.0F - 0.4F * rainStr;
         this.cache.set(this.cache.getStart() * startScale, this.cache.getEnd() * endScale);
      }

      return this.cache;
   }
}
