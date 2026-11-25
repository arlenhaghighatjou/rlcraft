package c4.corpsecomplex.common.modules.compatibility.rpginventory;

import c4.corpsecomplex.common.modules.inventory.capability.IDeathInventory;
import c4.corpsecomplex.common.modules.inventory.helpers.DeathStackHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import subaraki.rpginventory.capability.playerinventory.RpgInventoryData;
import subaraki.rpginventory.capability.playerinventory.RpgStackHandler;

public class RPGHandler extends DeathStackHandler {
   private static final String MOD_ID = "rpginventory";
   private RpgStackHandler rpgItems;

   public RPGHandler(EntityPlayer player) {
      super(player, "rpginventory");
      this.rpgItems = RpgInventoryData.get(player).getInventory();
      this.setSize(this.rpgItems.getSlots());
   }

   public boolean checkToStore(int slot) {
      return RPGModule.keepRPG;
   }

   public ItemStack getStackInSlot(int slot) {
      return this.rpgItems.getStackInSlot(slot);
   }

   public void retrieveInventory(IDeathInventory oldDeathInventory) {
      NBTTagCompound nbt = oldDeathInventory.getStorage("rpginventory");
      if (nbt != null) {
         this.storage.deserializeNBT(nbt);

         for(int slot = 0; slot < this.storage.getSlots(); ++slot) {
            ItemStack stack = this.storage.getStackInSlot(slot);
            if (!stack.isEmpty()) {
               this.rpgItems.setStackInSlot(slot, stack);
            }
         }

      }
   }
}
