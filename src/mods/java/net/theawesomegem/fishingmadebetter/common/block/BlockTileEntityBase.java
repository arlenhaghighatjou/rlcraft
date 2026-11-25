package net.theawesomegem.fishingmadebetter.common.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockTileEntityBase<TE extends TileEntity> extends BlockBase {
   public BlockTileEntityBase(Material material, String blockName) {
      super(material, blockName);
   }

   public abstract Class<TE> getTileEntityClass();

   public TE getTileEntity(IBlockAccess world, BlockPos pos) {
      return (TE)world.getTileEntity(pos);
   }

   public boolean hasTileEntity(IBlockState state) {
      return true;
   }

   @Nullable
   public abstract TE createTileEntity(World var1, IBlockState var2);
}
