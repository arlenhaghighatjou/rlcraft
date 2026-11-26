package com.lycanitesmobs.client.gui;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;

public class InventorySnooperScreen extends GuiInventory {
   public InventorySnooperScreen(EntityPlayer entityPlayer) {
      super(entityPlayer);
   }


   public int getGUIXSize() {
      return this.xSize;
   }

   public int getGUIYSize() {
      return this.ySize;
   }
}
