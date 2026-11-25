package org.orecruncher.dsurround.client.weather.tracker;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.weather.Weather;

@SideOnly(Side.CLIENT)
public class Tracker {
   public Tracker() {
   }

   protected String type() {
      return "VANILLA";
   }

   public final boolean isRaining() {
      return this.getIntensityLevel() > 0.0F;
   }

   public final boolean isThundering() {
      return getWorld().isThundering();
   }

   public Weather.Properties getWeatherProperties() {
      return Weather.Properties.VANILLA;
   }

   public float getIntensityLevel() {
      return getWorld().getRainStrength(1.0F);
   }

   public float getMaxIntensityLevel() {
      return 1.0F;
   }

   public int getNextRainChange() {
      return getWorld().getWorldInfo().getRainTime();
   }

   public float getThunderStrength() {
      return getWorld().getThunderStrength(1.0F);
   }

   public int getNextThunderChange() {
      return getWorld().getWorldInfo().getThunderTime();
   }

   public int getNextThunderEvent() {
      return 0;
   }

   public float getCurrentVolume() {
      return 0.66F;
   }

   @Nonnull
   public SoundEvent getCurrentStormSound() {
      return Weather.Properties.VANILLA.getStormSound();
   }

   @Nonnull
   public SoundEvent getCurrentDustSound() {
      return Weather.Properties.VANILLA.getDustSound();
   }

   public boolean doVanilla() {
      return true;
   }

   public boolean backgroundThunderPossible() {
      return false;
   }

   public void update() {
   }

   public String toString() {
      Weather.Properties props = this.getWeatherProperties();
      StringBuilder builder = new StringBuilder();
      builder.append("Storm: ").append(props.name());
      builder.append(" ").append(this.getIntensityLevel()).append('/').append(this.getMaxIntensityLevel());
      builder.append(" vanilla: ").append(getWorld().getRainStrength(1.0F));
      if (this.backgroundThunderPossible()) {
         builder.append(" thunder event: ").append(this.getNextThunderEvent());
      }

      builder.append(" (").append(this.type()).append(')');
      return builder.toString();
   }

   protected static World getWorld() {
      return Minecraft.getMinecraft().world;
   }
}
