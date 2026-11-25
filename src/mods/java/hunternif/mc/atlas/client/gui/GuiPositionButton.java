package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.core.GuiComponentButton;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;

public class GuiPositionButton extends GuiComponentButton {
   private static final int WIDTH = 11;
   private static final int HEIGHT = 11;

   public GuiPositionButton() {
      this.setSize(11, 11);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTick) {
      if (this.isEnabled()) {
         RenderHelper.disableStandardItemLighting();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         int x = this.getGuiX();
         int y = this.getGuiY();
         if (this.isMouseOver) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         } else {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
         }

         AtlasRenderHelper.drawFullTexture(Textures.BTN_POSITION, (double)x, (double)y, 11, 11);
         GlStateManager.disableBlend();
         if (this.isMouseOver) {
            this.drawTooltip(Collections.singletonList(I18n.format("gui.antiqueatlas.followPlayer", new Object[0])), Minecraft.getMinecraft().fontRenderer);
         }
      }

   }
}
