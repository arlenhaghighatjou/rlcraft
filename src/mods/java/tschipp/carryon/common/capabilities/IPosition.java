package tschipp.carryon.common.capabilities;

import net.minecraft.util.math.BlockPos;

public interface IPosition {
   BlockPos getPos();

   void setPos(BlockPos var1);

   boolean isBlockActivated();

   void setBlockActivated(boolean var1);
}
