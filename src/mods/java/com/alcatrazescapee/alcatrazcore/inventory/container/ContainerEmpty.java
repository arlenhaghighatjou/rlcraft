package com.alcatrazescapee.alcatrazcore.inventory.container;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;

public class ContainerEmpty extends Container {
   public ContainerEmpty() {
   }

   public void onCraftMatrixChanged(IInventory inventory) {
   }

   public boolean canInteractWith(@Nullable EntityPlayer player) {
      return false;
   }
}
