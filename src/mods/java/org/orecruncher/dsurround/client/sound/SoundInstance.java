package org.orecruncher.dsurround.client.sound;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.lib.compat.ModEnvironment;
import org.orecruncher.lib.random.XorShiftRandom;
import paulscode.sound.SoundSystemConfig;

@SideOnly(Side.CLIENT)
public class SoundInstance extends PositionedSound implements ISoundInstance {
   protected static final float ATTENUATION_OFFSET = 32.0F;
   protected static final Random RANDOM = XorShiftRandom.current();
   protected final MutableBlockPos pos;
   protected float volumeThrottle;
   protected SoundState state;
   protected boolean canQueue;

   SoundInstance(@Nonnull SoundEvent event, @Nonnull SoundCategory cat) {
      this(event.getSoundName(), cat);
   }

   SoundInstance(@Nonnull ResourceLocation soundResource, @Nonnull SoundCategory cat) {
      super(soundResource, cat);
      this.pos = new MutableBlockPos();
      this.volumeThrottle = 1.0F;
      this.state = SoundState.NONE;
      this.volume = 1.0F;
      this.pitch = 1.0F;
      this.setPosition(0.0F, 0.0F, 0.0F);
      this.repeat = false;
      this.repeatDelay = 0;
      this.attenuationType = AttenuationType.LINEAR;
      super.sound = SoundHandler.MISSING_SOUND;
   }

   public SoundState getState() {
      return this.state;
   }

   public void setState(@Nonnull SoundState state) {
      this.state = state;
   }

   public SoundInstance setCategory(@Nonnull SoundCategory cat) {
      this.category = cat;
      return this;
   }

   public SoundInstance setVolume(float v) {
      this.volume = v;
      return this;
   }

   public SoundInstance setPitch(float p) {
      this.pitch = p;
      return this;
   }

   public SoundInstance setPosition(float x, float y, float z) {
      this.xPosF = x;
      this.yPosF = y;
      this.zPosF = z;
      this.pos.setPos((double)x, (double)y, (double)z);
      return this;
   }

   public SoundInstance setPosition(@Nonnull Vec3i pos) {
      return this.setPosition((float)pos.getX() + 0.5F, (float)pos.getY() + 0.5F, (float)pos.getZ() + 0.5F);
   }

   public SoundInstance setPosition(@Nonnull Vec3d pos) {
      return this.setPosition((float)pos.x, (float)pos.y, (float)pos.z);
   }

   public float getYPosF() {
      float y = super.getYPosF();
      return this.getAttenuationType() == AttenuationType.NONE ? y + 32.0F : y;
   }

   public SoundInstance setAttenuationType(@Nonnull AttenuationType type) {
      this.attenuationType = type;
      return this;
   }

   public SoundInstance setRepeat(boolean flag) {
      this.repeat = flag;
      return this;
   }

   public SoundInstance setRepeatDelay(int delay) {
      this.repeatDelay = delay;
      return this;
   }

   public SoundInstance setVolumeThrottle(float throttle) {
      this.volumeThrottle = throttle;
      return this;
   }

   public void setQueue(boolean f) {
      this.canQueue = f;
   }

   public boolean getQueue() {
      return this.canQueue;
   }

   public float getVolume() {
      return super.getVolume() * this.volumeThrottle;
   }

   public void fade() {
   }

   public void unfade() {
   }

   public boolean isFading() {
      return false;
   }

   public boolean isDonePlaying() {
      return this.getState().isTerminal();
   }

   public boolean canSoundBeHeard() {
      return this.volume > 0.0F && (this.getAttenuationType() == AttenuationType.NONE || SoundSystemConfig.getMasterGain() > 0.0F);
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("Sound{");
      builder.append(this.getSoundLocation().toString());
      builder.append(", ").append(this.getCategory().toString());
      builder.append(", ").append(this.getAttenuationType().toString());
      builder.append(", ").append(this.getState().toString());
      builder.append(", v:").append(this.getVolume());
      builder.append(", p:").append(this.getPitch());
      builder.append(", locX:").append(this.getXPosF());
      builder.append(", locY:").append(this.getYPosF());
      builder.append(", locZ:").append(this.getZPosF());
      builder.append("}");
      return builder.toString();
   }

   public static AttenuationType noAttenuation() {
      return ModEnvironment.SoundPhysics.isLoaded() ? AttenuationType.LINEAR : AttenuationType.NONE;
   }
}
