package org.orecruncher.dsurround.client.sound;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.sound.SoundSetupEvent;
import net.minecraftforge.client.event.sound.SoundEvent.SoundSourceEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.event.DiagnosticEvent;
import org.orecruncher.dsurround.mixins.ISoundHandlerMixin;
import org.orecruncher.dsurround.mixins.ISoundManagerMixin;
import org.orecruncher.dsurround.mixins.ISoundSystemMixin;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.lib.ThreadGuard;
import org.orecruncher.lib.ReflectedField.BooleanField;
import org.orecruncher.lib.ReflectedField.ObjectField;
import org.orecruncher.lib.ThreadGuard.Action;
import org.orecruncher.lib.math.MathStuff;
import paulscode.sound.CommandObject;
import paulscode.sound.Library;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.Source;

@EventBusSubscriber(
   value = {Side.CLIENT},
   modid = "dsurround"
)
public final class SoundEngine {
   private static final ObjectField<SoundManager, SoundSystem> getSoundSystem = new ObjectField(SoundManager.class, "sndSystem", "sndSystem");
   private static final BooleanField<Source> removed = new BooleanField(Source.class, "removed", (String)null);
   private static final float MUTE_VOLUME = 1.0E-5F;
   private static final int MAX_STREAM_CHANNELS = 16;
   private static final int SOUND_QUEUE_SLACK = 6;
   private static int maxSounds = 0;
   private static final SoundEngine instance_ = new SoundEngine();
   private final ThreadGuard guard = (new ThreadGuard(ModBase.log(), Side.CLIENT, "SoundManager")).setAction(ModBase.isDeveloperMode() ? Action.EXCEPTION : (ModOptions.logging.enableDebugLogging ? Action.LOG : Action.NONE));
   private final Set<ISoundInstance> queuedSounds = new ReferenceOpenHashSet(256);
   private String playedSoundId = null;

   public static SoundEngine instance() {
      return instance_;
   }

   private SoundEngine() {
      MinecraftForge.EVENT_BUS.register(this);
   }

   @Nonnull
   public SoundRegistry getSoundRegistry() {
      return ((ISoundHandlerMixin)((ISoundHandlerMixin)Minecraft.getMinecraft().getSoundHandler())).getSoundRegistry();
   }

   @Nonnull
   public SoundManager getSoundManager() {
      return ((ISoundHandlerMixin)((ISoundHandlerMixin)Minecraft.getMinecraft().getSoundHandler())).getSoundManager();
   }

   private int currentSoundCount() {
      return this.getPlayingSounds().size();
   }

   private boolean canFitSound() {
      return this.currentSoundCount() < maxSounds - 6;
   }

   private void flushSoundQueue() {
      this.getSoundSystem().CommandQueue((CommandObject)null);
   }

   private SoundSystem getSoundSystem() {
      return (SoundSystem)getSoundSystem.get(this.getSoundManager());
   }

   private Library getSoundLibrary() {
      return ((ISoundSystemMixin)this.getSoundSystem()).getSoundLibrary();
   }

   private Map<String, ISound> getPlayingSounds() {
      return ((ISoundManagerMixin)this.getSoundManager()).getPlayingSounds();
   }

   private Map<ISound, String> getPlayingSoundsInv() {
      return ((ISoundManagerMixin)this.getSoundManager()).getPlayingSoundsInv();
   }

   private Map<ISound, Integer> getDelayedSounds() {
      return ((ISoundManagerMixin)this.getSoundManager()).getDelayedSounds();
   }

   public boolean isSoundPlaying(@Nonnull ISoundInstance sound) {
      return sound.getState().isActive();
   }

   public void stopSound(@Nonnull ISoundInstance sound) {
      if (sound.getState() == SoundState.QUEUED) {
         sound.setState(SoundState.DONE);
      } else {
         this.getSoundManager().stopSound(sound);
      }

   }

   public void stopAllSounds() {
      this.getSoundManager().stopAllSounds();
      this.flushSoundQueue();
      this.clearOrphans();
      this.queuedSounds.forEach((s) -> {
         s.setState(SoundState.DONE);
      });
      this.queuedSounds.clear();
   }

   public boolean playSound(@Nonnull ISoundInstance sound) {
      if (this.queuedSounds.contains(sound)) {
         return sound.getState().isActive();
      } else {
         sound.setState(SoundState.ERROR);
         return this.playSound0(sound);
      }
   }

   protected boolean playSound0(@Nonnull ISoundInstance sound) {
      if (this.canFitSound()) {
         this.playedSoundId = null;

         try {
            this.getSoundManager().playSound(sound);
            if (this.playedSoundId != null) {
               this.queuedSounds.add(sound);
               sound.setState(SoundState.PLAYING);
            }
         } catch (Throwable var4) {
            String txt = String.format("Unable to play sound [%s]", sound);
            ModBase.log().error(txt, var4);
         }
      } else if (sound.getQueue() && sound.getState() != SoundState.QUEUED) {
         sound.setState(SoundState.QUEUED);
         this.queuedSounds.add(sound);
      }

      if (ModBase.log().testTrace(1)) {
         if (sound.getState().isActive()) {
            String tag = sound.getState() == SoundState.QUEUED ? "WAITING" : "";
            ModBase.log().debug("> QUEUED: [%s] %s", new Object[]{sound, tag});
         } else {
            ModBase.log().debug("> NOT QUEUED: [%s]", new Object[]{sound});
         }
      }

      return sound.getState().isActive();
   }

   private void clearOrphans() {
      Map<String, ISound> playingSounds = this.getPlayingSounds();
      SoundSystem sndSystem = this.getSoundSystem();
      synchronized(SoundSystemConfig.THREAD_SYNC) {
         Map<String, Source> sounds = this.getSoundLibrary().getSources();
         List<String> remove = (List)sounds.entrySet().stream().filter((e) -> {
            return !playingSounds.containsKey(e.getKey());
         }).map((e) -> {
            Source src = (Source)e.getValue();
            ModBase.log().debug("Killing orphaned sound [%s]", new Object[]{src.filenameURL != null ? src.filenameURL.getFilename() : "UNKNOWN"});
            cleanupSource(src);
            return (String)e.getKey();
         }).collect(Collectors.toList());
         remove.forEach(sndSystem::removeSource);
      }
   }

   private static void cleanupSource(@Nonnull Source source) {
      if (source.toStream) {
         removed.set(source, true);
      } else {
         source.cleanup();
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void clientTick(@Nonnull ClientTickEvent event) {
      if (event.side == Side.CLIENT && event.phase == Phase.END) {
         Map<ISound, Integer> delayedSounds = this.getDelayedSounds();
         Map<ISound, String> playingInv = this.getPlayingSoundsInv();
         this.queuedSounds.removeIf((sound) -> {
            switch(sound.getState()) {
            case QUEUED:
               if (this.canFitSound()) {
                  this.playSound0(sound);
               }
               break;
            case DELAYED:
               if (!delayedSounds.containsKey(sound)) {
                  sound.setState(playingInv.containsKey(sound) ? SoundState.PLAYING : SoundState.DONE);
               }
               break;
            case PLAYING:
               if (!playingInv.containsKey(sound)) {
                  sound.setState(delayedSounds.containsKey(sound) ? SoundState.DELAYED : SoundState.DONE);
               }
               break;
            case NONE:
               sound.setState(SoundState.ERROR);
            }

            return sound.getState().isTerminal();
         });
      }

   }

   public boolean isMuted() {
      try {
         return this.getSoundSystem().getMasterVolume() == 1.0E-5F;
      } catch (Throwable var2) {
         return false;
      }
   }

   public void setMuted(boolean flag) {
      try {
         if (flag) {
            this.getSoundSystem().setMasterVolume(1.0E-5F);
         } else {
            GameSettings options = Minecraft.getMinecraft().gameSettings;
            if (options != null) {
               this.getSoundSystem().setMasterVolume(options.getSoundLevel(SoundCategory.MASTER));
            }
         }
      } catch (Throwable var3) {
         ;
      }

   }

   @SubscribeEvent
   public void onSoundSourceEvent(@Nonnull SoundSourceEvent event) {
      this.guard.check("playSound");
      this.playedSoundId = event.getUuid();
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void diagnostics(DiagnosticEvent.Gather event) {
      event.output.add(TextFormatting.AQUA + "SoundSystem: " + this.currentSoundCount() + "/" + maxSounds);
      event.output.add(TextFormatting.AQUA + "Tracking   : " + this.queuedSounds.size());
      List<String> results = (List)((Map)this.getPlayingSounds().values().stream().map((s) -> {
         return s.getSound().getSoundLocation();
      }).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))).entrySet().stream().map((e) -> {
         return TextFormatting.GOLD + ((ResourceLocation)e.getKey()).toString() + ": " + e.getValue();
      }).sorted().collect(Collectors.toList());
      event.output.addAll(results);
   }

   public static float getVolume(@Nullable SoundCategory category) {
      if (category != null && category != SoundCategory.MASTER) {
         GameSettings settings = Minecraft.getMinecraft().gameSettings;
         return settings != null ? settings.getSoundLevel(category) : 1.0F;
      } else {
         return 1.0F;
      }
   }

   private static boolean fadeMusic(@Nonnull ISound sound) {
      return sound.getCategory() == SoundCategory.MUSIC && !(sound instanceof ConfigSoundInstance) && (!(sound instanceof TrackingSoundInstance) || !ModOptions.sound.enableBattleMusic);
   }

   private static float getVolumeScale(@Nullable ISound sound) {
      if (sound == null) {
         return 1.0F;
      } else {
         try {
            float fade = fadeMusic(sound) ? MusicFader.getMusicScaling() : 1.0F;
            return RegistryManager.SOUND.getVolumeScale(sound) * fade;
         } catch (Throwable var2) {
            return 1.0F;
         }
      }
   }

   public static float getClampedVolume(@Nonnull ISound sound) {
      float volumeScale = getVolumeScale(sound);
      float volume = sound.getVolume() * getVolume(sound.getCategory()) * volumeScale;
      return MathStuff.clamp(volume, 0.0F, 1.0F);
   }

   private static void alErrorCheck() {
      int error = AL10.alGetError();
      if (error != 0) {
         ModBase.log().warn("OpenAL error: %d", new Object[]{error});
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void configureSound(@Nullable SoundSetupEvent event) {
      int totalChannels = -1;

      try {
         boolean create = !AL.isCreated();
         if (create) {
            AL.create();
            alErrorCheck();
         }

         IntBuffer ib = BufferUtils.createIntBuffer(1);
         ALC10.alcGetInteger(AL.getDevice(), 4112, ib);
         alErrorCheck();
         totalChannels = ib.get(0);
         if (create) {
            AL.destroy();
         }
      } catch (Throwable var4) {
         var4.printStackTrace();
      }

      int normalChannelCount = ModOptions.sound.normalSoundChannelCount;
      int streamChannelCount = ModOptions.sound.streamingSoundChannelCount;
      if (ModOptions.sound.autoConfigureChannels && totalChannels > 64) {
         totalChannels -= 32;
         streamChannelCount = Math.min(totalChannels / 8, 16);
         normalChannelCount = totalChannels - streamChannelCount;
      }

      ModBase.log().info("Sound channels: %d normal, %d streaming (total avail: %s)", new Object[]{normalChannelCount, streamChannelCount, totalChannels == -1 ? "UNKNOWN" : Integer.toString(totalChannels)});
      SoundSystemConfig.setNumberNormalChannels(normalChannelCount);
      SoundSystemConfig.setNumberStreamingChannels(streamChannelCount);
      maxSounds = SoundSystemConfig.getNumberNormalChannels() + SoundSystemConfig.getNumberStreamingChannels();
      if (ModOptions.sound.streamBufferCount != 0) {
         SoundSystemConfig.setNumberStreamingBuffers(ModOptions.sound.streamBufferCount);
      }

      if (ModOptions.sound.streamBufferSize != 0) {
         SoundSystemConfig.setStreamingBufferSize(ModOptions.sound.streamBufferSize * 1024);
      }

      ModBase.log().info("Stream buffers: %d x %d", new Object[]{SoundSystemConfig.getNumberStreamingBuffers(), SoundSystemConfig.getStreamingBufferSize()});
   }
}
