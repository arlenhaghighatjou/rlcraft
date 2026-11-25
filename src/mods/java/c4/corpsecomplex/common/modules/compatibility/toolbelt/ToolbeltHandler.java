package c4.corpsecomplex.common.modules.compatibility.toolbelt;

import c4.corpsecomplex.common.modules.inventory.capability.IDeathInventory;
import c4.corpsecomplex.common.modules.inventory.helpers.DeathStackHandler;
import gigaherz.toolbelt.slot.ExtensionSlotBelt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ToolbeltHandler extends DeathStackHandler {
   private static final String MOD_ID = "toolbelt";
   private ExtensionSlotBelt beltSlot;

   public ToolbeltHandler(EntityPlayer player) {
      super(player, "toolbelt");
      this.beltSlot = ExtensionSlotBelt.get(player);
   }

   public boolean checkToStore(int slot) {
      return ToolbeltModule.keepToolbelt;
   }

   public ItemStack getStackInSlot(int index) {
      return this.beltSlot.getBelt().getContents();
   }

   public void retrieveInventory(IDeathInventory oldDeathInventory) {
      NBTTagCompound nbt = oldDeathInventory.getStorage("toolbelt");
      if (nbt != null) {
         this.storage.deserializeNBT(nbt);
         this.beltSlot.getBelt().setContents(this.storage.getStackInSlot(0));
      }
   }
}
