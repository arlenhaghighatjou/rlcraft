package com.endertech.minecraft.forge.materials;

import javax.annotation.Nullable;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class NullRecipe implements IRecipe {
   private IRecipe replacedRecipe;

   public NullRecipe(IRecipe replacedRecipe) {
      this.replacedRecipe = replacedRecipe;
   }

   public boolean matches(InventoryCrafting inv, World worldIn) {
      return false;
   }

   public ItemStack getCraftingResult(InventoryCrafting inv) {
      return this.replacedRecipe.getCraftingResult(inv);
   }

   public boolean canFit(int width, int height) {
      return this.replacedRecipe.canFit(width, height);
   }

   public ItemStack getRecipeOutput() {
      return ItemStack.EMPTY;
   }

   public IRecipe setRegistryName(ResourceLocation name) {
      return (IRecipe)this.replacedRecipe.setRegistryName(name);
   }

   @Nullable
   public ResourceLocation getRegistryName() {
      return this.replacedRecipe.getRegistryName();
   }

   public Class<IRecipe> getRegistryType() {
      return this.replacedRecipe.getRegistryType();
   }
}
