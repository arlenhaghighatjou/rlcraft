package mezz.jei.transfer;

import java.util.Collection;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.util.ErrorUtil;

public class RecipeTransferHandlerHelper implements IRecipeTransferHandlerHelper {
   public RecipeTransferHandlerHelper() {
   }

   public IRecipeTransferError createInternalError() {
      return RecipeTransferErrorInternal.INSTANCE;
   }

   public IRecipeTransferError createUserErrorWithTooltip(String tooltipMessage) {
      ErrorUtil.checkNotNull(tooltipMessage, "tooltipMessage");
      return new RecipeTransferErrorTooltip(tooltipMessage);
   }

   public IRecipeTransferError createUserErrorForSlots(String tooltipMessage, Collection<Integer> missingItemSlots) {
      ErrorUtil.checkNotNull(tooltipMessage, "tooltipMessage");
      ErrorUtil.checkNotEmpty(missingItemSlots, "missingItemSlots");
      return new RecipeTransferErrorSlots(tooltipMessage, missingItemSlots);
   }
}
