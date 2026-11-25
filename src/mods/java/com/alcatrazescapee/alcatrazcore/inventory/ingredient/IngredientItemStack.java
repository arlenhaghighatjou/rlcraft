package com.alcatrazescapee.alcatrazcore.inventory.ingredient;

import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public class IngredientItemStack implements IRecipeIngredient {
   protected final ItemStack stack;
   private final List<ItemStack> stacks;

   IngredientItemStack(@Nonnull ItemStack stack) {
      this.stack = stack;
      this.stacks = Collections.singletonList(stack);
   }

   @Nonnull
   public String getName() {
      return this.stack.getUnlocalizedName();
   }

   @Nonnull
   public List<ItemStack> getStacks() {
      return this.stacks;
   }

   public boolean test(Object obj) {
      return this.testIgnoreCount(obj) && ((ItemStack)obj).getCount() >= this.stack.getCount();
   }

   public boolean testIgnoreCount(Object obj) {
      return obj instanceof ItemStack && CoreHelpers.doStacksMatch(this.stack, (ItemStack)obj);
   }

   public boolean matches(IRecipeIngredient other) {
      return other instanceof IngredientItemStack && CoreHelpers.doStacksMatch(this.stack, ((IngredientItemStack)other).stack);
   }
}
