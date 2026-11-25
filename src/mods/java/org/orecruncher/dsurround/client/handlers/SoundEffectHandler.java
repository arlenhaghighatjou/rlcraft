package org.orecruncher.dsurround.client.handlers;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.client.sound.Emitter;
import org.orecruncher.dsurround.client.sound.EntityEmitter;
import org.orecruncher.dsurround.client.sound.ISoundInstance;
import org.orecruncher.dsurround.client.sound.SoundEffect;
import org.orecruncher.dsurround.client.sound.SoundEngine;
import org.orecruncher.dsurround.client.sound.SoundInstance;
import org.orecruncher.dsurround.client.sound.SoundState;
import org.orecruncher.dsurround.client.sound.Sounds;
import org.orecruncher.dsurround.event.DiagnosticEvent;
import org.orecruncher.dsurround.registry.RegistryDataEvent;
import org.orecruncher.dsurround.registry.sound.SoundRegistry;
import org.orecruncher.lib.ReflectedField.IntegerField;
import org.orecruncher.lib.collections.ObjectArray;

@SideOnly(Side.CLIENT)
public class SoundEffectHandler extends EffectHandlerBase {
   private static final IntegerField<Object> ambienceTicks = new IntegerField("net.minecraft.client.multiplayer.WorldClient", "ambienceTicks", "ambienceTicks");
   public static final SoundEffectHandler INSTANCE = new SoundEffectHandler();
   private final Object2ObjectOpenHashMap<SoundEffect, Emitter> emitters = new Object2ObjectOpenHashMap();
   private final ObjectArray<SoundEffectHandler.PendingSound> pending = new ObjectArray();

   private SoundEffectHandler() {
      super("Sound Effects");
   }

   public void process(@Nonnull EntityPlayer player) {
      this.emitters.values().forEach(Emitter::update);
      this.pending.stream().filter((s) -> {
         return s.getTickAge() >= 0;
      }).forEach((s) -> {
         INSTANCE.playSound(s.getSound());
      });
      this.pending.removeIf((s) -> {
         return s.getTickAge() >= 0;
      });
      this.doMoodProcessing();
   }

   public void onConnect() {
      this.clearSounds();
   }

   public void onDisconnect() {
      this.clearSounds();
   }

   public void clearSounds() {
      this.emitters.values().forEach(Emitter::stop);
      this.emitters.clear();
      this.pending.clear();
      SoundEngine.instance().stopAllSounds();
   }

   public void queueAmbientSounds(@Nonnull Object2FloatOpenHashMap<SoundEffect> sounds) {
      this.emitters.object2ObjectEntrySet().removeIf((entry) -> {
         Emitter emitter = (Emitter)entry.getValue();
         if (emitter.isDonePlaying()) {
            return (void)true;
         } else {
            float volume = sounds.getFloat(entry.getKey());
            if (volume > 0.0F) {
               emitter.setVolumeThrottle(volume);
               if (emitter.isFading()) {
                  emitter.unfade();
               }

               sounds.removeFloat(entry.getKey());
            } else if (!emitter.isFading()) {
               emitter.fade();
            }

            return (void)false;
         }
      });
      sounds.forEach((fx, volume) -> {
         Emitter e = new EntityEmitter(EnvironStateHandler.EnvironState.getPlayer(), fx);
         e.setVolumeThrottle(volume.floatValue());
         this.emitters.put(fx, e);
      });
   }

   public void stopSound(@Nonnull ISoundInstance sound) {
      SoundEngine.instance().stopSound(sound);
   }

   public boolean playSound(@Nonnull ISoundInstance sound) {
      return sound.canSoundBeHeard() && SoundEngine.instance().playSound(sound);
   }

   public void playSoundAtPlayer(@Nonnull EntityPlayer player, @Nonnull SoundEffect sound) {
      this.playSound(sound.createSoundNear(player));
   }

   public boolean playSoundAt(@Nonnull BlockPos pos, @Nonnull SoundEffect sound, int tickDelay) {
      SoundInstance s = sound.createSoundAt(pos);
      if (tickDelay == 0) {
         return this.playSound(s);
      } else {
         s.setState(SoundState.DELAYED);
         this.pending.add(new SoundEffectHandler.PendingSound(s, tickDelay));
         return false;
      }
   }

   @SubscribeEvent
   public void registryReloadEvent(@Nonnull RegistryDataEvent.Reload event) {
      if (event.reg instanceof SoundRegistry) {
         this.onConnect();
      }

   }

   @SubscribeEvent
   public void playerJoinWorldEvent(@Nonnull EntityJoinWorldEvent event) {
      if (getPlayer() != null && event.getEntity().getEntityWorld().isRemote && EnvironStateHandler.EnvironState.isPlayer(event.getEntity()) && !event.getEntity().isDead) {
         this.clearSounds();
      }

   }

   public void doMoodProcessing() {
      if (EnvironStateHandler.EnvironState.getWorld() instanceof WorldClient) {
         WorldClient wc = (WorldClient)EnvironStateHandler.EnvironState.getWorld();
         int ticks = ambienceTicks.get(wc);
         if (ticks == 1) {
            int deltaX = this.RANDOM.nextInt(30) - 15;
            int deltaY = this.RANDOM.nextInt(30) - 15;
            int deltaZ = this.RANDOM.nextInt(30) - 15;
            int distance = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
            if (distance > 4 && distance <= 255) {
               BlockPos blockpos = EnvironStateHandler.EnvironState.getPlayerPosition().add(deltaX, deltaY, deltaZ);
               IBlockState iblockstate = wc.getBlockState(blockpos);
               if (iblockstate.getMaterial() == Material.AIR && wc.getLightFor(EnumSkyBlock.SKY, blockpos) <= 0 && wc.getLight(blockpos) <= this.RANDOM.nextInt(8)) {
                  SoundInstance fx = Sounds.AMBIENT_CAVE.createSoundAt(blockpos).setVolume(0.9F).setPitch(0.8F + this.RANDOM.nextFloat() * 0.2F);
                  this.playSound(fx);
                  ticks = this.RANDOM.nextInt(12000) + 6000;
                  ModBase.log().debug("Next ambient event: %d ticks", new Object[]{ticks});
               }
            }
         }

         if (ticks == 1) {
            ticks = 2;
         }

         ambienceTicks.set(wc, ticks);
      }
   }

   @SubscribeEvent
   public void diagnostics(@Nonnull DiagnosticEvent.Gather event) {
      if (EnvironStateHandler.EnvironState.getWorld() instanceof WorldClient) {
         event.output.add(String.format("Ambiance Timer: %d", ambienceTicks.get(EnvironStateHandler.EnvironState.getWorld())));
      }

      this.emitters.values().forEach((emitter) -> {
         event.output.add("EMITTER: " + emitter.toString());
      });
      this.pending.forEach((effect) -> {
         event.output.add((effect.getTickAge() < 0 ? "DELAYED: " : "PENDING: ") + effect.getSound().toString());
      });
   }

   private static final class PendingSound {
      private final int timeMark;
      private final ISoundInstance sound;

      public PendingSound(@Nonnull ISoundInstance sound, int delay) {
         this.timeMark = EnvironStateHandler.EnvironState.getTickCounter() + delay;
         this.sound = sound;
      }

      public int getTickAge() {
         return EnvironStateHandler.EnvironState.getTickCounter() - this.timeMark;
      }

      @Nonnull
      public ISoundInstance getSound() {
         return this.sound;
      }
   }
}
