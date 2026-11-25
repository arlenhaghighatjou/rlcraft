package crafttweaker.mc1120.actions;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class ActionFurnaceRemoveRecipe implements IActionFurnaceRemoval {
   private IIngredient output;
   private IIngredient input;
   Map<ItemStack, ItemStack> smeltingMap = new HashMap();

   public ActionFurnaceRemoveRecipe(IIngredient output, IIngredient input) {
      this.output = output;
      this.input = input;
   }

   public void apply() {
      Map<ItemStack, ItemStack> smeltingList = FurnaceRecipes.instance().getSmeltingList();
      if (this.input == null) {
         for(Entry<ItemStack, ItemStack> entry : smeltingList.entrySet()) {
            if (this.output.matches(CraftTweakerMC.getIItemStackForMatching((ItemStack)entry.getValue()))) {
               this.smeltingMap.put(entry.getKey(), entry.getValue());
            }
         }
      } else {
         for(Entry<ItemStack, ItemStack> entry : smeltingList.entrySet()) {
            if (this.output.matches(CraftTweakerMC.getIItemStackForMatching((ItemStack)entry.getValue())) && this.input.matches(CraftTweakerMC.getIItemStackForMatching((ItemStack)entry.getKey()))) {
               this.smeltingMap.put(entry.getKey(), entry.getValue());
            }
         }
      }

      for(Entry<ItemStack, ItemStack> entry : this.smeltingMap.entrySet()) {
         FurnaceRecipes.instance().getSmeltingList().remove(entry.getKey(), entry.getValue());
         FurnaceRecipes.instance().experienceList.keySet().removeIf((itemStack) -> {
            return this.output.matches(CraftTweakerMC.getIItemStackForMatching(itemStack));
         });
      }

      CraftTweakerAPI.logInfo(this.smeltingMap.size() + " recipes removed for: " + this.output);
   }

   public String describe() {
      return "Removing furnace recipes for: " + this.output;
   }
}
