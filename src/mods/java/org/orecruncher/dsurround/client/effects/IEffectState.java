package org.orecruncher.dsurround.client.effects;

import javax.annotation.Nonnull;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.sound.ISoundInstance;
import org.orecruncher.dsurround.client.sound.SoundEffect;

@SideOnly(Side.CLIENT)
public interface IEffectState {
   void addParticle(@Nonnull Particle var1);

   boolean playSound(@Nonnull ISoundInstance var1);

   @Nonnull
   ISoundInstance createSound(@Nonnull SoundEffect var1, @Nonnull Entity var2);

   @Nonnull
   EntityPlayer thePlayer();
}
