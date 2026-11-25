package crafttweaker.mc1120.recipes;

import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.recipes.ICraftingRecipe;
import crafttweaker.api.recipes.IMTRecipe;
import crafttweaker.api.recipes.ShapelessRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

/** @deprecated */
@Deprecated
public class ShapelessRecipeBasic extends ShapelessRecipes implements IMTRecipe {
   private final ShapelessRecipe recipe;

   public ShapelessRecipeBasic(String name, NonNullList<Ingredient> ingredients, ShapelessRecipe recipe) {
      super(name, CraftTweakerMC.getItemStack(recipe.getOutput()), ingredients);
      this.recipe = recipe;
   }

   public boolean matches(InventoryCrafting inventory, World world) {
      return this.recipe.matches(MCCraftingInventory.get(inventory));
   }

   public ItemStack getCraftingResult(InventoryCrafting inventory) {
      return this.recipe.getCraftingResult(MCCraftingInventory.get(inventory)) != null ? CraftTweakerMC.getItemStack(this.recipe.getCraftingResult(MCCraftingInventory.get(inventory))).copy() : ItemStack.EMPTY;
   }

   public ICraftingRecipe getRecipe() {
      return this.recipe;
   }
}
