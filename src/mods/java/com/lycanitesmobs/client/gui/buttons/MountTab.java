package com.lycanitesmobs.client.gui.buttons;

import com.lycanitesmobs.client.KeyHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;

public class MountTab extends Tab {
   public MountTab(int id) {
      super(id, Tab.startX, Tab.startY, new ResourceLocation("textures/items/saddle.png"));
   }

   public void onTabClicked() {
      KeyBinding.setKeyBindState(KeyHandler.instance.mountInventory.getKeyCode(), true);
   }

   public boolean shouldAddToList() {
      return false;
   }
}
