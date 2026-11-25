package org.orecruncher.dsurround.lib.scanner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import org.orecruncher.lib.chunk.IBlockAccessEx;

public abstract class ScanLocus {
   public ScanLocus() {
   }

   @Nonnull
   public abstract IBlockAccessEx getWorld();

   public abstract BlockPos getCenter();

   public int getReference() {
      return this.getWorld().worldReference();
   }

   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof ScanLocus)) {
         return false;
      } else {
         ScanLocus sl = (ScanLocus)o;
         return this.getWorld().getWorld() == sl.getWorld().getWorld() && this.getCenter().equals(sl.getCenter());
      }
   }
}
