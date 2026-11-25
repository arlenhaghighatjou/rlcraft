package org.orecruncher.dsurround.registry.acoustics;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IOptions {
   default long getDelayMin() {
      return 0L;
   }

   default long getDelayMax() {
      return 0L;
   }

   default boolean isDelayedSound() {
      return this.getDelayMin() > 0L && this.getDelayMax() > 0L;
   }

   default float getGlidingVolume() {
      return 0.0F;
   }

   default float getGlidingPitch() {
      return 0.0F;
   }

   default float getVolumeScale() {
      return 1.0F;
   }

   default float getPitchScale() {
      return 1.0F;
   }
}
