package com.fantasticsource.mctools.sound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SimpleSound implements ISound {
   public final ResourceLocation rl;
   public final SoundCategory category;
   public float volume;
   public float pitch;
   public AttenuationType attenuationType;
   protected Sound sound;
   protected boolean repeat;
   protected int repeatDelay;
   protected final Entity following;
   protected final float x;
   protected final float y;
   protected final float z;

   public SimpleSound(ResourceLocation rl, SoundCategory category) {
      this(rl, category, 0);
   }

   public SimpleSound(ResourceLocation rl, SoundCategory category, @Nonnull Entity following) {
      this(rl, category, 0, following);
   }

   public SimpleSound(ResourceLocation rl, SoundCategory category, float x, float y, float z) {
      this(rl, category, 0, x, y, z);
   }

   public SimpleSound(ResourceLocation rl, SoundCategory category, int repeatDelay) {
      this(rl, category, repeatDelay, Minecraft.getMinecraft().player);
   }

   public SimpleSound(ResourceLocation rl, SoundCategory category, int repeatDelay, @Nonnull Entity following) {
      this.volume = 1.0F;
      this.pitch = 1.0F;
      this.attenuationType = AttenuationType.LINEAR;
      this.x = 0.0F;
      this.y = 0.0F;
      this.z = 0.0F;
      this.rl = rl;
      this.category = category;
      this.repeatDelay = repeatDelay;
      this.following = following;
      this.repeat = repeatDelay > 0;
   }

   public SimpleSound(ResourceLocation rl, SoundCategory category, int repeatDelay, float x, float y, float z) {
      this.volume = 1.0F;
      this.pitch = 1.0F;
      this.attenuationType = AttenuationType.LINEAR;
      this.following = null;
      this.rl = rl;
      this.category = category;
      this.repeatDelay = repeatDelay;
      this.x = x;
      this.y = y;
      this.z = z;
      this.repeat = repeatDelay > 0;
   }

   @SideOnly(Side.CLIENT)
   public static void play(ResourceLocation rl) {
      Minecraft.getMinecraft().getSoundHandler().playSound(new SimpleSound(rl, SoundCategory.MASTER));
   }

   public ResourceLocation getSoundLocation() {
      return this.rl;
   }

   @Nullable
   public SoundEventAccessor createAccessor(SoundHandler handler) {
      SoundEventAccessor accessor = handler.getAccessor(this.rl);
      if (accessor == null) {
         this.sound = SoundHandler.MISSING_SOUND;
      } else {
         this.sound = accessor.cloneEntry();
      }

      return accessor;
   }

   public Sound getSound() {
      return this.sound;
   }

   public SoundCategory getCategory() {
      return this.category;
   }

   public boolean canRepeat() {
      return this.repeat;
   }

   public int getRepeatDelay() {
      return this.repeatDelay;
   }

   public float getVolume() {
      return this.sound.getVolume() * this.volume;
   }

   public float getPitch() {
      return this.sound.getPitch() * this.pitch;
   }

   public float getXPosF() {
      return this.following == null ? this.x : (float)this.following.posX;
   }

   public float getYPosF() {
      return this.following == null ? this.y : (float)this.following.posY;
   }

   public float getZPosF() {
      return this.following == null ? this.z : (float)this.following.posZ;
   }

   public AttenuationType getAttenuationType() {
      return this.attenuationType;
   }
}
