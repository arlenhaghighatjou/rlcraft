package crafttweaker.mc1120.furnace;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.recipes.FurnaceRecipe;
import crafttweaker.api.recipes.IFurnaceManager;
import crafttweaker.api.recipes.IFurnaceRecipe;
import crafttweaker.mc1120.actions.ActionAddFurnaceRecipe;
import crafttweaker.mc1120.actions.ActionFurnaceRemoveAllRecipes;
import crafttweaker.mc1120.actions.ActionFurnaceRemoveRecipe;
import crafttweaker.mc1120.actions.ActionSetFuel;
import crafttweaker.mc1120.actions.IActionFurnaceRemoval;
import crafttweaker.mc1120.item.MCItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import stanhebben.zenscript.annotations.Optional;

public class MCFurnaceManager implements IFurnaceManager {
   public static final Map<IItemStack, Integer> fuelMap = new HashMap();
   public static List<ActionAddFurnaceRecipe> recipesToAdd = new ArrayList();
   public static List<IActionFurnaceRemoval> recipesToRemove = new ArrayList();

   public MCFurnaceManager() {
   }

   public void remove(IIngredient output, @Optional IIngredient input) {
      if (output == null) {
         throw new IllegalArgumentException("output cannot be null");
      } else {
         recipesToRemove.add(new ActionFurnaceRemoveRecipe(output, input));
      }
   }

   public void removeAll() {
      recipesToRemove.add(new ActionFurnaceRemoveAllRecipes());
   }

   public void addRecipe(IItemStack output, IIngredient input, @Optional double xp) {
      List<IItemStack> items = input.getItems();
      if (items == null) {
         CraftTweakerAPI.logError("Cannot turn " + input.toString() + " into a furnace recipe");
      } else {
         ItemStack[] items2 = CraftTweakerMC.getItemStacks(items);
         ItemStack output2 = CraftTweakerMC.getItemStack(output);
         recipesToAdd.add(new ActionAddFurnaceRecipe(input, items2, output2, xp));
      }
   }

   public void setFuel(IIngredient item, int fuel) {
      CraftTweakerAPI.apply(new ActionSetFuel(new SetFuelPattern(item, fuel)));
   }

   public int getFuel(IItemStack item) {
      return TileEntityFurnace.getItemBurnTime(CraftTweakerMC.getItemStack(item));
   }

   public IItemStack getSmeltingResult(IItemStack stack) {
      return CraftTweakerMC.getIItemStack(FurnaceRecipes.instance().getSmeltingResult(CraftTweakerMC.getItemStack(stack)));
   }

   public List<IFurnaceRecipe> getAll() {
      return (List)FurnaceRecipes.instance().getSmeltingList().entrySet().stream().filter((ent) -> {
         if (!((ItemStack)ent.getValue()).isEmpty() && !((ItemStack)ent.getKey()).isEmpty()) {
            return true;
         } else {
            CraftTweakerAPI.logWarning("Furnace recipe from " + ent.getKey() + " to " + ent.getValue() + " has a empty stack.");
            return false;
         }
      }).map((ent) -> {
         return new FurnaceRecipe(MCItemStack.createNonCopy((ItemStack)ent.getKey()), MCItemStack.createNonCopy((ItemStack)ent.getValue()), FurnaceRecipes.instance().getSmeltingExperience((ItemStack)ent.getValue()));
      }).collect(Collectors.toList());
   }
}
