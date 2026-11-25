package ivorius.reccomplex.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockGenericSolid extends ItemBlock {
   public ItemBlockGenericSolid(Block block) {
      super(block);
      this.setMaxDamage(0);
      this.setHasSubtypes(true);
   }

   public int getMetadata(int damage) {
      return damage;
   }

   public String getUnlocalizedName(ItemStack stack) {
      return super.getUnlocalizedName(stack) + ".meta." + stack.getItemDamage();
   }
}
