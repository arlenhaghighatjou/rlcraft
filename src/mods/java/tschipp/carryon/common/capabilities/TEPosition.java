package tschipp.carryon.common.capabilities;

import net.minecraft.util.math.BlockPos;

public class TEPosition implements IPosition {
   private BlockPos pos = new BlockPos(0, 0, 0);
   private boolean blockActivated = false;

   public TEPosition() {
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public void setPos(BlockPos pos) {
      this.pos = pos;
   }

   public boolean isBlockActivated() {
      return this.blockActivated;
   }

   public void setBlockActivated(boolean b) {
      this.blockActivated = b;
   }
}
