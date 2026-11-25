package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockParasiteMouth extends BlockBase {
   public BlockParasiteMouth(Material material, String name, float hardness, boolean creative, boolean tickRandom) {
      super(material, name, hardness, creative, tickRandom);
      this.setDefaultState(this.blockState.getBaseState());
   }

   public boolean isOpaqueCube(IBlockState state) {
      return false;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   @SideOnly(Side.CLIENT)
   public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }
}
