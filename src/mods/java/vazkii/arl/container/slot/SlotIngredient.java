package vazkii.arl.container.slot;

import net.minecraft.inventory.IInventory;
import vazkii.arl.recipe.RecipeHandler;

public class SlotIngredient extends SlotFiltered {
   public SlotIngredient(IInventory inventoryIn, int index, int xPosition, int yPosition, Object obj) {
      super(inventoryIn, index, xPosition, yPosition, RecipeHandler.asIngredient(obj)::apply);
   }
}
