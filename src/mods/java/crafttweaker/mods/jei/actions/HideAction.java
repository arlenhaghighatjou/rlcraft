package crafttweaker.mods.jei.actions;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mods.jei.JEI;
import crafttweaker.mods.jei.JEIAddonPlugin;
import net.minecraft.item.ItemStack;

public class HideAction implements IAction {
   private final IItemStack stack;

   public HideAction(IItemStack stack) {
      this.stack = stack;
   }

   public void apply() {
      if (this.stack == null) {
         CraftTweakerAPI.logError("Cannot hide null item!");
      } else {
         ItemStack IStack = CraftTweakerMC.getItemStack(this.stack);
         JEI.HIDDEN_ITEMS.addAll(JEIAddonPlugin.getSubTypes(IStack));
      }
   }

   public String describe() {
      return "Hiding item in JEI: " + this.stack;
   }
}
