package com.lycanitesmobs.client.gui.buttons;

import com.lycanitesmobs.client.gui.beastiary.IndexBeastiaryScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class MainTab extends Tab {
   public MainTab(int id) {
      super(id, Tab.startX, Tab.startY, new ResourceLocation("lycanitesmobs", "textures/items/soulgazer.png"));
   }

   public MainTab(int id, int x, int y) {
      super(id, x, y, new ResourceLocation("lycanitesmobs", "textures/items/soulgazer.png"));
   }

   public void onTabClicked() {
      IndexBeastiaryScreen.openToPlayer(Minecraft.getMinecraft().player);
   }

   public boolean shouldAddToList() {
      return true;
   }
}
