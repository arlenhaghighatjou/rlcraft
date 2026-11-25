package com.TominoCZ.FBP.gui;

import com.TominoCZ.FBP.handler.FBPConfigHandler;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FBPGuiYesNo extends GuiScreen {
   GuiButton yes;
   GuiButton no;
   GuiScreen parent;

   public FBPGuiYesNo(GuiScreen s) {
      this.parent = s;
   }

   public void initGui() {
      this.buttonList.clear();
      this.yes = new FBPGuiButton(1, this.width / 2 - 75, (int)((double)this.height / 1.85D), "\u00a7aYes", false, false);
      this.no = new FBPGuiButton(0, this.width / 2 + 26, (int)((double)this.height / 1.85D), "\u00a7cNo", false, false);
      this.yes.setWidth(50);
      this.no.setWidth(50);
      this.buttonList.add(this.yes);
      this.buttonList.add(this.no);
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      switch(button.id) {
      case 1:
         FBPConfigHandler.defaults(true);
      default:
         this.mc.displayGuiScreen(this.parent);
         FBPConfigHandler.write();
      }
   }

   public void updateScreen() {
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   protected void keyTyped(char c, int keyCode) throws IOException {
      if (keyCode == 1) {
         this.closeGui();
      } else {
         super.keyTyped(c, keyCode);
      }
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.parent.width = this.width;
      this.parent.height = this.height;
      this.parent.initGui();
      this.parent.drawScreen(0, 0, partialTicks);
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, "Are you sure?", this.width / 2, this.yes.y - 30, Integer.parseInt("FFAA00", 16));
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   void closeGui() {
      this.mc.displayGuiScreen(this.parent);
   }
}
