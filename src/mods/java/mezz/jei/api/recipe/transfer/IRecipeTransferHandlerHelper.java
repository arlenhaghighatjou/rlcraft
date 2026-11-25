package mezz.jei.api.recipe.transfer;

import java.util.Collection;

public interface IRecipeTransferHandlerHelper {
   IRecipeTransferError createInternalError();

   IRecipeTransferError createUserErrorWithTooltip(String var1);

   IRecipeTransferError createUserErrorForSlots(String var1, Collection<Integer> var2);
}
