package de.Whitedraco.switchbow.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerArrowOverlay extends Container {
   public ContainerArrowOverlay() {
   }

   public boolean canInteractWith(EntityPlayer playerIn) {
      return true;
   }
}
