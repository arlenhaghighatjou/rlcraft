package hunternif.mc.atlas.client.gui.core;

import java.io.IOException;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public abstract class AGuiScrollbar extends GuiComponent {
   ResourceLocation texture;
   int textureWidth;
   int textureHeight;
   int capLength;
   int textureBodyLength;
   private static int scrollStep = 18;
   boolean visible = false;
   private boolean isDragged = false;
   private boolean wasClicking = false;
   private boolean usesWheel = true;
   private float contentRatio = 1.0F;
   private float scrollRatio = 0.0F;
   int anchorPos = 0;
   int anchorSize;
   double bodyTextureScale = 1.0D;
   int scrollPos = 0;
   final GuiViewport viewport;

   AGuiScrollbar(GuiViewport viewport) {
      this.viewport = viewport;
   }

   public void setTexture(ResourceLocation texture, int width, int height, int capLength) {
      this.texture = texture;
      this.textureWidth = width;
      this.textureHeight = height;
      this.capLength = capLength;
      this.textureBodyLength = this.getTextureLength() - capLength * 2;
      this.setScrollbarWidth(width, height);
   }

   public void setUsesWheel(boolean value) {
      this.usesWheel = value;
   }

   public void updateContent() {
      this.contentRatio = (float)this.getViewportSize() / (float)this.getContentSize();
      this.visible = this.contentRatio < 1.0F;
      this.updateAnchorSize();
      this.updateAnchorPos();
   }

   public void setScrollPos(int scrollPos) {
      this.viewport.content.validateSize();
      this.viewport.validateSize();
      this.doSetScrollPos(scrollPos);
   }

   private void doSetScrollPos(int scrollPos) {
      scrollPos = Math.max(0, Math.min(scrollPos, this.getContentSize() - this.getViewportSize()));
      this.scrollPos = scrollPos;
      this.scrollRatio = (float)scrollPos / (float)(this.getContentSize() - this.getViewportSize());
      this.updateAnchorPos();
   }

   public void setScrollRatio(float scrollRatio) {
      this.viewport.content.validateSize();
      this.viewport.validateSize();
      this.doSetScrollRatio(scrollRatio);
   }

   private void doSetScrollRatio(float scrollRatio) {
      if (scrollRatio < 0.0F) {
         scrollRatio = 0.0F;
      }

      if (scrollRatio > 1.0F) {
         scrollRatio = 1.0F;
      }

      this.scrollRatio = scrollRatio;
      this.scrollPos = Math.round(scrollRatio * (float)(this.getContentSize() - this.getViewportSize()));
      this.updateAnchorPos();
   }

   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      if (this.usesWheel) {
         int wheelMove = Mouse.getEventDWheel();
         if (wheelMove != 0 && this.visible) {
            wheelMove = wheelMove > 0 ? -1 : 1;
            this.doSetScrollPos(this.scrollPos + wheelMove * scrollStep);
         }
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTick) {
      if (!this.visible) {
         this.isDragged = false;
      } else {
         boolean mouseDown = Mouse.isButtonDown(0);
         if (!this.wasClicking && mouseDown && this.isMouseOver) {
            this.isDragged = true;
         }

         if (!mouseDown) {
            this.isDragged = false;
         }

         this.wasClicking = mouseDown;
         if (this.isDragged) {
            this.doSetScrollRatio((float)(this.getMousePos(mouseX, mouseY) - this.anchorSize / 2) / (float)(this.getScrollbarLength() - this.anchorSize));
         }

         GlStateManager.enableTexture2D();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.drawAnchor();
         GlStateManager.disableBlend();
      }
   }

   private void updateAnchorSize() {
      this.anchorSize = Math.max(this.capLength * 2, Math.round(Math.min(1.0F, this.contentRatio) * (float)this.getScrollbarLength()));
      this.bodyTextureScale = (double)(this.anchorSize - this.capLength * 2) / (double)this.textureBodyLength;
   }

   private void updateAnchorPos() {
      this.anchorPos = Math.round(this.scrollRatio * (float)(this.getViewportSize() - this.anchorSize));
      this.updateContentPos();
   }

   protected abstract int getTextureLength();

   protected abstract int getScrollbarLength();

   protected abstract int getViewportSize();

   protected abstract int getContentSize();

   protected abstract int getMousePos(int var1, int var2);

   protected abstract void drawAnchor();

   protected abstract void updateContentPos();

   protected abstract void setScrollbarWidth(int var1, int var2);
}
