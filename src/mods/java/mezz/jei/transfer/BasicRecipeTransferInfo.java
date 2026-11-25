package mezz.jei.transfer;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class BasicRecipeTransferInfo<C extends Container> implements IRecipeTransferInfo<C> {
   private final Class<C> containerClass;
   private final String recipeCategoryUid;
   private final int recipeSlotStart;
   private final int recipeSlotCount;
   private final int inventorySlotStart;
   private final int inventorySlotCount;

   public BasicRecipeTransferInfo(Class<C> containerClass, String recipeCategoryUid, int recipeSlotStart, int recipeSlotCount, int inventorySlotStart, int inventorySlotCount) {
      this.containerClass = containerClass;
      this.recipeCategoryUid = recipeCategoryUid;
      this.recipeSlotStart = recipeSlotStart;
      this.recipeSlotCount = recipeSlotCount;
      this.inventorySlotStart = inventorySlotStart;
      this.inventorySlotCount = inventorySlotCount;
   }

   public Class<C> getContainerClass() {
      return this.containerClass;
   }

   public String getRecipeCategoryUid() {
      return this.recipeCategoryUid;
   }

   public boolean canHandle(C container) {
      return true;
   }

   public List<Slot> getRecipeSlots(C container) {
      List<Slot> slots = new ArrayList();

      for(int i = this.recipeSlotStart; i < this.recipeSlotStart + this.recipeSlotCount; ++i) {
         Slot slot = container.getSlot(i);
         slots.add(slot);
      }

      return slots;
   }

   public List<Slot> getInventorySlots(C container) {
      List<Slot> slots = new ArrayList();

      for(int i = this.inventorySlotStart; i < this.inventorySlotStart + this.inventorySlotCount; ++i) {
         Slot slot = container.getSlot(i);
         slots.add(slot);
      }

      return slots;
   }
}
