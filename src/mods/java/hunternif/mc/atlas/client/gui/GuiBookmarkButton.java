package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.core.GuiToggleButton;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;

public class GuiBookmarkButton extends GuiToggleButton {
   private static final int IMAGE_WIDTH = 84;
   private static final int IMAGE_HEIGHT = 36;
   private static final int WIDTH = 21;
   private static final int HEIGHT = 18;
   private static final int ICON_WIDTH = 16;
   private static final int ICON_HEIGHT = 16;
   private final int colorIndex;
   private ResourceLocation iconTexture;
   private String title;

   GuiBookmarkButton(int colorIndex, ResourceLocation iconTexture, String title) {
      this.colorIndex = colorIndex;
      this.setIconTexture(iconTexture);
      this.setTitle(title);
      this.setSize(21, 18);
   }

   void setIconTexture(ResourceLocation iconTexture) {
      this.iconTexture = iconTexture;
   }

   void setTitle(String title) {
      this.title = title;
   }

   String getTitle() {
      return this.title;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTick) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      RenderHelper.disableStandardItemLighting();
      int u = this.colorIndex * 21;
      int v = !this.isMouseOver && !this.isSelected() ? 18 : 0;
      AtlasRenderHelper.drawTexturedRect(Textures.BOOKMARKS, (double)this.getGuiX(), (double)this.getGuiY(), u, v, 21, 18, 84, 36);
      AtlasRenderHelper.drawFullTexture(this.iconTexture, (double)(this.getGuiX() + (!this.isMouseOver && !this.isSelected() ? 2 : 3)), (double)(this.getGuiY() + 1), 16, 16);
      if (this.isMouseOver) {
         this.drawTooltip(Collections.singletonList(this.title), Minecraft.getMinecraft().fontRenderer);
      }

   }
}
