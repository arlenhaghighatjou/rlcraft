package c4.corpsecomplex.common.modules.inventory;

import c4.corpsecomplex.common.modules.inventory.capability.IDeathInventory;
import c4.corpsecomplex.common.modules.inventory.helpers.DeathStackHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemHandlerHelper;

public class InventoryHandler extends DeathStackHandler {
   private static final String MOD_ID = "vanilla";
   private InventoryPlayer inventoryPlayer;

   public InventoryHandler(EntityPlayer player) {
      super(player, "vanilla");
      this.inventoryPlayer = player.inventory;
      this.setSize(this.inventoryPlayer.getSizeInventory());
   }

   public ItemStack getStackInSlot(int slot) {
      return this.inventoryPlayer.getStackInSlot(slot);
   }

   public boolean checkToStore(int slot) {
      if (slot == this.inventoryPlayer.currentItem) {
         return InventoryModule.keepMainhand;
      } else if (slot < 9) {
         return InventoryModule.keepHotbar;
      } else if (slot >= 9 && slot < 36) {
         return InventoryModule.keepMainInventory;
      } else if (slot >= 36 && slot < 40) {
         return InventoryModule.keepArmor;
      } else {
         return slot == 40 ? InventoryModule.keepOffhand : false;
      }
   }

   public void retrieveInventory(IDeathInventory oldDeathInventory) {
      NBTTagCompound nbt = oldDeathInventory.getStorage("vanilla");
      if (nbt != null) {
         this.storage.deserializeNBT(nbt);

         for(int slot = 0; slot < this.storage.getSlots(); ++slot) {
            ItemStack stack = this.storage.getStackInSlot(slot);
            if (!stack.isEmpty()) {
               if (!this.inventoryPlayer.getStackInSlot(slot).isEmpty()) {
                  ItemHandlerHelper.giveItemToPlayer(this.inventoryPlayer.player, stack);
               } else {
                  this.inventoryPlayer.setInventorySlotContents(slot, stack);
               }
            }
         }

      }
   }
}
