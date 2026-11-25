package mezz.jei.transfer;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import net.minecraft.client.Minecraft;

public class RecipeTransferErrorInternal implements IRecipeTransferError {
   public static final RecipeTransferErrorInternal INSTANCE = new RecipeTransferErrorInternal();

   private RecipeTransferErrorInternal() {
   }

   public IRecipeTransferError.Type getType() {
      return IRecipeTransferError.Type.INTERNAL;
   }

   public void showError(Minecraft minecraft, int mouseX, int mouseY, IRecipeLayout recipeLayout, int recipeX, int recipeY) {
   }
}
