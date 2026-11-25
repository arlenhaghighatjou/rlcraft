package goblinbob.mobends.core.client.gui.addonswindow;

import goblinbob.mobends.core.addon.Addons;
import goblinbob.mobends.core.addon.IAddon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiAddonsWindow extends Gui {
   public static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("mobends", "textures/gui/addons_window.png");
   private static final int WIDTH = 210;
   private static final int HEIGHT = 122;
   static final int SCROLLBAR_WIDTH = 5;
   private int x;
   private int y;
   private FontRenderer fontRenderer;

   public GuiAddonsWindow() {
      this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
   }

   public void initGui(int x, int y) {
      this.x = x - 105;
      this.y = y - 61;
   }

   public void onOpened() {
   }

   public void display(int mouseX, int mouseY, float partialTicks) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
      this.drawTexturedModalRect(this.x, this.y, 0, 0, 210, 122);
      this.drawCenteredString(this.fontRenderer, I18n.format("mobends.gui.addons", new Object[0]), this.x + 105, this.y + 4, 16777215);
      int y = this.y + 50;

      for(IAddon addon : Addons.getRegistered()) {
         this.drawCenteredString(this.fontRenderer, addon.getDisplayName(), this.x + 105, y, 16777215);
         y += 50;
      }

   }

   public void update(int mouseX, int mouseY) {
   }

   public boolean mouseClicked(int mouseX, int mouseY, int state) {
      return false;
   }

   public void mouseReleased(int mouseX, int mouseY, int event) {
   }
}
