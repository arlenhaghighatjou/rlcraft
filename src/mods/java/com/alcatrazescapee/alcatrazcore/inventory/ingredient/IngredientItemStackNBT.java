package com.alcatrazescapee.alcatrazcore.inventory.ingredient;

import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public class IngredientItemStackNBT extends IngredientItemStack {
   IngredientItemStackNBT(@Nonnull ItemStack stack) {
      super(stack);
   }

   public boolean testIgnoreCount(Object obj) {
      return obj instanceof ItemStack && CoreHelpers.doStacksMatchUseNBT(this.stack, (ItemStack)obj);
   }

   public boolean matches(IRecipeIngredient other) {
      return other instanceof IngredientItemStack && CoreHelpers.doStacksMatchUseNBT(this.stack, ((IngredientItemStack)other).stack);
   }
}
