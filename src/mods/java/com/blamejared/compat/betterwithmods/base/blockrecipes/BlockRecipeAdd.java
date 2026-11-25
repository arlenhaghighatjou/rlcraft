package com.blamejared.compat.betterwithmods.base.blockrecipes;

import betterwithmods.common.registry.block.managers.CraftingManagerBlock;
import betterwithmods.common.registry.block.recipe.BlockRecipe;
import com.blamejared.mtlib.utils.BaseListAddition;
import com.google.common.collect.Lists;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;

public class BlockRecipeAdd<T extends BlockRecipe> extends BaseListAddition<T> {
   public BlockRecipeAdd(String name, CraftingManagerBlock<T> recipes, T recipe) {
      super(name, recipes.getRecipes(), Lists.newArrayList(new BlockRecipe[]{recipe}));
   }

   protected String getRecipeInfo(T recipe) {
      return (String)recipe.getOutputs().stream().map(ItemStack::getDisplayName).collect(Collectors.joining(","));
   }
}
