package vazkii.quark.oddities.client.gui.button;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import vazkii.quark.oddities.client.gui.GuiMatrixEnchanting;

public class GuiButtonMatrixEnchantingPlus extends GuiButton {
   public GuiButtonMatrixEnchantingPlus(int x, int y) {
      super(0, x, y, 50, 12, "");
   }

   public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
      if (this.visible) {
         mc.renderEngine.bindTexture(GuiMatrixEnchanting.BACKGROUND);
         int u = 0;
         int v = 177;
         if (!this.enabled) {
            v += 12;
         } else if (this.hovered) {
            v += 24;
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F);
         this.drawTexturedModalRect(this.x, this.y, u, v, this.width, this.height);
      }
   }
}
