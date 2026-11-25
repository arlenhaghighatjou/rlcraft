package org.orecruncher.dsurround.registry.acoustics;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface ISoundPlayer {
   void playSound(@Nonnull Vec3d var1, @Nonnull SoundEvent var2, float var3, float var4, @Nullable IOptions var5);

   EaglercraftRandom getRNG();
}
