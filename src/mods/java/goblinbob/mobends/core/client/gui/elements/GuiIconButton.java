package goblinbob.mobends.core.client.gui.elements;

import goblinbob.mobends.core.client.gui.GuiBendsMenu;
import goblinbob.mobends.core.util.Draw;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;

public class GuiIconButton {
   public static final int WIDTH = 20;
   public static final int HEIGHT = 20;
   protected int x = 0;
   protected int y = 0;
   protected int iconU;
   protected int iconV;
   protected int iconWidth;
   protected int iconHeight;
   protected boolean hovered;

   public GuiIconButton(int iconU, int iconV, int iconWidth, int iconHeight) {
      this.iconU = iconU;
      this.iconV = iconV;
      this.iconWidth = iconWidth;
      this.iconHeight = iconHeight;
      this.hovered = false;
   }

   public void initGui(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void update(int mouseX, int mouseY) {
      this.hovered = mouseX >= this.x && mouseX <= this.x + 20 && mouseY >= this.y && mouseY <= this.y + 20;
   }

   public void display() {
      int bgTextureY = this.hovered ? 64 : 44;
      Minecraft.getMinecraft().getTextureManager().bindTexture(GuiBendsMenu.ICONS_TEXTURE);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableAlpha();
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      Draw.texturedModalRect(this.x, this.y, 88, bgTextureY, 20, 20);
      Draw.texturedModalRect(this.x + 10 - this.iconWidth / 2, this.y + 10 - this.iconHeight / 2, this.iconU, this.iconV, this.iconWidth, this.iconHeight);
   }

   public boolean mouseClicked(int mouseX, int mouseY, int state) {
      return this.hovered;
   }
}
