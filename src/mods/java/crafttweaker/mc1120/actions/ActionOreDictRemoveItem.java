package crafttweaker.mc1120.actions;

import crafttweaker.IAction;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ActionOreDictRemoveItem implements IAction {
   private final String id;
   private final ItemStack item;

   public ActionOreDictRemoveItem(String id, ItemStack item) {
      this.id = id;
      this.item = item;
   }

   public void apply() {
      OreDictionary.getOres(this.id, false).remove(this.item);
   }

   public String describe() {
      return "Removing " + this.item.getDisplayName() + " from ore dictionary entry " + this.id;
   }
}
