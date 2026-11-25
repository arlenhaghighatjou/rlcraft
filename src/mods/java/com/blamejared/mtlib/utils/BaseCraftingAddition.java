package com.blamejared.mtlib.utils;

import com.blamejared.mtlib.helpers.LogHelper;
import java.util.List;
import net.minecraft.item.crafting.IRecipe;

public abstract class BaseCraftingAddition extends BaseListAddition<IRecipe> {
   protected BaseCraftingAddition(String name, List<IRecipe> list) {
      super(name, list);
   }

   protected BaseCraftingAddition(String name, List<IRecipe> list, List<IRecipe> recipes) {
      super(name, list, recipes);
   }

   protected String getRecipeInfo(IRecipe recipe) {
      return LogHelper.getStackDescription(recipe.getRecipeOutput());
   }
}
