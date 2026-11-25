package c4.corpsecomplex.common.modules.spawning.capability;

import net.minecraft.util.math.BlockPos;

public interface IDeathLocation {
   BlockPos getDeathLocation();

   void setDeathLocation(BlockPos var1);

   boolean hasDeathLocation();

   void setHasDeathLocation(boolean var1);

   int getDeathDimension();

   void setDeathDimension(int var1);

   boolean hasUsedScroll();

   void setUsedScroll(boolean var1);
}
