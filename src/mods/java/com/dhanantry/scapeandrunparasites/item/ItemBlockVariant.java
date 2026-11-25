package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockVariant extends ItemBlock {
   private Enum[] variants;

   public ItemBlockVariant(Block block) {
      super(block);
      this.setHasSubtypes(true);
      this.setMaxDamage(0);
      this.variants = ((IMetaName)block).getVariants();
   }

   public String getUnlocalizedName(ItemStack stack) {
      return stack.getMetadata() > this.variants.length - 1 ? super.getUnlocalizedName() + "_" + this.variants[stack.getMetadata() & this.variants.length - 1] : super.getUnlocalizedName() + "_" + this.variants[stack.getMetadata()];
   }

   public int getMetadata(int damage) {
      return damage;
   }
}
