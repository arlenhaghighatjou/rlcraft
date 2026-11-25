package goblinbob.mobends.core.client.gui.elements;

import goblinbob.mobends.core.client.gui.GuiBendsMenu;
import goblinbob.mobends.core.util.Draw;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class GuiHelpButton {
   public static final int WIDTH = 20;
   public static final int HEIGHT = 20;
   protected int x = 0;
   protected int y = 0;
   protected boolean hovered = false;

   public GuiHelpButton() {
   }

   public void initGui(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void update(int mouseX, int mouseY) {
      this.hovered = mouseX >= this.x && mouseX <= this.x + 20 && mouseY >= this.y && mouseY <= this.y + 20;
   }

   public void display() {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Minecraft.getMinecraft().getTextureManager().bindTexture(GuiBendsMenu.ICONS_TEXTURE);
      int textureY = this.hovered ? 64 : 44;
      Draw.texturedModalRect(this.x, this.y, 88, textureY, 20, 20);
   }

   public boolean mouseClicked(int mouseX, int mouseY, int state) {
      return this.hovered;
   }
}
