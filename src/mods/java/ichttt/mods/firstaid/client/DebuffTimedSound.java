package ichttt.mods.firstaid.client;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.FirstAidConfig;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DebuffTimedSound implements ITickableSound {
   private static final float volumeMultiplier = 1.25F;
   private final float minusPerTick;
   private final int debuffDuration;
   private final ResourceLocation soundLocation;
   private final SoundEvent event;
   private final WeakReference<EntityPlayerSP> player;
   private Sound sound;
   private float volume = 1.25F;
   private int ticks;
   private static final Map<SoundEvent, DebuffTimedSound> activeSounds = new HashMap();

   public static void playHurtSound(SoundEvent event, int duration) {
      if (FirstAidConfig.enableSoundSystem) {
         SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();
         DebuffTimedSound matchingSound = (DebuffTimedSound)activeSounds.get(event);
         if (matchingSound != null) {
            if (!matchingSound.isDonePlaying()) {
               soundHandler.stopSound(matchingSound);
            }

            activeSounds.remove(event);
         }

         DebuffTimedSound newSound = new DebuffTimedSound(event, duration);
         soundHandler.playSound(newSound);
         activeSounds.put(event, newSound);
      }
   }

   public DebuffTimedSound(SoundEvent event, int debuffDuration) {
      this.event = event;
      this.soundLocation = event.getSoundName();
      this.player = new WeakReference(Minecraft.getMinecraft().player);
      this.debuffDuration = Integer.min(300, debuffDuration);
      this.minusPerTick = 1.0F / (float)this.debuffDuration * 1.25F;
   }

   public boolean isDonePlaying() {
      EntityPlayerSP player = (EntityPlayerSP)this.player.get();
      boolean done = player == null || this.ticks >= this.debuffDuration || player.getHealth() <= 0.0F;
      if (done) {
         activeSounds.remove(this.event);
      }

      return done;
   }

   @Nonnull
   public ResourceLocation getSoundLocation() {
      return this.soundLocation;
   }

   @Nullable
   public SoundEventAccessor createAccessor(@Nonnull SoundHandler handler) {
      SoundEventAccessor soundEventAccessor = handler.getAccessor(this.soundLocation);
      if (soundEventAccessor == null) {
         FirstAid.LOGGER.warn("Missing sound for location " + this.soundLocation);
         this.sound = SoundHandler.MISSING_SOUND;
      } else {
         this.sound = soundEventAccessor.cloneEntry();
      }

      return soundEventAccessor;
   }

   @Nonnull
   public Sound getSound() {
      return this.sound;
   }

   @Nonnull
   public SoundCategory getCategory() {
      return SoundCategory.PLAYERS;
   }

   public boolean canRepeat() {
      return false;
   }

   public int getRepeatDelay() {
      return 0;
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return 1.0F;
   }

   public float getXPosF() {
      EntityPlayerSP player = (EntityPlayerSP)this.player.get();
      return player == null ? 0.0F : (float)player.posX;
   }

   public float getYPosF() {
      EntityPlayerSP player = (EntityPlayerSP)this.player.get();
      return player == null ? 0.0F : (float)player.posY;
   }

   public float getZPosF() {
      EntityPlayerSP player = (EntityPlayerSP)this.player.get();
      return player == null ? 0.0F : (float)player.posZ;
   }

   @Nonnull
   public AttenuationType getAttenuationType() {
      return AttenuationType.NONE;
   }

   public void update() {
      ++this.ticks;
      this.volume = Math.max(0.15F, this.volume - this.minusPerTick);
   }
}
