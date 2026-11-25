package reborncore.client.gui.slots;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotFilteredVoid extends BaseSlot {
   private List<ItemStack> filter = new ArrayList();

   public SlotFilteredVoid(IInventory par1iInventory, int id, int x, int y) {
      super(par1iInventory, id, x, y);
   }

   public SlotFilteredVoid(IInventory par1iInventory, int id, int x, int y, ItemStack[] filterList) {
      super(par1iInventory, id, x, y);

      for(ItemStack itemStack : filterList) {
         this.filter.add(itemStack);
      }

   }

   public boolean isItemValid(ItemStack stack) {
      for(ItemStack itemStack : this.filter) {
         if (itemStack.getItem().equals(stack.getItem()) && itemStack.getItemDamage() == stack.getItemDamage()) {
            return false;
         }
      }

      return super.isItemValid(stack);
   }

   public void putStack(ItemStack arg0) {
   }
}
