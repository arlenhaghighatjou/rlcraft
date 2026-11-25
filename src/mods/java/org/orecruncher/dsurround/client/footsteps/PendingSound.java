package org.orecruncher.dsurround.client.footsteps;

import javax.annotation.Nonnull;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.acoustics.IOptions;
import org.orecruncher.dsurround.registry.acoustics.ISoundPlayer;

@SideOnly(Side.CLIENT)
public class PendingSound {
   private static final float LATENESS_SLACK_FACTOR = 1.2F;
   private final Vec3d location;
   private final SoundEvent sound;
   private final float volume;
   private final float pitch;
   private final long timeToPlay;
   private final long maximum;
   private final float lateTolerance;

   public PendingSound(@Nonnull Vec3d location, @Nonnull SoundEvent sound, float volume, float pitch, long timeToPlay, long maximum) {
      this.location = location;
      this.sound = sound;
      this.volume = volume;
      this.pitch = pitch;
      this.timeToPlay = timeToPlay;
      this.maximum = maximum;
      this.lateTolerance = (float)maximum * 1.2F;
   }

   public void playSound(@Nonnull ISoundPlayer player) {
      player.playSound(this.location, this.sound, this.volume, this.pitch, (IOptions)null);
   }

   public long getTimeToPlay() {
      return this.timeToPlay;
   }

   public long getMaximumBase() {
      return this.maximum;
   }

   public float getLateTolerance() {
      return this.lateTolerance;
   }

   public boolean isLate(long time) {
      if (this.maximum < 0L) {
         return false;
      } else {
         return (float)(time - this.timeToPlay) > this.lateTolerance;
      }
   }

   public long howLate(long time) {
      return time - this.timeToPlay;
   }
}
