package org.orecruncher.dsurround.client.weather.tracker;

import javax.annotation.Nonnull;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.event.WeatherUpdateEvent;

@SideOnly(Side.CLIENT)
public class ServerDrivenTracker extends SimulationTracker {
   protected int nextRainChange = 0;
   protected float thunderStrength = 0.0F;
   protected int nextThunderChange = 0;

   public ServerDrivenTracker() {
   }

   protected String type() {
      return "SERVER";
   }

   public int getNextRainChange() {
      return this.nextRainChange;
   }

   public float getThunderStrength() {
      return this.thunderStrength;
   }

   public int getNextThunderChange() {
      return this.nextThunderChange;
   }

   public float getCurrentVolume() {
      return 0.05F + 0.95F * this.intensityLevel;
   }

   @Nonnull
   public SoundEvent getCurrentStormSound() {
      return this.intensity.getStormSound();
   }

   @Nonnull
   public SoundEvent getCurrentDustSound() {
      return this.intensity.getDustSound();
   }

   public void update(@Nonnull WeatherUpdateEvent event) {
      this.maxIntensityLevel = event.maxRainIntensity;
      this.nextRainChange = event.nextRainChange;
      this.thunderStrength = event.thunderStrength;
      this.nextThunderChange = event.nextThunderChange;
      this.nextThunderEvent = event.nextThunderEvent;
      this.setCurrentIntensity(event.rainIntensity);
   }

   public void update() {
   }
}
