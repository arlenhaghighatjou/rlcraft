package c4.corpsecomplex.common.modules.compatibility.galacticraftcore;

import c4.corpsecomplex.common.modules.inventory.capability.IDeathInventory;
import c4.corpsecomplex.common.modules.inventory.helpers.DeathStackHandler;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GalacticraftHandler extends DeathStackHandler {
   private static final String MOD_ID = "galacticraft";
   private IInventory playerGalacticraft;

   public GalacticraftHandler(EntityPlayer player) {
      super(player, "galacticraft");
      this.playerGalacticraft = GCPlayerStats.get(player).getExtendedInventory();
      this.setSize(this.playerGalacticraft.getSizeInventory());
   }

   public boolean checkToStore(int slot) {
      return GalacticraftModule.keepGalacticraft;
   }

   public ItemStack getStackInSlot(int index) {
      return this.playerGalacticraft.getStackInSlot(index);
   }

   public void retrieveInventory(IDeathInventory oldDeathInventory) {
      NBTTagCompound nbt = oldDeathInventory.getStorage("galacticraft");
      if (nbt != null) {
         this.storage.deserializeNBT(nbt);

         for(int slot = 0; slot < this.storage.getSlots(); ++slot) {
            ItemStack stack = this.storage.getStackInSlot(slot);
            if (!stack.isEmpty()) {
               this.playerGalacticraft.setInventorySlotContents(slot, stack);
            }
         }

      }
   }
}
