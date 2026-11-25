package org.orecruncher.dsurround.client.sound;

import javax.annotation.Nonnull;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PositionedEmitter extends Emitter {
   private final BlockPos position;

   public PositionedEmitter(@Nonnull SoundEffect sound, @Nonnull BlockPos pos) {
      super(sound);
      this.position = pos.toImmutable();
      this.activeSound = this.createSound();
      this.activeSound.canQueue = true;
   }

   protected SoundInstance createSound() {
      return this.effect.createSoundAt(this.position).setRepeat(true);
   }
}
