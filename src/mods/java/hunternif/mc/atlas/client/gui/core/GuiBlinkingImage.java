package hunternif.mc.atlas.client.gui.core;

import hunternif.mc.atlas.util.AtlasRenderHelper;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiBlinkingImage extends GuiComponent {
   private ResourceLocation texture;
   private long blinkTime = 500L;
   private float visibleAlpha = 1.0F;
   private float invisibleAlpha = 0.25F;
   private long lastTickTime;
   private boolean isVisible;

   public GuiBlinkingImage() {
   }

   public void setTexture(ResourceLocation texture, int width, int height) {
      this.texture = texture;
      this.setSize(width, height);
      this.lastTickTime = 0L;
      this.isVisible = false;
   }

   public void setBlinkTime(long blinkTime) {
      this.blinkTime = blinkTime;
   }

   public void setVisibleAlpha(float visibleAlpha) {
      this.visibleAlpha = visibleAlpha;
   }

   public void setInvisibleAlpha(float invisibleAlpha) {
      this.invisibleAlpha = invisibleAlpha;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTick) {
      long currentTime = System.currentTimeMillis();
      if (this.lastTickTime + this.blinkTime < currentTime) {
         this.lastTickTime = currentTime;
         this.isVisible = !this.isVisible;
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, this.isVisible ? this.visibleAlpha : this.invisibleAlpha);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      this.drawImage();
   }

   private void drawImage() {
      AtlasRenderHelper.drawFullTexture(this.texture, (double)this.getGuiX(), (double)this.getGuiY(), this.getWidth(), this.getHeight());
   }
}
