package mezz.jei.startup;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.config.Config;

public abstract class AbstractModIdHelper implements IModIdHelper {
   public AbstractModIdHelper() {
   }

   public <T> String getModNameForIngredient(T ingredient, IIngredientHelper<T> ingredientHelper) {
      String modId = ingredientHelper.getModId(ingredient);
      return this.getModNameForModId(modId);
   }

   public <T> List<String> addModNameToIngredientTooltip(List<String> tooltip, T ingredient, IIngredientHelper<T> ingredientHelper) {
      String modNameFormat = Config.getModNameFormat();
      if (modNameFormat.isEmpty()) {
         return tooltip;
      } else {
         String modId = ingredientHelper.getDisplayModId(ingredient);
         String modName = this.getFormattedModNameForModId(modId);
         if (modName == null) {
            return tooltip;
         } else {
            List<String> tooltipCopy = new ArrayList(tooltip);
            tooltipCopy.add(modName);
            return tooltipCopy;
         }
      }
   }
}
