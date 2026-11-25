package slimeknights.mantle.multiblock;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IServantLogic {
   BlockPos getMasterPosition();

   void notifyMasterOfChange();

   boolean setPotentialMaster(IMasterLogic var1, World var2, BlockPos var3);

   boolean verifyMaster(IMasterLogic var1, World var2, BlockPos var3);

   void invalidateMaster(IMasterLogic var1, World var2, BlockPos var3);
}
