package betterquesting.api2.client.gui.resources.textures;

import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public class SlideShowTexture implements IGuiTexture {
   private final IGuiTexture[] slides;
   private final float interval;

   public SlideShowTexture(float interval, IGuiTexture... slides) {
      this.slides = slides;
      this.interval = interval;
   }

   public void drawTexture(int x, int y, int width, int height, float zLevel, float partialTick) {
      if (width > 0 && height > 0 && this.slides.length > 0) {
         IGuiTexture tex = this.getCurrentFrame();
         if (tex != null) {
            tex.drawTexture(x, y, width, height, zLevel, partialTick);
         }

      }
   }

   public void drawTexture(int x, int y, int width, int height, float zDepth, float partialTick, IGuiColor color) {
      if (width > 0 && height > 0 && this.slides.length > 0) {
         IGuiTexture tex = this.getCurrentFrame();
         if (tex != null) {
            tex.drawTexture(x, y, width, height, zDepth, partialTick, color);
         }

      }
   }

   /** @deprecated */
   @Deprecated
   public ResourceLocation getTexture() {
      IGuiTexture tex = this.getCurrentFrame();
      return tex == null ? null : tex.getTexture();
   }

   /** @deprecated */
   @Deprecated
   public IGuiRect getBounds() {
      IGuiTexture tex = this.getCurrentFrame();
      return tex == null ? null : tex.getBounds();
   }

   @Nullable
   public IGuiTexture getCurrentFrame() {
      return this.slides.length <= 0 ? null : this.slides[(int)Math.floor((double)System.currentTimeMillis() / 1000.0D % (double)((float)this.slides.length * this.interval) / (double)this.interval)];
   }

   public IGuiTexture[] getAllFrames() {
      return this.slides;
   }
}
