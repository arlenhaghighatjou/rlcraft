package org.orecruncher.dsurround.registry.acoustics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IAcoustic {
   @Nonnull
   String getName();

   void playSound(@Nonnull ISoundPlayer var1, @Nonnull Vec3d var2, @Nullable EventType var3, @Nullable IOptions var4);
}
