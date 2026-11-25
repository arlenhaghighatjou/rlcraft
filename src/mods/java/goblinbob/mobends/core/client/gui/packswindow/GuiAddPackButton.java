package goblinbob.mobends.core.client.gui.packswindow;

import goblinbob.mobends.core.client.gui.elements.GuiElement;
import goblinbob.mobends.core.util.Draw;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class GuiAddPackButton extends GuiElement {
   private static final int WIDTH = 100;
   private static final int HEIGHT = 12;
   private boolean hover;
   private boolean enabled;

   public GuiAddPackButton(GuiElement parent, int x, int y) {
      super(parent, x, y);
      this.x = 0;
      this.y = 0;
      this.hover = false;
      this.enabled = true;
   }

   public void setPosition(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void update(int mouseX, int mouseY) {
      super.update(mouseX, mouseY);
      this.hover = mouseX >= this.x && mouseX <= this.x + 100 && mouseY >= this.y && mouseY <= this.y + 12;
   }

   protected void drawBackground(float partialTicks) {
      if (this.enabled) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(GuiPacksWindow.BACKGROUND_TEXTURE);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         int textureY = this.hover ? 105 : 93;
         Draw.texturedModalRect(0, 0, 0, textureY, 100, 12);
      }
   }

   protected void drawForeground(float partialTicks) {
   }

   public boolean mouseClicked(int mouseX, int mouseY, int button) {
      return !this.enabled ? false : (this.hover = mouseX >= this.x && mouseX <= this.x + 100 && mouseY >= this.y && mouseY <= this.y + 12);
   }

   public GuiAddPackButton enable() {
      this.enabled = true;
      return this;
   }

   public GuiAddPackButton disable() {
      this.enabled = false;
      return this;
   }
}
