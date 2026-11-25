package snownee.kiwi.crafting;

import javax.annotation.Nonnull;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

public abstract class AbstractDynamicShapedRecipe extends Impl<IRecipe> implements IShapedRecipe {
   public AbstractDynamicShapedRecipe() {
   }

   public boolean matches(InventoryCrafting inv, World world) {
      for(int x = 0; x <= inv.getWidth() - this.getRecipeWidth(); ++x) {
         for(int y = 0; y <= inv.getHeight() - this.getRecipeHeight(); ++y) {
            if (this.checkMatch(inv, x, y)) {
               return true;
            }
         }
      }

      return false;
   }

   public abstract ItemStack getCraftingResult(InventoryCrafting var1);

   public boolean canFit(int width, int height) {
      return width >= this.getRecipeWidth() && height >= this.getRecipeHeight();
   }

   @Nonnull
   public ItemStack getRecipeOutput() {
      return ItemStack.EMPTY;
   }

   public abstract int getRecipeWidth();

   public abstract int getRecipeHeight();

   protected abstract boolean checkMatch(InventoryCrafting var1, int var2, int var3);

   public boolean isDynamic() {
      return true;
   }

   protected boolean checkEmpty(InventoryCrafting inv, int startX, int startY) {
      for(int x = 0; x < inv.getWidth(); ++x) {
         for(int y = 0; y < inv.getHeight(); ++y) {
            int subX = x - startX;
            int subY = y - startY;
            if ((subX < 0 || subY < 0 || subX >= this.getRecipeWidth() || subY >= this.getRecipeHeight()) && !Ingredient.EMPTY.apply(inv.getStackInRowAndColumn(x, y))) {
               return false;
            }
         }
      }

      return true;
   }
}
