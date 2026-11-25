package org.orecruncher.dsurround.registry.acoustics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
class NullAcoustic implements IAcoustic {
   protected String name;

   public NullAcoustic(@Nonnull String name) {
      this.name = name;
   }

   @Nonnull
   public String getName() {
      return this.name;
   }

   public void playSound(@Nonnull ISoundPlayer player, @Nonnull Vec3d location, @Nullable EventType event, @Nullable IOptions inputOptions) {
   }

   public String toString() {
      return "<< " + this.getName() + " >>";
   }
}
