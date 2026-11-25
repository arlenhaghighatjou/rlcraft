package c4.corpsecomplex.common.modules.compatibility.advinv;

import c4.corpsecomplex.common.modules.inventory.capability.IDeathInventory;
import c4.corpsecomplex.common.modules.inventory.helpers.DeathStackHandler;
import cubex2.advInv.api.AdvInvAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class AdvHandler extends DeathStackHandler {
   private static final String MOD_ID = "advinv";
   private IInventory playerAdv;

   public AdvHandler(EntityPlayer player) {
      super(player, "advinv");
      this.playerAdv = AdvInvAPI.getExtendedInventory(player);
      this.setSize(this.playerAdv.getSizeInventory());
   }

   public boolean checkToStore(int slot) {
      return AdvModule.keepAdv;
   }

   public ItemStack getStackInSlot(int index) {
      return this.playerAdv.getStackInSlot(index);
   }

   public void retrieveInventory(IDeathInventory oldDeathInventory) {
      NBTTagCompound nbt = oldDeathInventory.getStorage("advinv");
      if (nbt != null) {
         this.storage.deserializeNBT(nbt);

         for(int slot = 0; slot < this.storage.getSlots(); ++slot) {
            ItemStack stack = this.storage.getStackInSlot(slot);
            if (!stack.isEmpty()) {
               this.playerAdv.setInventorySlotContents(slot, stack);
            }
         }

      }
   }
}
