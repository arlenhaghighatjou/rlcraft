package c4.corpsecomplex.common.modules.inventory.helpers;

import c4.corpsecomplex.common.modules.inventory.InventoryHandler;
import c4.corpsecomplex.common.modules.inventory.InventoryModule;
import c4.corpsecomplex.common.modules.inventory.capability.IDeathInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public abstract class DeathStackHandler extends DeathInventoryHandler {
   protected ItemStackHandler storage = new ItemStackHandler();

   public abstract boolean checkToStore(int var1);

   public abstract ItemStack getStackInSlot(int var1);

   public abstract void retrieveInventory(IDeathInventory var1);

   public DeathStackHandler(EntityPlayer player, String modid) {
      super(player, modid);
   }

   public void setSize(int size) {
      this.storage.setSize(size);
   }

   public void storeInventory() {
      for(int index = 0; index < this.storage.getSlots(); ++index) {
         ItemStack stack = this.getStackInSlot(index);
         if (!stack.isEmpty()) {
            boolean isMainInventory = this instanceof InventoryHandler && index >= 9 && index < 36;
            this.storeStack(index, DeathStackHelper.stackToStore(this.player, stack, this.checkToStore(index), isMainInventory));
         }

         if (!stack.isEmpty() && InventoryModule.randomDestroy > 0.0D) {
            DeathStackHelper.randomlyDestroy(stack);
         }
      }

      this.deathInventory.addStorage(this.modid, this.storage.serializeNBT());
   }

   public void storeStack(int index, ItemStack stack) {
      if (!stack.isEmpty()) {
         this.storage.insertItem(index, stack, false);
      }

   }
}
