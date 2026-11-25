package mezz.jei.api;

import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.api.recipe.IFocus;

public interface IRecipesGui {
   <V> void show(IFocus<V> var1);

   void showCategories(List<String> var1);

   @Nullable
   Object getIngredientUnderMouse();
}
