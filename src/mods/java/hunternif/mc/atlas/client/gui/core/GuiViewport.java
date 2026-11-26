package hunternif.mc.atlas.client.gui.core;

import hunternif.mc.atlas.util.AtlasRenderHelper;
import java.io.IOException;
import net.minecraft.client.gui.ScaledResolution;

public class GuiViewport extends GuiComponent {
   final GuiComponent content = new GuiComponent();
   private int screenScale;

   public GuiViewport() {
      this.addChild(this.content);
   }

   public GuiComponent addContent(GuiComponent child) {
      return this.content.addChild(child);
   }

   public GuiComponent removeContent(GuiComponent child) {
      return this.content.removeChild(child);
   }

   public void removeAllContent() {
      this.content.removeAllChildren();
   }

   public void initGui() {
      super.initGui();
      this.screenScale = (new ScaledResolution(this.mc)).getScaleFactor();
   }

   public void drawScreen(int mouseX, int mouseY, float par3) {
      AtlasRenderHelper.beginClip(this.getGuiX(), this.getGuiY(), this.properWidth, this.properHeight);
      super.drawScreen(mouseX, mouseY, par3);
      AtlasRenderHelper.endClip();
   }

   public void handleMouseInput() throws IOException {
      if (this.isMouseInRegion(this.getGuiX(), this.getGuiY(), this.properWidth, this.properHeight)) {
         super.handleMouseInput();
      }

   }

   public int getWidth() {
      return this.properWidth;
   }

   public int getHeight() {
      return this.properHeight;
   }

   protected void validateSize() {
      super.validateSize();

      for(GuiComponent child : this.getChildren()) {
         if (child.getGuiY() <= this.getGuiY() + this.properHeight && child.getGuiY() + child.getHeight() >= this.getGuiY() && child.getGuiX() <= this.getGuiX() + this.properWidth && child.getGuiX() + child.getWidth() >= this.getGuiX()) {
            child.setClipped(false);
         } else {
            child.setClipped(true);
         }
      }

   }
}
