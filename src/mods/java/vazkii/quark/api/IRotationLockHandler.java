package vazkii.quark.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IRotationLockHandler {
   IBlockState setRotation(World var1, BlockPos var2, IBlockState var3, EnumFacing var4, boolean var5, boolean var6);
}
