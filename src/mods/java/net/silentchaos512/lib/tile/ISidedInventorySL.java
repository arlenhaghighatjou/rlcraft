package net.silentchaos512.lib.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;

public interface ISidedInventorySL extends ISidedInventory {
   boolean isUsable(EntityPlayer var1);

   default boolean isUsableByPlayer(EntityPlayer player) {
      return this.isUsable(player);
   }

   default boolean isEmpty() {
      return false;
   }
}
