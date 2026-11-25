package org.orecruncher.dsurround.registry.blockstate;

import com.google.common.base.Joiner;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.fx.BlockEffect;
import org.orecruncher.dsurround.client.sound.SoundEffect;
import org.orecruncher.lib.MyUtils;
import org.orecruncher.lib.WeightTable;

@SideOnly(Side.CLIENT)
public class BlockStateProfile extends BlockStateData {
   protected int chance = 100;
   protected SoundEffect[] sounds;
   protected BlockEffect[] effects;
   protected BlockEffect[] alwaysOn;

   public BlockStateProfile() {
      this.sounds = NO_SOUNDS;
      this.effects = NO_EFFECTS;
      this.alwaysOn = NO_EFFECTS;
   }

   @Nonnull
   public BlockStateProfile setChance(int chance) {
      this.chance = chance;
      return this;
   }

   public int getChance() {
      return this.chance;
   }

   @Nonnull
   public BlockStateProfile addSound(@Nonnull SoundEffect sound) {
      this.sounds = (SoundEffect[])MyUtils.append(this.sounds, sound);
      return this;
   }

   @Nonnull
   public BlockStateProfile clearSounds() {
      this.sounds = NO_SOUNDS;
      return this;
   }

   @Nonnull
   public SoundEffect[] getSounds() {
      return this.sounds;
   }

   @Nonnull
   public BlockStateProfile addEffect(@Nonnull BlockEffect effect) {
      if (effect.getChance() > 0) {
         this.effects = (BlockEffect[])MyUtils.append(this.effects, effect);
      } else {
         this.alwaysOn = (BlockEffect[])MyUtils.append(this.alwaysOn, effect);
      }

      return this;
   }

   @Nonnull
   public BlockStateProfile clearEffects() {
      this.effects = NO_EFFECTS;
      this.alwaysOn = NO_EFFECTS;
      return this;
   }

   @Nonnull
   public BlockEffect[] getEffects() {
      return this.effects;
   }

   @Nonnull
   public BlockEffect[] getAlwaysOnEffects() {
      return this.alwaysOn;
   }

   @Nullable
   public SoundEffect getSoundToPlay(@Nonnull EaglercraftRandom random) {
      return this.sounds != NO_SOUNDS && random.nextInt(this.getChance()) == 0 ? (SoundEffect)(new WeightTable(this.sounds)).next() : null;
   }

   public boolean hasSoundsOrEffects() {
      return this.sounds.length > 0 || this.effects.length > 0;
   }

   public boolean hasAlwaysOnEffects() {
      return this.alwaysOn.length > 0;
   }

   @Nonnull
   public String toString() {
      StringBuilder builder = new StringBuilder();
      if (this.sounds != NO_SOUNDS) {
         builder.append(" chance:").append(this.chance);
         builder.append("; sounds [");
         builder.append(Joiner.on(',').join(this.sounds));
         builder.append(']');
      } else {
         builder.append("NO SOUNDS");
      }

      if (this.effects != this.alwaysOn) {
         builder.append("; effects [");
         builder.append(Joiner.on(',').join(this.effects));
         builder.append(',');
         builder.append(Joiner.on(',').join(this.alwaysOn));
         builder.append(']');
      } else {
         builder.append("; NO EFFECTS");
      }

      return builder.toString();
   }
}
