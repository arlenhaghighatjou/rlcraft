package moe.plushie.armourers_workshop.api.common.painting;

import moe.plushie.armourers_workshop.api.common.skin.cubes.ICubeColour;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IPantableBlock {
   /** @deprecated */
   @Deprecated
   boolean setColour(IBlockAccess var1, BlockPos var2, int var3, EnumFacing var4);

   boolean setColour(IBlockAccess var1, BlockPos var2, byte[] var3, EnumFacing var4);

   int getColour(IBlockAccess var1, BlockPos var2, EnumFacing var3);

   void setPaintType(IBlockAccess var1, BlockPos var2, IPaintType var3, EnumFacing var4);

   IPaintType getPaintType(IBlockAccess var1, BlockPos var2, EnumFacing var3);

   ICubeColour getColour(IBlockAccess var1, BlockPos var2);

   boolean isRemoteOnly(IBlockAccess var1, BlockPos var2, EnumFacing var3);
}
