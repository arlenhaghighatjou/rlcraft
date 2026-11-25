package mezz.jei.api.gui;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocus;

public interface IGuiIngredientGroup<T> {
   void set(IIngredients var1);

   void set(int var1, @Nullable List<T> var2);

   void set(int var1, @Nullable T var2);

   void setBackground(int var1, IDrawable var2);

   void addTooltipCallback(ITooltipCallback<T> var1);

   Map<Integer, ? extends IGuiIngredient<T>> getGuiIngredients();

   void init(int var1, boolean var2, int var3, int var4);

   void init(int var1, boolean var2, IIngredientRenderer<T> var3, int var4, int var5, int var6, int var7, int var8, int var9);

   void setOverrideDisplayFocus(@Nullable IFocus<T> var1);
}
