package mezz.jei.api.recipe;

import mezz.jei.api.IJeiHelpers;

public interface IRecipeCategoryRegistration {
   void addRecipeCategories(IRecipeCategory... var1);

   IJeiHelpers getJeiHelpers();
}
