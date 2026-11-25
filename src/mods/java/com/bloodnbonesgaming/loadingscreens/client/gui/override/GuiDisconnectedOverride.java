package com.bloodnbonesgaming.loadingscreens.client.gui.override;

import com.bloodnbonesgaming.loadingscreens.client.LoadingScreenHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.ITextComponent;

public class GuiDisconnectedOverride extends GuiDisconnected {
   public GuiDisconnectedOverride(GuiScreen screen, String reasonLocalizationKey, ITextComponent chatComp) {
      super(screen, reasonLocalizationKey, chatComp);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      LoadingScreenHandler.getInstance().renderScreen(this.mc, this.width, this.height);
      this.drawCenteredString(this.fontRenderer, this.reason, this.width / 2, this.height / 2 - this.textHeight / 2 - this.fontRenderer.FONT_HEIGHT * 2, 11184810);
      int height = this.height / 2 - this.textHeight / 2;
      if (this.multilineMessage != null) {
         for(String s : this.multilineMessage) {
            this.drawCenteredString(this.fontRenderer, s, this.width / 2, height, 16777215);
            height += this.fontRenderer.FONT_HEIGHT;
         }
      }

      for(int i = 0; i < this.buttonList.size(); ++i) {
         ((GuiButton)this.buttonList.get(i)).drawButton(this.mc, mouseX, mouseY, partialTicks);
      }

      for(int j = 0; j < this.labelList.size(); ++j) {
         ((GuiLabel)this.labelList.get(j)).drawLabel(this.mc, mouseX, mouseY);
      }

   }
}
