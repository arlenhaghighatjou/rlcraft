package org.orecruncher.dsurround.registry.acoustics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.footsteps.ConfigOptions;
import org.orecruncher.dsurround.client.weather.Weather;

@SideOnly(Side.CLIENT)
public class RainSplashAcoustic implements IAcoustic {
   protected final IAcoustic[] acoustics;

   public RainSplashAcoustic(@Nonnull IAcoustic[] acoustics) {
      this.acoustics = acoustics;
   }

   @Nonnull
   public String getName() {
      return "RainSplash";
   }

   public void playSound(@Nonnull ISoundPlayer player, @Nonnull Vec3d location, @Nullable EventType event, @Nullable IOptions inputOptions) {
      ConfigOptions ops = new ConfigOptions();
      ops.setVolumeScale(Weather.getIntensityLevel() * 0.8F);
      ops.setPitchScale(1.75F);

      for(IAcoustic acoustic : this.acoustics) {
         acoustic.playSound(player, location, event, ops);
      }

   }

   public String toString() {
      return "<< RainSplash >>";
   }
}
