package com.blamejared.compat.forestry;

import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.helpers.LogHelper;
import com.blamejared.mtlib.helpers.StackHelper;
import com.blamejared.mtlib.utils.BaseAddForestry;
import com.blamejared.mtlib.utils.BaseMapAddition;
import com.blamejared.mtlib.utils.BaseMapRemoval;
import com.blamejared.mtlib.utils.BaseRemoveForestry;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import forestry.api.fuels.FuelManager;
import forestry.api.fuels.MoistenerFuel;
import forestry.api.recipes.IMoistenerRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.factory.recipes.MoistenerRecipe;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.forestry.Moistener")
@ModOnly("forestry")
@ZenRegister
public class Moistener {
   public static final String name = "Forestry Moistener";
   public static final String nameFuel = "Forestry Moistener (Fuel)";

   public Moistener() {
   }

   @ZenMethod
   public static void addRecipe(IItemStack output, IItemStack input, int packagingTime) {
      ModTweaker.LATE_ADDITIONS.add(new Moistener.Add(new MoistenerRecipe(InputHelper.toStack(input), InputHelper.toStack(output), packagingTime)));
   }

   @ZenMethod
   public static void removeRecipe(IIngredient output) {
      ModTweaker.LATE_REMOVALS.add(new Moistener.Remove(output));
   }

   @ZenMethod
   public static void addFuel(IItemStack item, IItemStack product, int moistenerValue, int stage) {
      if (stage >= 0) {
         ModTweaker.LATE_ADDITIONS.add(new Moistener.AddFuel(new MoistenerFuel(InputHelper.toStack(item), InputHelper.toStack(product), stage, moistenerValue)));
      } else {
         LogHelper.logWarning(String.format("No %s Recipe add for %s. Stage parameter must positive!", "Forestry Moistener", item.toString()));
      }

   }

   @ZenMethod
   public static void removeFuel(IIngredient moistenerItem) {
      Map<ItemStack, MoistenerFuel> fuelItems = new HashMap();

      for(Entry<ItemStack, MoistenerFuel> fuelItem : FuelManager.moistenerResource.entrySet()) {
         if (fuelItem != null && StackHelper.matches(moistenerItem, InputHelper.toIItemStack(((MoistenerFuel)fuelItem.getValue()).getItem()))) {
            fuelItems.put(fuelItem.getKey(), fuelItem.getValue());
         }
      }

      if (!fuelItems.isEmpty()) {
         ModTweaker.LATE_REMOVALS.add(new Moistener.RemoveFuel(fuelItems));
      } else {
         LogHelper.logWarning(String.format("No %s Recipe found for %s. Command ignored!", "Forestry Moistener", moistenerItem.toString()));
      }

   }

   private static class Add extends BaseAddForestry<IMoistenerRecipe> {
      public Add(IMoistenerRecipe recipe) {
         super("Forestry Moistener", RecipeManagers.moistenerManager, recipe);
      }

      public String getRecipeInfo() {
         return LogHelper.getStackDescription(((IMoistenerRecipe)this.recipe).getProduct());
      }
   }

   private static class AddFuel extends BaseMapAddition<ItemStack, MoistenerFuel> {
      public AddFuel(MoistenerFuel fuelEntry) {
         super("Forestry Moistener (Fuel)", FuelManager.moistenerResource);
         this.recipes.put(fuelEntry.getItem(), fuelEntry);
      }

      public String getRecipeInfo(Entry<ItemStack, MoistenerFuel> fuelEntry) {
         return LogHelper.getStackDescription(fuelEntry.getKey());
      }
   }

   private static class Remove extends BaseRemoveForestry<IMoistenerRecipe> {
      private IIngredient output;

      public Remove(IIngredient output) {
         super("Forestry Moistener", RecipeManagers.moistenerManager);
         this.output = output;
      }

      public String getRecipeInfo() {
         return this.output.toString();
      }

      public boolean checkIsRecipe(IMoistenerRecipe recipe) {
         return recipe != null && StackHelper.matches(this.output, InputHelper.toIItemStack(recipe.getProduct()));
      }
   }

   private static class RemoveFuel extends BaseMapRemoval<ItemStack, MoistenerFuel> {
      public RemoveFuel(Map<ItemStack, MoistenerFuel> recipes) {
         super("Forestry Moistener (Fuel)", FuelManager.moistenerResource, recipes);
      }

      public String getRecipeInfo(Entry<ItemStack, MoistenerFuel> fuelEntry) {
         return LogHelper.getStackDescription(fuelEntry.getKey());
      }
   }
}
