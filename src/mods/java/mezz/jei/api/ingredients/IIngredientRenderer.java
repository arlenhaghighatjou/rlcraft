package mezz.jei.api.ingredients;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;

public interface IIngredientRenderer<T> {
   void render(Minecraft var1, int var2, int var3, @Nullable T var4);

   default List<String> getTooltip(Minecraft minecraft, T ingredient, ITooltipFlag tooltipFlag) {
      return this.getTooltip(minecraft, ingredient, tooltipFlag == TooltipFlags.ADVANCED);
   }

   default FontRenderer getFontRenderer(Minecraft minecraft, T ingredient) {
      return minecraft.fontRenderer;
   }

   /** @deprecated */
   @Deprecated
   default List<String> getTooltip(Minecraft minecraft, T ingredient, boolean advanced) {
      return this.getTooltip(minecraft, ingredient);
   }

   /** @deprecated */
   @Deprecated
   default List<String> getTooltip(Minecraft minecraft, T ingredient) {
      throw new UnsupportedOperationException("Calling old getTooltip is not supported.");
   }
}
