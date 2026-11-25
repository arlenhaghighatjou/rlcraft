package com.teamacronymcoders.base.blocks;

import com.teamacronymcoders.base.items.itemblocks.ItemSubBlock;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class BlockSubBase extends BlockBase {
   private String[] names;

   public BlockSubBase(Material mat, String[] names) {
      super(mat, "");
      this.names = names;
      this.setItemBlock(new ItemSubBlock(this, names));
   }

   public List<ItemStack> getAllSubItems(List<ItemStack> itemStacks) {
      for(int x = 0; x < this.names.length; ++x) {
         itemStacks.add(new ItemStack(this, 1, x));
      }

      return itemStacks;
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }
}
