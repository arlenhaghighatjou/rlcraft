package mezz.jei.ingredients;

import mezz.jei.api.IItemBlacklist;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import net.minecraft.item.ItemStack;

/** @deprecated */
@Deprecated
public class ItemBlacklist implements IItemBlacklist {
   private final IIngredientBlacklist ingredientBlacklist;

   public ItemBlacklist(IIngredientBlacklist ingredientBlacklist) {
      this.ingredientBlacklist = ingredientBlacklist;
   }

   public void addItemToBlacklist(ItemStack itemStack) {
      this.ingredientBlacklist.addIngredientToBlacklist(itemStack);
   }

   public void removeItemFromBlacklist(ItemStack itemStack) {
      this.ingredientBlacklist.removeIngredientFromBlacklist(itemStack);
   }

   public boolean isItemBlacklisted(ItemStack itemStack) {
      return this.ingredientBlacklist.isIngredientBlacklisted(itemStack);
   }
}
