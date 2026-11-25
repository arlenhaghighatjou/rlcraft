package primetoxinz.coralreef;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockMeta extends ItemBlock {
   public ItemBlockMeta(Block block) {
      super(block);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   public int getMetadata(int meta) {
      return meta;
   }

   public String getUnlocalizedName(ItemStack stack) {
      return super.getUnlocalizedName() + "." + stack.getMetadata();
   }
}
