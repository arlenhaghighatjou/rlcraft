package mezz.jei.transfer;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.gui.TooltipRenderer;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

public class RecipeTransferErrorTooltip implements IRecipeTransferError {
   private final List<String> message = new ArrayList();

   public RecipeTransferErrorTooltip(String message) {
      this.message.add(Translator.translateToLocal("jei.tooltip.transfer"));
      this.message.add(TextFormatting.RED + message);
   }

   public IRecipeTransferError.Type getType() {
      return IRecipeTransferError.Type.USER_FACING;
   }

   public void showError(Minecraft minecraft, int mouseX, int mouseY, IRecipeLayout recipeLayout, int recipeX, int recipeY) {
      TooltipRenderer.drawHoveringText(minecraft, this.message, mouseX, mouseY, 150);
   }
}
