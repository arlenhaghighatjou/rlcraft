package com.dhanantry.scapeandrunparasites.block;

import java.util.Random;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;

public class BlockSlabDouble extends BlockSlabBase {
   public BlockSlabDouble(Material materialIn, String name, float hardness, boolean creative, boolean tickRandom, BlockSlab half) {
      super(materialIn, name, hardness, creative, tickRandom, half);
   }

   public boolean isDouble() {
      return true;
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(this);
   }
}
