package crafttweaker.mods.jei.actions;

import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mods.jei.JEIAddonPlugin;
import java.util.Collections;
import mezz.jei.api.ingredients.VanillaTypes;

public class AddItemAction implements IAction {
   private final IItemStack stack;

   public AddItemAction(IItemStack stack) {
      this.stack = stack;
   }

   public void apply() {
      JEIAddonPlugin.itemRegistry.addIngredientsAtRuntime(VanillaTypes.ITEM, Collections.singletonList(CraftTweakerMC.getItemStack(this.stack)));
   }

   public String describe() {
      return String.format("Adding %s to JEI", this.stack);
   }
}
