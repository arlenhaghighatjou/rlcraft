package hunternif.mc.atlas.client.gui;

import hunternif.mc.atlas.client.Textures;
import hunternif.mc.atlas.client.gui.core.GuiToggleButton;
import hunternif.mc.atlas.registry.MarkerType;
import hunternif.mc.atlas.util.AtlasRenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiMarkerInList extends GuiToggleButton {
   public static final int FRAME_SIZE = 34;
   private final MarkerType markerType;

   public GuiMarkerInList(MarkerType markerType) {
      this.markerType = markerType;
      this.setSize(34, 34);
   }

   public MarkerType getMarkerType() {
      return this.markerType;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTick) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      AtlasRenderHelper.drawFullTexture(this.isSelected() ? Textures.MARKER_FRAME_ON : Textures.MARKER_FRAME_OFF, (double)this.getGuiX(), (double)this.getGuiY(), 34, 34);
      ResourceLocation texture = this.markerType.getIcon();
      if (texture != null) {
         AtlasRenderHelper.drawFullTexture(texture, (double)(this.getGuiX() + 1), (double)(this.getGuiY() + 1), 32, 32);
      }

      super.drawScreen(mouseX, mouseY, partialTick);
   }
}
