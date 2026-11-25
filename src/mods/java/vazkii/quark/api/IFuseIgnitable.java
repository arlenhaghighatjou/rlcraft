package vazkii.quark.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IFuseIgnitable {
   void onIgnitedByFuse(IBlockAccess var1, BlockPos var2, IBlockState var3);
}
