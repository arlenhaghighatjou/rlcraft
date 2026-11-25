package com.alcatrazescapee.notreepunching.integration.crafttweaker;

import com.alcatrazescapee.alcatrazcore.inventory.ingredient.IRecipeIngredient;
import com.alcatrazescapee.notreepunching.common.recipe.IRecipeAction;
import com.alcatrazescapee.notreepunching.common.recipe.KnifeRecipe;
import com.alcatrazescapee.notreepunching.common.recipe.ModRecipes;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.oredict.IOreDictEntry;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.IntFunction;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.notreepunching.Knife")
public class CTKnifeRecipe {
   public CTKnifeRecipe() {
   }

   @ZenMethod
   public static void add(IIngredient input, IItemStack... output) {
      ItemStack[] outputStack = (ItemStack[])Arrays.stream(output).map(CraftTweakerPlugin::toStack).toArray((x$0) -> {
         return new ItemStack[x$0];
      });
      final KnifeRecipe recipe;
      if (input instanceof IOreDictEntry) {
         IOreDictEntry ore = (IOreDictEntry)input;
         recipe = new KnifeRecipe(ore.getName(), ore.getAmount(), outputStack);
      } else {
         recipe = new KnifeRecipe(CraftTweakerPlugin.toStack(input), outputStack);
      }

      CraftTweakerAPI.apply(new IAction() {
         public void apply() {
            ModRecipes.addScheduledAction(() -> {
               ModRecipes.KNIFE.add(recipe);
            });
         }

         public String describe() {
            return "Adding knife recipe for " + recipe.getName();
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
               ModRecipes.KNIFE.remove(ingredient);
            });
         }

         public String describe() {
            return "Removing knife recipe for " + ingredient.getName();
         }
      });
   }
}
