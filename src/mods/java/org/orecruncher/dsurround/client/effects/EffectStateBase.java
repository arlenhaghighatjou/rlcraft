package org.orecruncher.dsurround.client.effects;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.sound.ISoundInstance;
import org.orecruncher.dsurround.client.sound.SoundEffect;

@SideOnly(Side.CLIENT)
public class EffectStateBase implements IEffectState {
   protected static final IParticleHelper NULL_PARTICLE_HELPER = (particle) -> {
   };
   protected static final ISoundHelper NULL_SOUND_HELPER = new ISoundHelper() {
      public boolean playSound(@Nonnull ISoundInstance sound) {
         return false;
      }

      public void stopSound(@Nonnull ISoundInstance sound) {
      }
   };
   protected final IParticleHelper particleHelper;
   protected final ISoundHelper soundHelper;

   protected EffectStateBase() {
      this(NULL_PARTICLE_HELPER, NULL_SOUND_HELPER);
   }

   public EffectStateBase(@Nonnull IParticleHelper ph, @Nonnull ISoundHelper sh) {
      this.particleHelper = ph;
      this.soundHelper = sh;
   }

   public void addParticle(@Nonnull Particle particle) {
      this.particleHelper.addParticle(particle);
   }

   public boolean playSound(@Nonnull ISoundInstance sound) {
      return this.soundHelper.playSound(sound);
   }

   public void stopSound(@Nonnull ISoundInstance sound) {
      this.soundHelper.stopSound(sound);
   }

   @Nonnull
   public ISoundInstance createSound(@Nonnull SoundEffect se, @Nonnull Entity player) {
      return se.createTrackingSound(player, false);
   }

   public boolean isActivePlayer(@Nonnull Entity player) {
      EntityPlayer ep = this.thePlayer();
      return ep.getEntityId() == player.getEntityId();
   }

   @Nonnull
   public EntityPlayer thePlayer() {
      return Minecraft.getMinecraft().player;
   }
}
