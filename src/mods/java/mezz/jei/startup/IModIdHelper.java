package mezz.jei.startup;

import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.api.ingredients.IIngredientHelper;

public interface IModIdHelper {
   String getModNameForModId(String var1);

   @Nullable
   String getFormattedModNameForModId(String var1);

   <T> String getModNameForIngredient(T var1, IIngredientHelper<T> var2);

   <T> List<String> addModNameToIngredientTooltip(List<String> var1, T var2, IIngredientHelper<T> var3);

   @Nullable
   String getModNameTooltipFormatting();
}
