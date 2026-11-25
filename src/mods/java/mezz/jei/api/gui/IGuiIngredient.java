package mezz.jei.api.gui;

import java.awt.Color;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;

public interface IGuiIngredient<T> {
   @Nullable
   T getDisplayedIngredient();

   List<T> getAllIngredients();

   boolean isInput();

   void drawHighlight(Minecraft var1, Color var2, int var3, int var4);
}
