package lykrast.defiledlands.common.item;

import lykrast.defiledlands.common.block.BlockVariant;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMetadata extends ItemBlock {
   private Enum[] variants;
   private boolean differentNames;

   public ItemBlockMetadata(Block block) {
      this(block, false);
   }

   public ItemBlockMetadata(Block block, boolean names) {
      super(block);
      this.differentNames = false;
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
      if (names && block instanceof BlockVariant) {
         this.differentNames = true;
         this.variants = ((BlockVariant)block).getVariants();
      }

   }

   public int getMetadata(int meta) {
      return meta;
   }

   public String getUnlocalizedName(ItemStack itemStack) {
      return this.differentNames ? super.getUnlocalizedName(itemStack) + "." + this.variants[itemStack.getMetadata()] : super.getUnlocalizedName(itemStack);
   }
}
