package crafttweaker.mc1120.actions;

import crafttweaker.IAction;
import net.minecraft.item.ItemStack;

public class ActionSetStackMaxDamage implements IAction {
   private final ItemStack stack;
   private final int damage;

   public ActionSetStackMaxDamage(ItemStack stack, int damage) {
      this.stack = stack;
      this.damage = damage;
   }

   private static void set(ItemStack stack, int damage) {
      if (!stack.isEmpty()) {
         stack.getItem().setMaxDamage(damage);
      }

   }

   public void apply() {
      set(this.stack, this.damage);
   }

   public String describe() {
      return "Setting max damage of  " + this.stack.getDisplayName() + " to " + this.damage;
   }
}
