package c4.corpsecomplex.common.modules.inventory.helpers;

import c4.corpsecomplex.common.modules.inventory.capability.DeathInventory;
import c4.corpsecomplex.common.modules.inventory.capability.IDeathInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

public abstract class DeathInventoryHandler {
   protected EntityPlayer player;
   protected String modid;
   protected IDeathInventory deathInventory;

   public abstract boolean checkToStore(int var1);

   public abstract void storeInventory();

   public abstract void retrieveInventory(IDeathInventory var1);

   public DeathInventoryHandler(EntityPlayer player, String modid) {
      this.player = player;
      this.modid = modid;
      this.deathInventory = (IDeathInventory)player.getCapability(DeathInventory.Provider.DEATH_INV_CAP, (EnumFacing)null);
   }
}
