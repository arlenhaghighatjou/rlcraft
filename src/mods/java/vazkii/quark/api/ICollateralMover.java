package vazkii.quark.api;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ICollateralMover {
   default boolean isCollateralMover(World world, BlockPos source, EnumFacing moveDirection, BlockPos pos) {
      return true;
   }

   ICollateralMover.MoveResult getCollateralMovement(World var1, BlockPos var2, EnumFacing var3, EnumFacing var4, BlockPos var5);

   public static enum MoveResult {
      MOVE,
      BREAK,
      SKIP,
      PREVENT;

      private MoveResult() {
      }
   }
}
