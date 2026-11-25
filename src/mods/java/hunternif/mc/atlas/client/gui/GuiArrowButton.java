package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.core.GuiComponentButton;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;

public class GuiArrowButton extends GuiComponentButton {
   private static final int WIDTH = 12;
   private static final int HEIGHT = 12;
   private static final int IMAGE_WIDTH = 24;
   private static final int IMAGE_HEIGHT = 24;
   private final GuiArrowButton.ArrowDirection direction;

   private GuiArrowButton(GuiArrowButton.ArrowDirection direction) {
      this.setSize(12, 12);
      this.direction = direction;
   }

   static GuiArrowButton up() {
      return new GuiArrowButton(GuiArrowButton.ArrowDirection.UP);
   }

   static GuiArrowButton down() {
      return new GuiArrowButton(GuiArrowButton.ArrowDirection.DOWN);
   }

   static GuiArrowButton left() {
      return new GuiArrowButton(GuiArrowButton.ArrowDirection.LEFT);
   }

   static GuiArrowButton right() {
      return new GuiArrowButton(GuiArrowButton.ArrowDirection.RIGHT);
   }

   public void drawScreen(int mouseX, int mouseY, float partialTick) {
      RenderHelper.disableStandardItemLighting();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      int x = this.getGuiX();
      int y = this.getGuiY();
      if (this.isMouseOver) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         int distanceSq = (mouseX - x - this.getWidth() / 2) * (mouseX - x - this.getWidth() / 2) + (mouseY - y - this.getHeight() / 2) * (mouseY - y - this.getHeight() / 2);
         double alpha = distanceSq < 400 ? 0.5D : Math.pow((double)distanceSq, -0.28D);
         GlStateManager.color(1.0F, 1.0F, 1.0F, (float)alpha);
      }

      int u = 0;
      int v = 0;
      switch(this.direction) {
      case LEFT:
         u = 0;
         v = 0;
         break;
      case RIGHT:
         u = 0;
         v = 12;
         break;
      case UP:
         u = 12;
         v = 0;
         break;
      case DOWN:
         u = 12;
         v = 12;
      }

      AtlasRenderHelper.drawTexturedRect(Textures.BTN_ARROWS, (double)x, (double)y, u, v, 12, 12, 24, 24);
      GlStateManager.disableBlend();
   }

   public static enum ArrowDirection {
      UP("Up"),
      DOWN("Down"),
      LEFT("Left"),
      RIGHT("Right");

      public final String description;

      private ArrowDirection(String text) {
         this.description = text;
      }
   }
}
