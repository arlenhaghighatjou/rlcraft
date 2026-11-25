package com.mujmajnkraft.bettersurvival.recipe;

import javax.annotation.Nullable;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class BlankRecipe implements IRecipe {
   IRecipe oldRecipe;

   public BlankRecipe(IRecipe oldRecipe) {
      this.oldRecipe = oldRecipe;
   }

   public boolean matches(InventoryCrafting inv, World worldIn) {
      return false;
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      return ItemStack.EMPTY;
   }

   public boolean canFit(int width, int height) {
      return false;
   }

   public ItemStack getRecipeOutput() {
      return ItemStack.EMPTY;
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
      return NonNullList.create();
   }

   public IRecipe setRegistryName(ResourceLocation name) {
      return (IRecipe)this.oldRecipe.setRegistryName(name);
   }

   @Nullable
   public ResourceLocation getRegistryName() {
      return this.oldRecipe.getRegistryName();
   }

   public Class<IRecipe> getRegistryType() {
      return this.oldRecipe.getRegistryType();
   }
}
