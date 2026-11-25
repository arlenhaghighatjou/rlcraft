package org.orecruncher.dsurround.registry.blockstate;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.fx.BlockEffect;
import org.orecruncher.dsurround.client.sound.SoundEffect;

@SideOnly(Side.CLIENT)
public class BlockStateData {
   public static final BlockEffect[] NO_EFFECTS = new BlockEffect[0];
   public static final SoundEffect[] NO_SOUNDS = new SoundEffect[0];
   protected static final BlockStateData DEFAULT = new BlockStateData();

   public BlockStateData() {
   }

   @Nonnull
   public BlockStateData setChance(int chance) {
      return this;
   }

   public int getChance() {
      return 0;
   }

   @Nonnull
   public BlockStateData addSound(@Nonnull SoundEffect sound) {
      return this;
   }

   @Nonnull
   public BlockStateData clearSounds() {
      return this;
   }

   @Nonnull
   public SoundEffect[] getSounds() {
      return NO_SOUNDS;
   }

   @Nonnull
   public BlockStateData addEffect(@Nonnull BlockEffect effect) {
      return this;
   }

   @Nonnull
   public BlockStateData clearEffects() {
      return this;
   }

   @Nonnull
   public BlockEffect[] getEffects() {
      return NO_EFFECTS;
   }

   @Nonnull
   public BlockEffect[] getAlwaysOnEffects() {
      return NO_EFFECTS;
   }

   @Nullable
   public SoundEffect getSoundToPlay(@Nonnull Random random) {
      return null;
   }

   public boolean hasSoundsOrEffects() {
      return false;
   }

   public boolean hasAlwaysOnEffects() {
      return false;
   }

   @Nonnull
   public String toString() {
      return "<Default BlockStateData>";
   }
}
