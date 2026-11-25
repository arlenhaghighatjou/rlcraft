package slimeknights.mantle.multiblock;

import net.minecraft.util.math.BlockPos;

public interface IMasterLogic {
   void notifyChange(IServantLogic var1, BlockPos var2);
}
