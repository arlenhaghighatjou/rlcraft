package vazkii.quark.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface INonSticky {
   boolean canStickToBlock(World var1, BlockPos var2, BlockPos var3, BlockPos var4, IBlockState var5, IBlockState var6, EnumFacing var7);
}
