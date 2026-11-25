package org.orecruncher.dsurround.client.footsteps;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.client.handlers.SoundEffectHandler;
import org.orecruncher.dsurround.client.sound.SoundBuilder;
import org.orecruncher.dsurround.client.sound.SoundInstance;
import org.orecruncher.dsurround.registry.acoustics.EventType;
import org.orecruncher.dsurround.registry.acoustics.IAcoustic;
import org.orecruncher.dsurround.registry.acoustics.IOptions;
import org.orecruncher.dsurround.registry.acoustics.ISoundPlayer;
import org.orecruncher.dsurround.registry.sound.SoundRegistry;
import org.orecruncher.lib.TimeUtils;
import org.orecruncher.lib.collections.ObjectArray;
import org.orecruncher.lib.random.XorShiftRandom;

@SideOnly(Side.CLIENT)
public class SoundPlayer implements ISoundPlayer {
   protected final Random random = XorShiftRandom.current();
   protected final ObjectArray<PendingSound> pending = new ObjectArray();
   protected final float scale;

   public SoundPlayer(float volumeScale) {
      this.scale = volumeScale;
   }

   public void playAcoustic(@Nonnull Association assoc, @Nonnull EventType event) {
      this.playAcoustic(assoc.getStrikeLocation().getStrikePosition(), assoc.getData(), event, (IOptions)null);
   }

   private void logAcousticPlay(@Nonnull IAcoustic[] acoustics, @Nonnull EventType event) {
      if (ModBase.log().isDebugging()) {
         String txt = (String)Arrays.stream(acoustics).map(IAcoustic::getName).collect(Collectors.joining(","));
         ModBase.log().debug(2, "Playing acoustic %s for event %s", new Object[]{txt, event.toString().toUpperCase()});
      }

   }

   public void playAcoustic(@Nonnull Vec3d location, @Nullable IAcoustic[] acoustics, @Nonnull EventType event, @Nullable IOptions inputOptions) {
      if (acoustics != null && acoustics.length > 0) {
         this.logAcousticPlay(acoustics, event);

         for(IAcoustic acoustic : acoustics) {
            acoustic.playSound(this, location, event, inputOptions);
         }
      }

   }

   public void playSound(@Nonnull Vec3d location, @Nonnull SoundEvent sound, float volume, float pitch, @Nullable IOptions options) {
      if (options != null && options.isDelayedSound()) {
         long delay = TimeUtils.currentTimeMillis() + this.randAB(this.random, options.getDelayMin(), options.getDelayMax());
         this.pending.add(new PendingSound(location, sound, volume, pitch, delay, options.getDelayMax()));
      } else {
         this.actuallyPlaySound(location, sound, volume, pitch);
      }

   }

   protected void actuallyPlaySound(@Nonnull Vec3d entity, @Nonnull SoundEvent sound, float volume, float pitch) {
      try {
         SoundInstance s = SoundBuilder.builder(sound, SoundRegistry.FOOTSTEPS).setPosition(entity).setVolume(volume * this.scale).setPitch(pitch).build();
         SoundEffectHandler.INSTANCE.playSound(s);
      } catch (Throwable var6) {
         ModBase.log().error("Unable to play sound", var6);
      }

   }

   public Random getRNG() {
      return this.random;
   }

   public void think() {
      long time = TimeUtils.currentTimeMillis();
      this.pending.removeIf((sound) -> {
         if (sound.getTimeToPlay() <= time) {
            if (!sound.isLate(time)) {
               sound.playSound(this);
            }

            return true;
         } else {
            return false;
         }
      });
   }

   private long randAB(@Nonnull Random rng, long a, long b) {
      return a >= b ? a : a + (long)rng.nextInt((int)(b + 1L));
   }
}
