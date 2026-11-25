package org.orecruncher.dsurround.client.sound;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.dsurround.client.handlers.SoundEffectHandler;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.dsurround.registry.sound.SoundMetadata;
import org.orecruncher.lib.gui.RecordTitleEmitter;
import org.orecruncher.lib.gui.RecordTitleEmitter.ITimeKeeper;
import org.orecruncher.lib.random.XorShiftRandom;

@SideOnly(Side.CLIENT)
public abstract class Emitter {
   protected static final int ERROR_DELAY = 10;
   protected static final int ERROR_DELAY_RANDOM = 6;
   protected static final EaglercraftRandom RANDOM = XorShiftRandom.current();
   protected static final ITimeKeeper timer = EnvironStateHandler.EnvironState::getTickCounter;
   protected final RecordTitleEmitter titleEmitter;
   protected final SoundEffect effect;
   protected SoundInstance activeSound;
   protected boolean done = false;
   protected int errorDelayTicks;

   public Emitter(@Nonnull SoundEffect sound) {
      this.effect = sound;
      if (StringUtils.isEmpty(sound.getSoundTitle())) {
         SoundEvent effect = this.effect.getSound();
         SoundMetadata data = effect != null ? RegistryManager.SOUND.getSoundMetadata(effect.getRegistryName()) : null;
         if (data != null) {
            if (!StringUtils.isEmpty(data.getTitle())) {
               StringBuilder builder = new StringBuilder();
               builder.append(data.getTitle());
               if (data.getCredits().size() > 0) {
                  builder.append(" by ").append((String)data.getCredits().get(0));
               }

               this.titleEmitter = new RecordTitleEmitter(builder.toString(), timer);
            } else {
               this.titleEmitter = null;
            }
         } else {
            this.titleEmitter = null;
         }
      } else {
         this.titleEmitter = new RecordTitleEmitter(sound.getSoundTitle(), timer);
      }

   }

   protected abstract SoundInstance createSound();

   public void update() {
      if (this.titleEmitter != null) {
         this.titleEmitter.update();
      }

      if (this.activeSound == null) {
         this.activeSound = this.createSound();
         this.activeSound.setQueue(true);
      } else {
         if (this.activeSound.getState().isActive()) {
            if (!this.activeSound.canSoundBeHeard() || this.isFading() && this.activeSound.getState() == SoundState.DELAYED) {
               SoundEffectHandler.INSTANCE.stopSound(this.activeSound);
            }

            return;
         }

         if (this.isFading()) {
            this.done = true;
            return;
         }
      }

      try {
         if (this.errorDelayTicks > 0) {
            --this.errorDelayTicks;
         } else if (!SoundEffectHandler.INSTANCE.playSound(this.activeSound)) {
            this.errorDelayTicks = 10 + RANDOM.nextInt(6);
         }
      } catch (Throwable var2) {
         ModBase.log().error("Unable to play sound", var2);
      }

   }

   public void setVolumeThrottle(float throttle) {
      if (this.activeSound != null) {
         this.activeSound.setVolumeThrottle(throttle);
      }

   }

   public void setPitch(float pitch) {
      if (this.activeSound != null) {
         this.activeSound.setPitch(pitch);
      }

   }

   public void fade() {
      if (this.activeSound != null) {
         ModBase.log().debug("FADE: %s", new Object[]{this.activeSound.toString()});
         this.activeSound.fade();
      }

   }

   public boolean isFading() {
      return this.activeSound != null ? this.activeSound.isFading() : false;
   }

   public void unfade() {
      if (this.activeSound != null) {
         ModBase.log().debug("UNFADE: %s", new Object[]{this.activeSound.toString()});
         this.activeSound.unfade();
      }

   }

   public boolean isDonePlaying() {
      if (this.activeSound == null) {
         return this.done;
      } else {
         return this.done || this.activeSound.isDonePlaying();
      }
   }

   public void stop() {
      if (this.activeSound != null) {
         this.activeSound.setRepeat(false);
         SoundEngine.instance().stopSound(this.activeSound);
      }

   }

   public String toString() {
      return this.activeSound.toString();
   }
}
