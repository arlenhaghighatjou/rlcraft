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
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.oredict.IOreDictEntry;
import forestry.api.fuels.FermenterFuel;
import forestry.api.fuels.FuelManager;
import forestry.api.recipes.IFermenterRecipe;
import forestry.api.recipes.RecipeManagers;
import forestry.factory.recipes.FermenterRecipe;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.forestry.Fermenter")
@ModOnly("forestry")
@ZenRegister
public class Fermenter {
   public static final String name = "Forestry Fermenter";
   public static final String nameFuel = "Forestry Fermenter (Fuel)";

   public Fermenter() {
   }

   @ZenMethod
   public static void addRecipe(ILiquidStack fluidOutput, IItemStack resource, ILiquidStack fluidInput, int fermentationValue, float fluidOutputModifier) {
      ModTweaker.LATE_ADDITIONS.add(new Fermenter.Add(new FermenterRecipe(InputHelper.toStack(resource), fermentationValue, fluidOutputModifier, InputHelper.getFluid(fluidOutput), InputHelper.toFluid(fluidInput))));
   }

   @ZenMethod
   public static void removeRecipe(IIngredient input) {
      ModTweaker.LATE_REMOVALS.add(new Fermenter.Remove(input));
   }

   private static boolean isInOreDictOreIsThatOreDict(IIngredient ingredient, String oredict) {
      if (ingredient instanceof IOreDictEntry) {
         return Objects.equals(oredict, ((IOreDictEntry)ingredient).getName());
      } else {
         if (ingredient instanceof IItemStack) {
            for(IOreDictEntry iOreDictEntry : ((IItemStack)ingredient).getOres()) {
               if (iOreDictEntry.getName().equals(oredict)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   @ZenMethod
   public static void addFuel(IItemStack item, int fermentPerCycle, int burnDuration) {
      ModTweaker.LATE_ADDITIONS.add(new Fermenter.AddFuel(new FermenterFuel(InputHelper.toStack(item), fermentPerCycle, burnDuration)));
   }

   @ZenMethod
   public static void removeFuel(IIngredient fermenterItem) {
      Map<ItemStack, FermenterFuel> fuelItems = new HashMap();

      for(Entry<ItemStack, FermenterFuel> fuelItem : FuelManager.fermenterFuel.entrySet()) {
         if (fuelItem != null && StackHelper.matches(fermenterItem, InputHelper.toIItemStack(((FermenterFuel)fuelItem.getValue()).getItem()))) {
            fuelItems.put(fuelItem.getKey(), fuelItem.getValue());
         }
      }

      if (!fuelItems.isEmpty()) {
         ModTweaker.LATE_REMOVALS.add(new Fermenter.RemoveFuel(fuelItems));
      } else {
         LogHelper.logWarning(String.format("No %s Recipe found for %s. Command ignored!", "Forestry Fermenter", fermenterItem.toString()));
      }

   }

   private static class Add extends BaseAddForestry<IFermenterRecipe> {
      public Add(IFermenterRecipe recipe) {
         super("Forestry Fermenter", RecipeManagers.fermenterManager, recipe);
      }

      public String getRecipeInfo() {
         return ((IFermenterRecipe)this.recipe).getOutput().getName();
      }
   }

   private static class AddFuel extends BaseMapAddition<ItemStack, FermenterFuel> {
      public AddFuel(FermenterFuel fuelEntry) {
         super("Forestry Fermenter (Fuel)", FuelManager.fermenterFuel);
         this.recipes.put(fuelEntry.getItem(), fuelEntry);
      }

      public String getRecipeInfo(Entry<ItemStack, FermenterFuel> fuelEntry) {
         return LogHelper.getStackDescription(fuelEntry.getKey());
      }
   }

   private static class Remove extends BaseRemoveForestry<IFermenterRecipe> {
      private IIngredient input;

      public Remove(IIngredient input) {
         super("Forestry Fermenter", RecipeManagers.fermenterManager);
         this.input = input;
      }

      protected String getRecipeInfo() {
         return LogHelper.getStackDescription(this.input);
      }

      public boolean checkIsRecipe(IFermenterRecipe recipe) {
         if (Fermenter.isInOreDictOreIsThatOreDict(this.input, recipe.getResourceOreName())) {
            return true;
         } else {
            return StackHelper.matches(this.input, InputHelper.toIItemStack(recipe.getResource())) ? true : StackHelper.matches(this.input, InputHelper.toILiquidStack(recipe.getFluidResource()));
         }
      }
   }

   private static class RemoveFuel extends BaseMapRemoval<ItemStack, FermenterFuel> {
      public RemoveFuel(Map<ItemStack, FermenterFuel> recipes) {
         super("Forestry Fermenter (Fuel)", FuelManager.fermenterFuel, recipes);
      }

      public String getRecipeInfo(Entry<ItemStack, FermenterFuel> fuelEntry) {
         return LogHelper.getStackDescription(fuelEntry.getKey());
      }
   }
}
