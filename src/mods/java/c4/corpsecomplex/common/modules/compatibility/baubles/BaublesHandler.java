package c4.corpsecomplex.common.modules.compatibility.baubles;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import c4.corpsecomplex.common.modules.inventory.capability.IDeathInventory;
import c4.corpsecomplex.common.modules.inventory.helpers.DeathStackHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class BaublesHandler extends DeathStackHandler {
   private static final String MOD_ID = "baubles";
   private IBaublesItemHandler playerBaubles;

   public BaublesHandler(EntityPlayer player) {
      super(player, "baubles");
      this.playerBaubles = BaublesApi.getBaublesHandler(player);
      this.setSize(this.playerBaubles.getSlots());
   }

   public boolean checkToStore(int slot) {
      return BaublesModule.keepBaubles;
   }

   public ItemStack getStackInSlot(int index) {
      return this.playerBaubles.getStackInSlot(index);
   }

   public void retrieveInventory(IDeathInventory oldDeathInventory) {
      NBTTagCompound nbt = oldDeathInventory.getStorage("baubles");
      if (nbt != null) {
         this.storage.deserializeNBT(nbt);

         for(int slot = 0; slot < this.storage.getSlots(); ++slot) {
            ItemStack stack = this.storage.getStackInSlot(slot);
            if (!stack.isEmpty()) {
               this.playerBaubles.setStackInSlot(slot, stack);
            }
         }

      }
   }
}
