package gigaherz.toolbelt.customslots;

import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ExtensionSlotItemHandler implements IExtensionSlot {
   protected final IExtensionContainer owner;
   protected final ResourceLocation slotType;
   protected final int slot;
   protected final IItemHandlerModifiable inventory;

   public ExtensionSlotItemHandler(IExtensionContainer owner, ResourceLocation slotType, IItemHandlerModifiable inventory, int slot) {
      this.owner = owner;
      this.slotType = slotType;
      this.slot = slot;
      this.inventory = inventory;
   }

   @Nonnull
   public IExtensionContainer getContainer() {
      return this.owner;
   }

   @Nonnull
   public ResourceLocation getType() {
      return this.slotType;
   }

   @Nonnull
   public ItemStack getContents() {
      return this.inventory.getStackInSlot(this.slot);
   }

   public void setContents(@Nonnull ItemStack stack) {
      ItemStack oldStack = this.getContents();
      if (oldStack != stack) {
         if (!oldStack.isEmpty()) {
            this.notifyUnequip(oldStack);
         }

         this.inventory.setStackInSlot(this.slot, stack);
         if (!stack.isEmpty()) {
            this.notifyEquip(stack);
         }

      }
   }

   public void onContentsChanged() {
      this.owner.onContentsChanged(this);
   }

   private void notifyEquip(ItemStack stack) {
      IExtensionSlotItem extItem = (IExtensionSlotItem)stack.getCapability(CapabilityExtensionSlotItem.INSTANCE, (EnumFacing)null);
      if (extItem != null) {
         extItem.onEquipped(stack, this);
      }
   }

   private void notifyUnequip(ItemStack stack) {
      IExtensionSlotItem extItem = (IExtensionSlotItem)stack.getCapability(CapabilityExtensionSlotItem.INSTANCE, (EnumFacing)null);
      if (extItem != null) {
         extItem.onUnequipped(stack, this);
      }
   }

   public void onWornTick() {
      ItemStack stack = this.getContents();
      if (!stack.isEmpty()) {
         IExtensionSlotItem extItem = (IExtensionSlotItem)stack.getCapability(CapabilityExtensionSlotItem.INSTANCE, (EnumFacing)null);
         if (extItem != null) {
            extItem.onWornTick(stack, this);
         }
      }
   }
}
