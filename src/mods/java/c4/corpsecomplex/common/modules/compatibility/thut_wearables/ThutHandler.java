package c4.corpsecomplex.common.modules.compatibility.thut_wearables;

import c4.corpsecomplex.common.modules.inventory.capability.IDeathInventory;
import c4.corpsecomplex.common.modules.inventory.helpers.DeathStackHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thut.wearables.ThutWearables;
import thut.wearables.inventory.PlayerWearables;

public class ThutHandler extends DeathStackHandler {
   private static final String MOD_ID = "thut_wearables";
   private PlayerWearables playerWearables;

   public ThutHandler(EntityPlayer player) {
      super(player, "thut_wearables");
      this.playerWearables = ThutWearables.getWearables(player);
      this.setSize(this.playerWearables.getSlots());
   }

   public boolean checkToStore(int slot) {
      return ThutModule.keepThut;
   }

   public ItemStack getStackInSlot(int index) {
      return this.playerWearables.getStackInSlot(index);
   }

   public void retrieveInventory(IDeathInventory oldDeathInventory) {
      NBTTagCompound nbt = oldDeathInventory.getStorage("thut_wearables");
      if (nbt != null) {
         this.storage.deserializeNBT(nbt);

         for(int slot = 0; slot < this.storage.getSlots(); ++slot) {
            ItemStack stack = this.storage.getStackInSlot(slot);
            if (!stack.isEmpty()) {
               this.playerWearables.setStackInSlot(slot, stack);
            }
         }

      }
   }
}
