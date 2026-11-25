package knightminer.inspirations.tools.item;

import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;

public class ItemEnchantableShield extends ItemShield {
   public ItemEnchantableShield() {
      this.setUnlocalizedName("shield");
   }

   public int getItemEnchantability(ItemStack stack) {
      return stack.getSubCompound("BlockEntityTag") != null ? 16 : 12;
   }
}
