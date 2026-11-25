package com.lycanitesmobs.client.gui.buttons;

import com.lycanitesmobs.client.gui.beastiary.SummoningBeastiaryScreen;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class MinionTab extends Tab {
   public MinionTab(int id) {
      super(id, Tab.startX, Tab.startY, new ResourceLocation("lycanitesmobs", "textures/items/summoningstaff.png"));
   }

   public void onTabClicked() {
      if (ExtendedPlayer.getForPlayer(Minecraft.getMinecraft().player) != null) {
         SummoningBeastiaryScreen.openToPlayer(Minecraft.getMinecraft().player);
      }

   }

   public boolean shouldAddToList() {
      return true;
   }
}
