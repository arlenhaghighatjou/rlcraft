package com.teamacronymcoders.base.items;

import java.util.List;
import net.minecraft.item.crafting.IRecipe;

@FunctionalInterface
public interface IHasRecipe {
   List<IRecipe> getRecipes(List<IRecipe> var1);
}
