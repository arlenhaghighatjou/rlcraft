package net.silentchaos512.lib.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public interface IInventorySL extends IInventory {
   boolean isUsable(EntityPlayer var1);

   default boolean isUsableByPlayer(EntityPlayer player) {
      return this.isUsable(player);
   }

   default boolean isEmpty() {
      return false;
   }
}
