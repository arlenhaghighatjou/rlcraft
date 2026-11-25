package mezz.jei.api.gui;

import javax.annotation.Nullable;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategory;

public interface IRecipeLayout {
   IGuiItemStackGroup getItemStacks();

   IGuiFluidStackGroup getFluidStacks();

   <T> IGuiIngredientGroup<T> getIngredientsGroup(IIngredientType<T> var1);

   @Nullable
   IFocus<?> getFocus();

   IRecipeCategory<?> getRecipeCategory();

   void setRecipeTransferButton(int var1, int var2);

   void setShapeless();

   /** @deprecated */
   @Deprecated
   <T> IGuiIngredientGroup<T> getIngredientsGroup(Class<T> var1);
}
