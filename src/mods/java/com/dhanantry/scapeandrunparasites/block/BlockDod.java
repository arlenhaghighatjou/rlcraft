package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.entity.tile.TileEntityDod;
import java.util.Random;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDod extends BlockBase implements ITileEntityProvider {
   public BlockDod(String name, float hardness, boolean creative, boolean tickRandom, float resistance) {
      super(Material.SPONGE, name, hardness, creative, tickRandom, resistance);
      this.setDefaultState(this.blockState.getBaseState());
   }

   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      boolean flag = super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
      return flag;
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote) {
         if (worldIn.isAreaLoaded(pos, 3)) {
            ;
         }
      }
   }

   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityDod();
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      worldIn.removeTileEntity(pos);
      super.breakBlock(worldIn, pos, state);
   }
}
