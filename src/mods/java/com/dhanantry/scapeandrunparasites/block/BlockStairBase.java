package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.SRPMain;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.BlockStairs.EnumShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

public class BlockStairBase extends BlockStairs {
   public BlockStairBase(String name, boolean creative, IBlockState modelState) {
      super(modelState);
      this.setRegistryName(name);
      this.setUnlocalizedName("srparasites." + name);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(HALF, EnumHalf.BOTTOM).withProperty(SHAPE, EnumShape.STRAIGHT));
      this.setLightOpacity(255);
      if (creative) {
         this.setCreativeTab(SRPMain.SRP_CREATIVETAB);
      }

   }
}
