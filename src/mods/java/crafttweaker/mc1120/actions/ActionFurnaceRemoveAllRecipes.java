package crafttweaker.mc1120.actions;

import crafttweaker.CraftTweakerAPI;
import net.minecraft.item.crafting.FurnaceRecipes;

public class ActionFurnaceRemoveAllRecipes implements IActionFurnaceRemoval {
   public ActionFurnaceRemoveAllRecipes() {
   }

   public void apply() {
      int size = FurnaceRecipes.instance().getSmeltingList().size();
      FurnaceRecipes.instance().getSmeltingList().clear();
      FurnaceRecipes.instance().experienceList.clear();
      CraftTweakerAPI.logInfo(size + " furnace recipes removed for");
   }

   public String describe() {
      return "Removing all furnace recipes";
   }
}
