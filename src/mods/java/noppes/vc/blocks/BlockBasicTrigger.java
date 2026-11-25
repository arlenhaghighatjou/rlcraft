package noppes.vc.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noppes.vc.blocks.tiles.TileBasicRotation;

public abstract class BlockBasicTrigger extends BlockBasicRotated {
   protected BlockBasicTrigger(Block block) {
      super(block);
   }

   public boolean canProvidePower(IBlockState state) {
      return true;
   }

   public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
      return this.getWeakPower(state, world, pos, side);
   }

   public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
      TileBasicRotation tile = (TileBasicRotation)world.getTileEntity(pos);
      return tile != null ? tile.powerProvided() : 0;
   }

   public void updateSurrounding(World par1World, BlockPos pos) {
      par1World.notifyNeighborsOfStateChange(pos, this, false);
      par1World.notifyNeighborsOfStateChange(pos.down(), this, false);
      par1World.notifyNeighborsOfStateChange(pos.up(), this, false);
      par1World.notifyNeighborsOfStateChange(pos.west(), this, false);
      par1World.notifyNeighborsOfStateChange(pos.east(), this, false);
      par1World.notifyNeighborsOfStateChange(pos.south(), this, false);
      par1World.notifyNeighborsOfStateChange(pos.north(), this, false);
   }
}
