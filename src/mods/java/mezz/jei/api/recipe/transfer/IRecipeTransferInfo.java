package mezz.jei.api.recipe.transfer;

import java.util.List;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public interface IRecipeTransferInfo<C extends Container> {
   Class<C> getContainerClass();

   String getRecipeCategoryUid();

   boolean canHandle(C var1);

   List<Slot> getRecipeSlots(C var1);

   List<Slot> getInventorySlots(C var1);

   default boolean requireCompleteSets() {
      return true;
   }
}
