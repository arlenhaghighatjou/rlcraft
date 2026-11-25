package net.silentchaos512.lib.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.silentchaos512.lib.block.BlockMetaSubtypes;

public class ItemBlockMetaSubtypes extends ItemBlock {
   private final int subtypeCount;

   public ItemBlockMetaSubtypes(BlockMetaSubtypes block) {
      this(block, block.getSubtypeCount());
   }

   public ItemBlockMetaSubtypes(Block block, int subtypeCount) {
      super(block);
      this.subtypeCount = subtypeCount;
      this.setMaxDamage(0);
      this.setHasSubtypes(subtypeCount > 1);
   }

   public int getMetadata(int damage) {
      return damage & 15;
   }

   public String getUnlocalizedName(ItemStack stack) {
      return super.getUnlocalizedName() + this.getMetadata(stack.getItemDamage());
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         for(int i = 0; i < this.subtypeCount; ++i) {
            items.add(new ItemStack(this, 1, i));
         }

      }
   }
}
