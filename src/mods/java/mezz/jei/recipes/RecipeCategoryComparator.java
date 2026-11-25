package mezz.jei.recipes;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import mezz.jei.api.recipe.IRecipeCategory;

public class RecipeCategoryComparator implements Comparator<String> {
   private final ImmutableList<String> recipeCategories;

   public RecipeCategoryComparator(List<IRecipeCategory> recipeCategories) {
      List<String> recipeCategoryUids = new ArrayList();

      for(IRecipeCategory recipeCategory : recipeCategories) {
         recipeCategoryUids.add(recipeCategory.getUid());
      }

      this.recipeCategories = ImmutableList.copyOf(recipeCategoryUids);
   }

   public int compare(String recipeCategory1, String recipeCategory2) {
      Integer index1 = this.recipeCategories.indexOf(recipeCategory1);
      Integer index2 = this.recipeCategories.indexOf(recipeCategory2);
      return index1.compareTo(index2);
   }
}
