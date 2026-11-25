package org.orecruncher.dsurround.registry.acoustics;

import com.google.common.base.Joiner;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SimultaneousAcoustic implements IAcoustic {
   protected final IAcoustic[] acoustics;

   public SimultaneousAcoustic(@Nonnull Collection<IAcoustic> acoustics) {
      this.acoustics = (IAcoustic[])acoustics.toArray(new IAcoustic[0]);
   }

   @Nonnull
   public String getName() {
      return "Simultaneous Acoustic";
   }

   public void playSound(@Nonnull ISoundPlayer player, @Nonnull Vec3d location, @Nullable EventType event, @Nullable IOptions inputOptions) {
      for(IAcoustic acoustic : this.acoustics) {
         acoustic.playSound(player, location, event, inputOptions);
      }

   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(this.getName()).append('[');
      builder.append(Joiner.on(',').join(this.acoustics));
      builder.append(']');
      return builder.toString();
   }
}
