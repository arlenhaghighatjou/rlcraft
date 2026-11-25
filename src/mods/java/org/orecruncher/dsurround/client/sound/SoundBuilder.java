package org.orecruncher.dsurround.client.sound;

import javax.annotation.Nonnull;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.mixins.IPositionedSoundMixin;

@SideOnly(Side.CLIENT)
public class SoundBuilder {
   private final SoundInstance sound;

   private SoundBuilder(@Nonnull SoundEvent evt, @Nonnull SoundCategory cat) {
      this.sound = new SoundInstance(evt, cat);
   }

   public SoundBuilder from(@Nonnull PositionedSound ps) {
      this.sound.setCategory(ps.getCategory());
      this.sound.setPosition(ps.getXPosF(), ps.getYPosF(), ps.getZPosF());
      this.sound.setAttenuationType(ps.getAttenuationType());
      IPositionedSoundMixin sound = (IPositionedSoundMixin)ps;
      this.sound.setVolume(sound.getVolumeRaw());
      this.sound.setPitch(sound.getPitchRaw());
      return this;
   }

   public SoundBuilder setPosition(float x, float y, float z) {
      this.sound.setPosition(x, y, z);
      return this;
   }

   public SoundBuilder setPosition(@Nonnull BlockPos pos) {
      this.sound.setPosition(pos);
      return this;
   }

   public SoundBuilder setPosition(@Nonnull Vec3d pos) {
      this.sound.setPosition(pos);
      return this;
   }

   public SoundBuilder setVolume(float v) {
      this.sound.setVolume(v);
      return this;
   }

   public SoundBuilder setPitch(float p) {
      this.sound.setPitch(p);
      return this;
   }

   public SoundInstance build() {
      return this.sound;
   }

   public static SoundBuilder builder(@Nonnull SoundEvent evt) {
      return builder(evt, SoundCategory.NEUTRAL);
   }

   public static SoundBuilder builder(@Nonnull SoundEvent evt, @Nonnull SoundCategory cat) {
      return new SoundBuilder(evt, cat);
   }

   public static SoundInstance create(@Nonnull SoundEvent evt, @Nonnull SoundCategory cat) {
      return new SoundInstance(evt, cat);
   }
}
