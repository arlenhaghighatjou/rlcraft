package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;

public class BlockSlabHalf extends BlockSlabBase {
   public BlockSlabHalf(Material materialIn, String name, float hardness, boolean creative, boolean tickRandom, BlockSlab half, BlockSlab doubleSlab) {
      super(materialIn, name, hardness, creative, tickRandom, half);
   }

   public boolean isDouble() {
      return false;
   }
}
