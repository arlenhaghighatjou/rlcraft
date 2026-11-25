package mezz.jei.api.gui;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;

public interface IRecipeLayoutDrawable extends IRecipeLayout {
   void setPosition(int var1, int var2);

   void drawRecipe(Minecraft var1, int var2, int var3);

   void drawOverlays(Minecraft var1, int var2, int var3);

   boolean isMouseOver(int var1, int var2);

   @Nullable
   Object getIngredientUnderMouse(int var1, int var2);

   /** @deprecated */
   @Deprecated
   void draw(Minecraft var1, int var2, int var3);
}
