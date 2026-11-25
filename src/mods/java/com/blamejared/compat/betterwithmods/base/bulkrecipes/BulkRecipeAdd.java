package com.blamejared.compat.betterwithmods.base.bulkrecipes;

import betterwithmods.common.registry.bulk.manager.CraftingManagerBulk;
import betterwithmods.common.registry.bulk.recipes.BulkRecipe;
import com.blamejared.mtlib.utils.BaseListAddition;
import com.google.common.collect.Lists;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;

public class BulkRecipeAdd<T extends BulkRecipe> extends BaseListAddition<T> {
   public BulkRecipeAdd(String name, Supplier<CraftingManagerBulk<T>> recipes, T recipe) {
      super(name, ((CraftingManagerBulk)recipes.get()).getRecipes(), Lists.newArrayList(new BulkRecipe[]{recipe}));
   }

   protected String getRecipeInfo(T recipe) {
      return (String)recipe.getOutputs().stream().map(ItemStack::getDisplayName).collect(Collectors.joining(","));
   }
}
