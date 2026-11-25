package com.bloodnbonesgaming.loadingscreens.client.gui.override;

import com.bloodnbonesgaming.loadingscreens.client.LoadingScreenHandler;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiConnectingWrapper extends GuiScreen {
   private final GuiConnecting wrapped;

   public GuiConnectingWrapper(GuiConnecting gui) {
      this.wrapped = gui;
   }

   public void updateScreen() {
      this.wrapped.updateScreen();
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
   }

   public void initGui() {
      this.wrapped.initGui();
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel", new Object[0])));
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 0) {
         this.wrapped.cancel = true;
         if (this.wrapped.networkManager != null) {
            this.wrapped.networkManager.closeChannel(new TextComponentString("Aborted"));
         }

         this.mc.displayGuiScreen(this.wrapped.previousGuiScreen);
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      LoadingScreenHandler.getInstance().renderScreen(this.mc, this.width, this.height);
      if (this.wrapped.networkManager == null) {
         this.drawCenteredString(this.fontRenderer, I18n.format("connect.connecting", new Object[0]), this.width / 2, this.height / 2 - 50, 16777215);
      } else {
         this.drawCenteredString(this.fontRenderer, I18n.format("connect.authorizing", new Object[0]), this.width / 2, this.height / 2 - 50, 16777215);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
