package crafttweaker.mc1120.recipes;

import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.recipes.ICraftingRecipe;
import crafttweaker.api.recipes.IMTRecipe;
import crafttweaker.api.recipes.ShapedRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

/** @deprecated */
@Deprecated
public class ShapedRecipeAdvanced extends Impl<IRecipe> implements IRecipe, IMTRecipe {
   private final ShapedRecipe recipe;

   public ShapedRecipeAdvanced(ShapedRecipe recipe) {
      this.recipe = recipe;
   }

   public boolean matches(InventoryCrafting inventory, World world) {
      return this.recipe.matches(MCCraftingInventory.get(inventory));
   }

   public ItemStack getCraftingResult(InventoryCrafting inventory) {
      return CraftTweakerMC.getItemStack(this.recipe.getCraftingResult(MCCraftingInventory.get(inventory))).copy();
   }

   public boolean canFit(int x, int y) {
      return x >= this.recipe.getWidth() && y >= this.recipe.getHeight();
   }

   public ItemStack getRecipeOutput() {
      return CraftTweakerMC.getItemStack(this.recipe.getOutput());
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
      return NonNullList.create();
   }

   public ICraftingRecipe getRecipe() {
      return this.recipe;
   }
}
