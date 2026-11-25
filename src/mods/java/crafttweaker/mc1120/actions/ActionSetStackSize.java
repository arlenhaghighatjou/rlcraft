package crafttweaker.mc1120.actions;

import crafttweaker.IAction;
import net.minecraft.item.ItemStack;

public class ActionSetStackSize implements IAction {
   private final ItemStack stack;
   private final int size;

   public ActionSetStackSize(ItemStack stack, int size) {
      this.stack = stack;
      this.size = size;
   }

   private static void set(ItemStack stack, int size) {
      if (!stack.isEmpty()) {
         stack.getItem().setMaxStackSize(size);
      }

   }

   public void apply() {
      set(this.stack, this.size);
   }

   public String describe() {
      return "Setting max stack size of  " + this.stack.getDisplayName() + " to " + this.size;
   }
}
