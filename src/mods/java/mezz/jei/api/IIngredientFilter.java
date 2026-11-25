package mezz.jei.api;

import com.google.common.collect.ImmutableList;

public interface IIngredientFilter {
   void setFilterText(String var1);

   String getFilterText();

   ImmutableList<Object> getFilteredIngredients();
}
