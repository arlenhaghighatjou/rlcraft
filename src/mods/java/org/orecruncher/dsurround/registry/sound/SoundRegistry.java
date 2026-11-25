package org.orecruncher.dsurround.registry.sound;

import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.dsurround.client.sound.ConfigSoundInstance;
import org.orecruncher.dsurround.client.sound.SoundBuilder;
import org.orecruncher.dsurround.client.sound.SoundConfigProcessor;
import org.orecruncher.dsurround.client.sound.SoundEngine;
import org.orecruncher.dsurround.client.sound.Sounds;
import org.orecruncher.dsurround.registry.Registry;
import org.orecruncher.dsurround.registry.config.ModConfiguration;
import org.orecruncher.dsurround.registry.config.SoundMetadataConfig;
import org.orecruncher.lib.compat.PositionedSoundUtil;
import org.orecruncher.lib.math.MathStuff;

@SideOnly(Side.CLIENT)
public final class SoundRegistry extends Registry {
   public static final SoundCategory FOOTSTEPS = SoundCategory.valueOf("DS_FOOTSTEPS");
   public static final SoundCategory BIOME = SoundCategory.valueOf("DS_BIOME");
   public static final float MIN_SOUNDFACTOR = 0.0F;
   public static final float MAX_SOUNDFACTOR = 4.0F;
   public static final float DEFAULT_SOUNDFACTOR = 1.0F;
   private final Set<ResourceLocation> blockedSounds = new ObjectOpenHashSet(32);
   private final Object2IntOpenHashMap<ResourceLocation> soundCull = new Object2IntOpenHashMap(32);
   private final Object2FloatOpenHashMap<ResourceLocation> volumeControl = new Object2FloatOpenHashMap(32);
   private final Map<ResourceLocation, SoundMetadata> soundMetadata = new Object2ObjectOpenHashMap();
   private final Map<ResourceLocation, SoundEvent> myRegistry = new Object2ObjectOpenHashMap();
   private final Object2ObjectOpenHashMap<ResourceLocation, SoundEvent> replacements = new Object2ObjectOpenHashMap();

   public SoundRegistry() {
      super("Sound Registry");
      this.volumeControl.defaultReturnValue(1.0F);
   }

   protected void preInit() {
      this.soundCull.clear();
      this.blockedSounds.clear();
      this.volumeControl.clear();
      this.soundMetadata.clear();
      this.myRegistry.clear();
      this.replacements.clear();
      this.bakeSoundRegistry();

      for(String line : ModOptions.sound.soundSettings) {
         String[] parts = line.split(" ");
         if (parts.length < 2) {
            ModBase.log().warn("Missing tokens in sound settings? (%s)", new Object[]{line});
         } else {
            ResourceLocation res = new ResourceLocation(parts[0]);

            for(int i = 1; i < parts.length; ++i) {
               if ("cull".compareToIgnoreCase(parts[i]) == 0) {
                  this.soundCull.put(res, -ModOptions.sound.soundCullingThreshold);
               } else if ("block".compareToIgnoreCase(parts[i]) == 0) {
                  this.blockedSounds.add(res);
               } else {
                  try {
                     int volume = Integer.parseInt(parts[i]);
                     this.volumeControl.put(res, MathStuff.clamp((float)volume / 100.0F, 0.0F, 4.0F));
                  } catch (Throwable var9) {
                     ModBase.log().warn("Unrecognized token %s (%s)", new Object[]{parts[i], line});
                  }
               }
            }
         }
      }

      ResourceLocation bowLooseResource = new ResourceLocation("dsurround", "bow.loose");
      if (!this.isSoundBlocked(bowLooseResource)) {
         SoundEvent bowLoose = this.getSound(bowLooseResource);
         this.replacements.put(new ResourceLocation("minecraft:entity.arrow.shoot"), bowLoose);
         this.replacements.put(new ResourceLocation("minecraft:entity.skeleton.shoot"), bowLoose);
      }

   }

   protected void init(@Nonnull ModConfiguration cfg) {
      this.soundMetadata.putAll((Map)cfg.sounds.entrySet().stream().collect(Collectors.toMap((e) -> {
         return new ResourceLocation((String)e.getKey());
      }, (e) -> {
         return new SoundMetadata((SoundMetadataConfig)e.getValue());
      })));
   }

   protected void complete() {
      ModBase.log().info("[%s] %d sound events in private registry", new Object[]{this.getName(), this.myRegistry.size()});
   }

   private void bakeSoundRegistry() {
      ResourceLocation soundFile = new ResourceLocation("dsurround", "sounds.json");

      try {
         SoundConfigProcessor proc = new SoundConfigProcessor(soundFile);
         Throwable var3 = null;

         try {
            proc.forEach((sound, meta) -> {
               SoundMetadata data = new SoundMetadata(meta);
               ResourceLocation resource = new ResourceLocation("dsurround", sound);
               this.soundMetadata.put(resource, data);
            });
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (proc != null) {
               if (var3 != null) {
                  try {
                     proc.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  proc.close();
               }
            }

         }
      } catch (Exception var15) {
         var15.printStackTrace();
      }

      SoundEngine.instance().getSoundRegistry().getKeys().forEach((rl) -> {
         SoundEvent var10000 = (SoundEvent)this.myRegistry.put(rl, new SoundEvent(rl));
      });
   }

   @Nonnull
   public SoundEvent getSound(ResourceLocation sound) {
      SoundEvent evt = (SoundEvent)this.myRegistry.get(sound);
      if (evt == null) {
         ModBase.log().warn("Cannot find sound that should be registered [%s]", new Object[]{sound.toString()});
         return new SoundEvent(sound);
      } else {
         return evt;
      }
   }

   public boolean isSoundBlocked(@Nonnull ResourceLocation sound) {
      return this.blockedSounds.contains(sound);
   }

   public boolean isSoundCulled(@Nonnull ResourceLocation sound) {
      return this.soundCull.containsKey(sound);
   }

   public float getVolumeScale(@Nonnull ResourceLocation soundName) {
      return this.volumeControl.getFloat(soundName);
   }

   public float getVolumeScale(@Nonnull ISound sound) {
      sound.getSoundLocation();
      return sound instanceof ConfigSoundInstance ? 1.0F : this.volumeControl.getFloat(sound.getSoundLocation());
   }

   @Nullable
   public SoundMetadata getSoundMetadata(@Nullable ResourceLocation resource) {
      return resource != null ? (SoundMetadata)this.soundMetadata.get(resource) : null;
   }

   private boolean isSoundCulledLogical(@Nonnull ResourceLocation res) {
      if (ModOptions.sound.soundCullingThreshold > 0) {
         int lastOccurance = this.soundCull.getInt(res);
         if (lastOccurance != 0) {
            int currentTick = EnvironStateHandler.EnvironState.getTickCounter();
            if (currentTick - lastOccurance < ModOptions.sound.soundCullingThreshold) {
               return true;
            }

            this.soundCull.put(res, currentTick);
         }
      }

      return false;
   }

   private boolean blockSoundProcess(@Nonnull ResourceLocation res) {
      return this.isSoundBlocked(res) || this.isSoundCulledLogical(res);
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void soundPlay(@Nonnull PlaySoundEvent e) {
      ISound theSound = e.getSound();
      if (theSound != null && !(theSound instanceof ConfigSoundInstance)) {
         ResourceLocation soundResource = theSound.getSoundLocation();
         if (this.blockSoundProcess(soundResource)) {
            e.setResultSound((ISound)null);
         } else {
            if ("entity.lightning.thunder".equals(e.getName())) {
               ResourceLocation thunderSound = Sounds.THUNDER.getSound().getSoundName();
               if (!this.isSoundBlocked(thunderSound)) {
                  PositionedSound sound = (PositionedSound)theSound;
                  if (PositionedSoundUtil.getVolume(sound) > 16.0F) {
                     BlockPos pos = new BlockPos((double)sound.getXPosF(), (double)sound.getYPosF(), (double)sound.getZPosF());
                     ISound newSound = Sounds.THUNDER.createSoundAt(pos).setVolume(ModOptions.sound.thunderVolume);
                     e.setResultSound(newSound);
                  }

                  return;
               }
            }

            if (theSound instanceof PositionedSound) {
               SoundEvent rep = (SoundEvent)this.replacements.get(soundResource);
               if (rep != null) {
                  e.setResultSound(SoundBuilder.builder(rep).from((PositionedSound)theSound).build());
               }
            }

         }
      }
   }
}
