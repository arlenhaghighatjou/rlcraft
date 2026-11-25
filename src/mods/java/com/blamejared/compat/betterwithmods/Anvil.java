package com.blamejared.compat.betterwithmods;

import betterwithmods.common.registry.anvil.AnvilCraftingManager;
import betterwithmods.common.registry.anvil.ShapedAnvilRecipe;
import betterwithmods.common.registry.anvil.ShapelessAnvilRecipe;
import betterwithmods.module.gameplay.AnvilRecipes;
import com.blamejared.ModTweaker;
import com.blamejared.mtlib.helpers.InputHelper;
import com.blamejared.mtlib.utils.BaseAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.betterwithmods.Anvil")
@ModOnly("betterwithmods")
@ZenRegister
public class Anvil {
   public Anvil() {
   }

   @ZenMethod
   public static void addShaped(IItemStack output, IIngredient[][] inputs) {
      ModTweaker.LATE_ADDITIONS.add(new Anvil.AddShaped(output, inputs));
   }

   @ZenMethod
   public static void addShapedFixed(IItemStack output, IIngredient[][] inputs) {
      ModTweaker.LATE_ADDITIONS.add(new Anvil.AddShapedFixed(output, inputs));
   }

   @ZenMethod
   public static void addShapeless(IItemStack output, IIngredient[] inputs) {
      ModTweaker.LATE_ADDITIONS.add(new Anvil.AddShapeless(output, inputs));
   }

   @ZenMethod
   public static void removeShaped(IItemStack output, @Optional IIngredient[][] ingredients) {
      ModTweaker.LATE_REMOVALS.add(new Anvil.RemoveShaped(output, ingredients));
   }

   @ZenMethod
   public static void removeShapedFixed(IItemStack output, @Optional IIngredient[][] ingredients) {
      ModTweaker.LATE_REMOVALS.add(new Anvil.RemoveShapedFixed(output, ingredients));
   }

   @ZenMethod
   public static void removeShapeless(IItemStack output, @Optional IIngredient[] ingredients) {
      ModTweaker.LATE_REMOVALS.add(new Anvil.RemoveShapeless(output, ingredients));
   }

   @ZenMethod
   public static void removeAll() {
      ModTweaker.LATE_REMOVALS.add(new Anvil.RemoveAll());
   }

   public static Object[] toShapedAnvilObjects(IIngredient[][] ingredients) {
      if (ingredients == null) {
         return null;
      } else {
         ArrayList<Object> prep = new ArrayList();
         char chr = 'a';

         for(int y = 0; y < 4; ++y) {
            StringBuilder matrix = new StringBuilder();

            for(int x = 0; x < 4; ++x) {
               if (x < ingredients.length && ingredients[x] != null && y < ingredients[x].length) {
                  if (ingredients[x][y] != null) {
                     prep.add(Character.valueOf(chr));
                     prep.add(InputHelper.toObject(ingredients[x][y]));
                     matrix.append(chr);
                     ++chr;
                  } else {
                     matrix.append(' ');
                  }
               }
            }

            if (matrix.length() > 0) {
               prep.add(y, matrix.toString());
            }
         }

         return prep.toArray();
      }
   }

   public static Object[] toShapedAnvilObjectsFixed(IIngredient[][] ingredients) {
      if (ingredients == null) {
         return null;
      } else {
         ArrayList<Object> prep = new ArrayList();
         char chr = 'a';

         for(int x = 0; x < 4; ++x) {
            StringBuilder matrix = new StringBuilder();

            for(int y = 0; y < 4; ++y) {
               if (x < ingredients.length && ingredients[x] != null && y < ingredients[x].length) {
                  if (ingredients[x][y] != null) {
                     prep.add(Character.valueOf(chr));
                     prep.add(InputHelper.toObject(ingredients[x][y]));
                     matrix.append(chr);
                     ++chr;
                  } else {
                     matrix.append(' ');
                  }
               }
            }

            if (matrix.length() > 0) {
               prep.add(x, matrix.toString());
            }
         }

         return prep.toArray();
      }
   }

   public static class AddShaped extends BaseAction {
      protected final IItemStack output;
      protected final IIngredient[][] ingredients;

      public AddShaped(IItemStack output, IIngredient[][] ingredients) {
         super("Add Anvil Shaped Recipe");
         this.output = output;
         this.ingredients = ingredients;
      }

      public void apply() {
         AnvilRecipes.addSteelShapedRecipe(new ResourceLocation("crafttweaker", this.name), InputHelper.toStack(this.output), Anvil.toShapedAnvilObjects(this.ingredients));
      }

      protected String getRecipeInfo() {
         return this.output.getDisplayName();
      }
   }

   public static class AddShapedFixed extends Anvil.AddShaped {
      public AddShapedFixed(IItemStack output, IIngredient[][] ingredients) {
         super(output, ingredients);
      }

      public void apply() {
         AnvilRecipes.addSteelShapedRecipe(new ResourceLocation("crafttweaker", this.name), InputHelper.toStack(this.output), Anvil.toShapedAnvilObjectsFixed(this.ingredients));
      }
   }

   public static class AddShapeless extends BaseAction {
      private final IItemStack output;
      private final IIngredient[] ingredients;

      public AddShapeless(IItemStack output, IIngredient[] ingredients) {
         super("Add Anvil Shapeless Recipe");
         this.output = output;
         this.ingredients = ingredients;
      }

      public void apply() {
         AnvilRecipes.addSteelShapelessRecipe(new ResourceLocation("crafttweaker", this.name), InputHelper.toStack(this.output), InputHelper.toObjects(this.ingredients));
      }

      protected String getRecipeInfo() {
         return this.output.getDisplayName();
      }
   }

   public static class RemoveAll extends BaseAction {
      protected RemoveAll() {
         super("Remove All Anvil");
      }

      public void apply() {
         AnvilCraftingManager.ANVIL_CRAFTING.clear();
      }

      protected String getRecipeInfo() {
         return "all";
      }
   }

   public static class RemoveShaped extends BaseAction {
      protected final IItemStack output;
      protected final IIngredient[][] ingredients;

      protected RemoveShaped(IItemStack output, IIngredient[][] ingredients) {
         super("Remove Shaped Anvil");
         this.output = output;
         this.ingredients = ingredients;
      }

      public void apply() {
         if (this.ingredients != null) {
            IRecipe removal = new ShapedAnvilRecipe(new ResourceLocation("crafttweaker", this.name), InputHelper.toStack(this.output), Anvil.toShapedAnvilObjects(this.ingredients));
            Iterator<IRecipe> iterator = AnvilCraftingManager.ANVIL_CRAFTING.iterator();

            while(iterator.hasNext()) {
               IRecipe recipe = (IRecipe)iterator.next();
               if (recipe.getRecipeOutput().isItemEqual(removal.getRecipeOutput()) && removal.getIngredients().equals(recipe.getIngredients())) {
                  iterator.remove();
               }
            }
         } else {
            Iterator<IRecipe> iterator = AnvilCraftingManager.ANVIL_CRAFTING.iterator();

            while(iterator.hasNext()) {
               IRecipe recipe = (IRecipe)iterator.next();
               if (recipe.getRecipeOutput().isItemEqual(InputHelper.toStack(this.output))) {
                  iterator.remove();
               }
            }
         }

      }

      protected String getRecipeInfo() {
         return this.output.getDisplayName();
      }
   }

   public static class RemoveShapedFixed extends Anvil.RemoveShaped {
      protected RemoveShapedFixed(IItemStack output, IIngredient[][] ingredients) {
         super(output, ingredients);
      }

      public void apply() {
         if (this.ingredients != null) {
            IRecipe removal = new ShapedAnvilRecipe(new ResourceLocation("crafttweaker", this.name), InputHelper.toStack(this.output), Anvil.toShapedAnvilObjectsFixed(this.ingredients));
            Iterator<IRecipe> iterator = AnvilCraftingManager.ANVIL_CRAFTING.iterator();

            while(iterator.hasNext()) {
               IRecipe recipe = (IRecipe)iterator.next();
               if (recipe.getRecipeOutput().isItemEqual(removal.getRecipeOutput()) && removal.getIngredients().equals(recipe.getIngredients())) {
                  iterator.remove();
               }
            }
         } else {
            Iterator<IRecipe> iterator = AnvilCraftingManager.ANVIL_CRAFTING.iterator();

            while(iterator.hasNext()) {
               IRecipe recipe = (IRecipe)iterator.next();
               if (recipe.getRecipeOutput().isItemEqual(InputHelper.toStack(this.output))) {
                  iterator.remove();
               }
            }
         }

      }
   }

   public static class RemoveShapeless extends BaseAction {
      private final IItemStack output;
      private final IIngredient[] ingredients;

      protected RemoveShapeless(IItemStack output, IIngredient[] ingredients) {
         super("Remove Shapeless Anvil");
         this.output = output;
         this.ingredients = ingredients;
      }

      public void apply() {
         if (this.ingredients != null) {
            IRecipe removal = new ShapelessAnvilRecipe(new ResourceLocation("crafttweaker", this.name), InputHelper.toStack(this.output), InputHelper.toObjects(this.ingredients));
            Iterator<IRecipe> iterator = AnvilCraftingManager.ANVIL_CRAFTING.iterator();

            while(iterator.hasNext()) {
               IRecipe recipe = (IRecipe)iterator.next();
               if (recipe.getRecipeOutput().isItemEqual(removal.getRecipeOutput()) && removal.getIngredients().equals(recipe.getIngredients())) {
                  iterator.remove();
               }
            }
         } else {
            Iterator<IRecipe> iterator = AnvilCraftingManager.ANVIL_CRAFTING.iterator();

            while(iterator.hasNext()) {
               IRecipe recipe = (IRecipe)iterator.next();
               if (recipe.getRecipeOutput().isItemEqual(InputHelper.toStack(this.output))) {
                  iterator.remove();
               }
            }
         }

      }

      protected String getRecipeInfo() {
         return this.output.getDisplayName();
      }
   }
}
