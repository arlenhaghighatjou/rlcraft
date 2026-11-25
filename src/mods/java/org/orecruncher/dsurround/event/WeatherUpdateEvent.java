package org.orecruncher.dsurround.event;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.Event;

public class WeatherUpdateEvent extends Event {
   public final int dimId;
   public final float rainIntensity;
   public final float maxRainIntensity;
   public final int nextRainChange;
   public final float thunderStrength;
   public final int nextThunderChange;
   public final int nextThunderEvent;

   public WeatherUpdateEvent(int dim, float rainIntensity, float maxRainIntensity, int nextRainChange, float thunderStrength, int nextThunderChange, int nextThunderEvent) {
      this.dimId = dim;
      this.maxRainIntensity = MathHelper.clamp(maxRainIntensity, 0.0F, 1.0F);
      this.rainIntensity = MathHelper.clamp(rainIntensity, 0.0F, this.maxRainIntensity);
      this.nextRainChange = nextRainChange;
      this.thunderStrength = thunderStrength;
      this.nextThunderChange = nextThunderChange;
      this.nextThunderEvent = nextThunderEvent;
   }
}
