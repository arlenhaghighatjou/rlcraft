package mezz.jei.api.recipe.transfer;

import mezz.jei.api.gui.IRecipeLayout;
import net.minecraft.client.Minecraft;

public interface IRecipeTransferError {
   IRecipeTransferError.Type getType();

   void showError(Minecraft var1, int var2, int var3, IRecipeLayout var4, int var5, int var6);

   public static enum Type {
      INTERNAL,
      USER_FACING;

      private Type() {
      }
   }
}
