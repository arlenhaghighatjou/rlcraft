package com.endertech.minecraft.forge.items;

import com.endertech.minecraft.forge.blocks.ForgeBlock;
import net.minecraft.item.ItemBlock;

public class ForgeBlockItem extends ItemBlock {
   public ForgeBlockItem(ForgeBlock block) {
      super(block);
      this.setHasSubtypes(block.hasVariants());
   }

   public int getMetadata(int damage) {
      return this.getHasSubtypes() ? damage : 0;
   }
}
