package mezz.jei.gui.ingredients;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.gui.Focus;
import mezz.jei.util.ErrorUtil;

public class IngredientLookupState {
   @Nullable
   private final IFocus<?> focus;
   private final ImmutableList<IRecipeCategory> recipeCategories;
   private int recipeCategoryIndex;
   private int recipeIndex;
   private int recipesPerPage;

   public IngredientLookupState(@Nullable IFocus<?> focus, List<IRecipeCategory> recipeCategories, int recipeCategoryIndex, int recipeIndex) {
      ErrorUtil.checkNotEmpty(recipeCategories, "recipeCategories");
      Preconditions.checkArgument(recipeCategoryIndex >= 0, "Recipe category index cannot be negative.");
      Preconditions.checkArgument(recipeIndex >= 0, "Recipe index cannot be negative.");
      if (focus != null) {
         focus = Focus.check(focus);
      }

      this.focus = focus;
      this.recipeCategories = ImmutableList.copyOf(recipeCategories);
      this.setRecipeCategoryIndex(recipeCategoryIndex);
      this.setRecipeIndex(recipeIndex);
   }

   @Nullable
   public IFocus<?> getFocus() {
      return this.focus;
   }

   public ImmutableList<IRecipeCategory> getRecipeCategories() {
      return this.recipeCategories;
   }

   public int getRecipeCategoryIndex() {
      return this.recipeCategoryIndex;
   }

   public void setRecipeCategoryIndex(int recipeCategoryIndex) {
      this.recipeCategoryIndex = recipeCategoryIndex;
   }

   public int getRecipeIndex() {
      return this.recipeIndex;
   }

   public void setRecipeIndex(int recipeIndex) {
      this.recipeIndex = recipeIndex;
   }

   public int getRecipesPerPage() {
      return this.recipesPerPage;
   }

   public void setRecipesPerPage(int recipesPerPage) {
      this.recipesPerPage = recipesPerPage;
   }
}
