package com.bloodnbonesgaming.loadingscreens.client.gui.override;

import com.bloodnbonesgaming.loadingscreens.client.LoadingScreenHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenWorking;

public class GuiWorkingOverride extends GuiScreenWorking {
   public GuiWorkingOverride() {
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if (this.doneWorking) {
         if (!this.mc.isConnectedToRealms()) {
            this.mc.displayGuiScreen((GuiScreen)null);
         }
      } else {
         LoadingScreenHandler.getInstance().renderScreen(this.mc, this.width, this.height);
         this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 70, 16777215);
         this.drawCenteredString(this.fontRenderer, this.stage + " " + this.progress + "%", this.width / 2, 90, 16777215);
      }

   }
}
