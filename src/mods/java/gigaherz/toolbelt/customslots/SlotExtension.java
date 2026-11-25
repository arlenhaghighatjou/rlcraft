package gigaherz.toolbelt.customslots;

import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotExtension extends Slot {
   private static IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);
   private final IExtensionSlot slot;

   public SlotExtension(IExtensionSlot slot, int x, int y) {
      super(emptyInventory, 0, x, y);
      this.slot = slot;
   }

   public boolean isItemValid(@Nonnull ItemStack stack) {
      return stack.isEmpty() ? false : this.slot.canEquip(stack);
   }

   @Nonnull
   public ItemStack getStack() {
      return this.slot.getContents();
   }

   public void putStack(@Nonnull ItemStack stack) {
      this.slot.setContents(stack);
      this.onSlotChanged();
   }

   public void onSlotChange(@Nonnull ItemStack p_75220_1_, @Nonnull ItemStack p_75220_2_) {
   }

   public int getSlotStackLimit() {
      return 1;
   }

   public int getItemStackLimit(@Nonnull ItemStack stack) {
      return 1;
   }

   public boolean canTakeStack(EntityPlayer playerIn) {
      return this.slot.canUnequip(this.slot.getContents());
   }

   @Nonnull
   public ItemStack decrStackSize(int amount) {
      ItemStack itemstack = this.slot.getContents();
      int available = Math.min(itemstack.getCount(), amount);
      int remaining = itemstack.getCount() - available;
      ItemStack split = itemstack.copy();
      split.setCount(available);
      itemstack.setCount(remaining);
      if (remaining <= 0) {
         this.slot.setContents(ItemStack.EMPTY);
      }

      this.onSlotChanged();
      return split;
   }

   public IExtensionSlot getExtensionSlot() {
      return this.slot;
   }

   public boolean isSameInventory(Slot other) {
      return other instanceof SlotExtension && ((SlotExtension)other).getExtensionSlot() == this.slot;
   }
}
