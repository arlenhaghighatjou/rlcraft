package c4.corpsecomplex.common.modules.compatibility.cosmeticarmorreworked;

import c4.corpsecomplex.common.modules.inventory.capability.IDeathInventory;
import c4.corpsecomplex.common.modules.inventory.helpers.DeathStackHandler;
import lain.mods.cos.CosmeticArmorReworked;
import lain.mods.cos.inventory.InventoryCosArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CosmeticHandler extends DeathStackHandler {
   private static final String MOD_ID = "cosmeticarmorreworked";
   private InventoryCosArmor playerCosmetics;

   public CosmeticHandler(EntityPlayer player) {
      super(player, "cosmeticarmorreworked");
      this.playerCosmetics = CosmeticArmorReworked.invMan.getCosArmorInventory(player.getUniqueID());
      this.setSize(this.playerCosmetics.getSizeInventory());
   }

   public boolean checkToStore(int slot) {
      return CosmeticModule.keepCosmetic;
   }

   public ItemStack getStackInSlot(int slot) {
      return this.playerCosmetics.getStackInSlot(slot);
   }

   public void retrieveInventory(IDeathInventory oldDeathInventory) {
      NBTTagCompound nbt = oldDeathInventory.getStorage("cosmeticarmorreworked");
      if (nbt != null) {
         this.storage.deserializeNBT(nbt);

         for(int slot = 0; slot < this.storage.getSlots(); ++slot) {
            ItemStack stack = this.storage.getStackInSlot(slot);
            if (!stack.isEmpty()) {
               this.playerCosmetics.setInventorySlotContents(slot, stack);
            }
         }

      }
   }
}
