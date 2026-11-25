package mezz.jei.api.recipe;

import java.util.Collections;
import java.util.List;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;

public interface IRecipeWrapper {
   void getIngredients(IIngredients var1);

   default void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
   }

   default List<String> getTooltipStrings(int mouseX, int mouseY) {
      return Collections.emptyList();
   }

   default boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
      return false;
   }
}
