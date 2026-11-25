package net.silentchaos512.lib.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class BlockMetaSubtypes extends Block {
   private final int subtypeCount;

   public BlockMetaSubtypes(Material blockMaterialIn, int subtypeCount) {
      super(blockMaterialIn);
      this.subtypeCount = subtypeCount;
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   public int getSubtypeCount() {
      return this.subtypeCount;
   }
}
