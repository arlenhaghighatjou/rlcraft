package xzeroair.trinkets.client.gui.hud.mana;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import xzeroair.trinkets.util.TrinketsConfig;

public class ManaHud extends GuiScreen {
   public static ResourceLocation background = null;
   private float oldMouseX;
   private float oldMouseY;

   public ManaHud() {
      this.allowUserInput = true;
   }

   public void updateScreen() {
      super.updateScreen();
   }

   public void initGui() {
      super.initGui();
   }

   protected void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      ConfigManager.sync("xat", Type.INSTANCE);
      this.mc.player.closeScreen();
   }

   protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      super.actionPerformed(button);
   }

   public void onGuiClosed() {
      super.onGuiClosed();
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.oldMouseX = (float)mouseX;
      this.oldMouseY = (float)mouseY;
      TrinketsConfig.CLIENT.MPBar.translatedX = (double)mouseX * 100.0D / (double)this.width * 0.01D;
      TrinketsConfig.CLIENT.MPBar.translatedY = (double)mouseY * 100.0D / (double)this.height * 0.01D;
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   protected void keyTyped(char par1, int par2) throws IOException {
      ConfigManager.sync("xat", Type.INSTANCE);
      super.keyTyped(par1, par2);
   }

   public void displayNormalInventory() {
      GuiInventory gui = new GuiInventory(this.mc.player);
      this.mc.displayGuiScreen(gui);
   }
}
