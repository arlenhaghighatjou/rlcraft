package org.orecruncher.dsurround.client.sound;

import javax.annotation.Nonnull;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;

@SideOnly(Side.CLIENT)
public class TrackingSoundInstance extends SoundInstance implements ITickableSound {
   private static final float DONE_VOLUME_THRESHOLD = 1.0E-5F;
   private static final float FADE_AMOUNT = 0.02F;
   private final Entity attachedTo;
   private final SoundEffect effect;
   private boolean isFading;
   private float maxVolume;
   private boolean isDonePlaying;
   private long lastTick;

   TrackingSoundInstance(@Nonnull Entity attachedTo, @Nonnull SoundEffect effect, boolean fadeIn) {
      super(effect.getSound(), effect.getCategory());
      this.attachedTo = attachedTo;
      this.repeat = effect.isRepeatable();
      this.effect = effect;
      this.maxVolume = effect.getVolume();
      this.volume = fadeIn ? 2.0E-5F : this.maxVolume;
      this.pitch = effect.getPitch(RANDOM);
      this.lastTick = (long)(EnvironStateHandler.EnvironState.getTickCounter() - 1);
      this.updateLocation();
   }

   public boolean canRepeat() {
      return !this.isDonePlaying() && super.canRepeat();
   }

   public int getRepeatDelay() {
      return this.effect.getRepeat(RANDOM);
   }

   public void fade() {
      this.isFading = true;
   }

   public void unfade() {
      this.isFading = false;
   }

   public boolean isFading() {
      return this.isFading;
   }

   public boolean isDonePlaying() {
      return this.isDonePlaying;
   }

   public void updateLocation() {
      AxisAlignedBB box = this.attachedTo.getEntityBoundingBox();
      this.setPosition(box.getCenter());
   }

   public boolean isEntityAlive() {
      return this.attachedTo.isEntityAlive();
   }

   public void update() {
      if (!this.isDonePlaying()) {
         if (!this.isEntityAlive()) {
            this.isDonePlaying = true;
         } else {
            long tickDelta = (long)EnvironStateHandler.EnvironState.getTickCounter() - this.lastTick;
            if (tickDelta != 0L) {
               this.lastTick = (long)EnvironStateHandler.EnvironState.getTickCounter();
               if (this.isFading()) {
                  this.volume -= 0.02F * (float)tickDelta;
               } else if (this.volume < this.maxVolume) {
                  this.volume += 0.02F * (float)tickDelta;
               }

               if (this.volume > this.maxVolume) {
                  this.volume = this.maxVolume;
               }

               if (this.volume <= 1.0E-5F) {
                  this.isDonePlaying = true;
                  this.volume = 0.0F;
               } else {
                  this.updateLocation();
               }

            }
         }
      }
   }

   public TrackingSoundInstance setVolume(float volume) {
      if (volume < this.maxVolume || !this.isFading) {
         this.maxVolume = volume;
      }

      return this;
   }
}
