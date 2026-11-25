package com.alcatrazescapee.notreepunching.integration.crafttweaker;

import com.alcatrazescapee.alcatrazcore.inventory.ingredient.IRecipeIngredient;
import com.alcatrazescapee.notreepunching.common.recipe.FirePitRecipe;
import com.alcatrazescapee.notreepunching.common.recipe.IRecipeAction;
import com.alcatrazescapee.notreepunching.common.recipe.ModRecipes;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.notreepunching.FirePit")
public class CTFirePitRecipe {
   public CTFirePitRecipe() {
   }

   @ZenMethod
   public static void add(IIngredient input, IItemStack output) {
      ItemStack outputStack = CraftTweakerPlugin.toStack(output);
      final FirePitRecipe recipe;
      if (input instanceof IOreDictEntry) {
         IOreDictEntry ore = (IOreDictEntry)input;
         recipe = new FirePitRecipe(outputStack, ore.getName(), ore.getAmount());
      } else {
         recipe = new FirePitRecipe(outputStack, CraftTweakerPlugin.toStack(input));
      }

      CraftTweakerAPI.apply(new IAction() {
         public void apply() {
            ModRecipes.addScheduledAction(() -> {
               ModRecipes.FIRE_PIT.add(recipe);
            });
         }

         public String describe() {
            return "Adding Fire pit recipe for " + recipe.getName();
         }
      });
   }

   @ZenMethod
   public static void remove(IIngredient input) {
      final IRecipeIngredient ingredient;
      if (input instanceof IOreDictEntry) {
         IOreDictEntry ore = (IOreDictEntry)input;
         ingredient = IRecipeIngredient.of(ore.getName(), ore.getAmount());
      } else {
         ingredient = IRecipeIngredient.of(CraftTweakerPlugin.toStack(input));
      }

      CraftTweakerAPI.apply(new IAction() {
         public void apply() {
            ModRecipes.addScheduledAction(() -> {
               ModRecipes.FIRE_PIT.remove(ingredient);
            });
         }

         public String describe() {
            return "Removing Fire pit recipe for " + ingredient.getName();
         }
      });
   }
}
