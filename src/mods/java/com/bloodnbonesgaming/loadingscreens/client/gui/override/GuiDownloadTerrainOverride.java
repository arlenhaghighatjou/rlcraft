package com.bloodnbonesgaming.loadingscreens.client.gui.override;

import com.bloodnbonesgaming.loadingscreens.client.LoadingScreenHandler;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.resources.I18n;

public class GuiDownloadTerrainOverride extends GuiDownloadTerrain {
   public GuiDownloadTerrainOverride() {
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      LoadingScreenHandler.getInstance().renderScreen(this.mc, this.width, this.height);
      this.drawCenteredString(this.fontRenderer, I18n.format("multiplayer.downloadingTerrain", new Object[0]), this.width / 2, this.height / 2 - 50, 16777215);
   }
}
