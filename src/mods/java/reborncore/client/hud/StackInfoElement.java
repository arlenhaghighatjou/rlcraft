package reborncore.client.hud;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class StackInfoElement {
   public boolean isStack;
   public ItemStack stack;
   public Item item;

   public StackInfoElement(ItemStack stack) {
      this.stack = stack;
      this.isStack = true;
   }

   public StackInfoElement(Item item) {
      this.item = item;
      this.isStack = false;
   }

   public abstract String getText(ItemStack var1);
}
