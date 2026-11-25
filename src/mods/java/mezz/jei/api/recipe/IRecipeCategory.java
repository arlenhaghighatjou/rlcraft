package mezz.jei.api.recipe;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;

public interface IRecipeCategory<T extends IRecipeWrapper> {
   String getUid();

   String getTitle();

   String getModName();

   IDrawable getBackground();

   @Nullable
   default IDrawable getIcon() {
      return null;
   }

   default void drawExtras(Minecraft minecraft) {
   }

   void setRecipe(IRecipeLayout var1, T var2, IIngredients var3);

   default List<String> getTooltipStrings(int mouseX, int mouseY) {
      return Collections.emptyList();
   }
}
