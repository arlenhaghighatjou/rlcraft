package org.orecruncher.dsurround.registry.acoustics;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SimpleAcoustic implements IAcoustic {
   protected SoundEvent sound;
   protected float volMin = 1.0F;
   protected float volMax = 1.0F;
   protected float pitchMin = 1.0F;
   protected float pitchMax = 1.0F;
   protected IOptions outputOptions;

   public SimpleAcoustic() {
   }

   public SimpleAcoustic(@Nonnull SoundEvent evt) {
      this.sound = evt;
   }

   @Nonnull
   public String getName() {
      return this.sound != null ? this.sound.getSoundName().toString() : "<UNKNOWN>";
   }

   public void playSound(@Nonnull ISoundPlayer player, @Nonnull Vec3d location, @Nullable EventType event, @Nullable IOptions inputOptions) {
      if (this.sound != null) {
         float volume = this.generateVolume(player.getRNG());
         float pitch = this.generatePitch(player.getRNG());
         if (inputOptions != null) {
            if (inputOptions.getGlidingVolume() > 0.0F) {
               volume = this.volMin + (this.volMax - this.volMin) * inputOptions.getGlidingVolume();
            }

            if (inputOptions.getGlidingPitch() > 0.0F) {
               pitch = this.pitchMin + (this.pitchMax - this.pitchMin) * inputOptions.getGlidingPitch();
            }

            volume *= inputOptions.getVolumeScale();
            pitch *= inputOptions.getPitchScale();
         }

         player.playSound(location, this.sound, volume, pitch, this.outputOptions);
      }
   }

   private float generateVolume(@Nonnull Random rng) {
      return this.randAB(rng, this.volMin, this.volMax);
   }

   private float generatePitch(@Nonnull Random rng) {
      return this.randAB(rng, this.pitchMin, this.pitchMax);
   }

   private float randAB(@Nonnull Random rng, float a, float b) {
      return a >= b ? a : a + rng.nextFloat() * (b - a);
   }

   public void setSound(@Nullable SoundEvent sound) {
      this.sound = sound;
   }

   public void setVolMin(float volMin) {
      this.volMin = volMin;
   }

   public void setVolMax(float volMax) {
      this.volMax = volMax;
   }

   public void setPitchMin(float pitchMin) {
      this.pitchMin = pitchMin;
   }

   public void setPitchMax(float pitchMax) {
      this.pitchMax = pitchMax;
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(this.getName()).append('[');
      builder.append("vMin:").append(this.volMin).append(',');
      builder.append("vMax:").append(this.volMax).append(',');
      builder.append("pMin:").append(this.pitchMax).append(',');
      builder.append("pMax:").append(this.pitchMax);
      builder.append(']');
      return builder.toString();
   }
}
