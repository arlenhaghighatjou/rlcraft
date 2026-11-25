package betterquesting.api2.client.gui.resources.colors;

import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.controls.IValueIO;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class GuiColorTransition implements IGuiColor {
   private final IGuiColor cNorm;
   private final IGuiColor cLow;
   private boolean useLerp = true;
   private float threshold = 1.0F;
   private IValueIO<Float> driver = null;

   public GuiColorTransition(IGuiColor cNorm, IGuiColor cLow) {
      this.cNorm = cNorm;
      this.cLow = cLow;
   }

   public GuiColorTransition setupBlending(boolean enable, float threshold) {
      this.useLerp = enable;
      this.threshold = threshold;
      return this;
   }

   public GuiColorTransition setBlendDriver(IValueIO<Float> driver) {
      this.driver = driver;
      return this;
   }

   public int getRGB() {
      if (this.driver != null && ((Float)this.driver.readValue()).floatValue() < this.threshold) {
         return !this.useLerp ? this.cLow.getRGB() : RenderUtils.lerpRGB(this.cLow.getRGB(), this.cNorm.getRGB(), ((Float)this.driver.readValue()).floatValue() / this.threshold);
      } else {
         return this.cNorm.getRGB();
      }
   }

   public float getRed() {
      return (float)(this.getRGB() >> 16 & 255) / 255.0F;
   }

   public float getGreen() {
      return (float)(this.getRGB() >> 8 & 255) / 255.0F;
   }

   public float getBlue() {
      return (float)(this.getRGB() & 255) / 255.0F;
   }

   public float getAlpha() {
      return (float)(this.getRGB() >> 24 & 255) / 255.0F;
   }

   public void applyGlColor() {
      int argb = this.getRGB();
      float r = (float)(argb >> 16 & 255) / 255.0F;
      float g = (float)(argb >> 8 & 255) / 255.0F;
      float b = (float)(argb & 255) / 255.0F;
      float a = (float)(argb >> 24 & 255) / 255.0F;
      GlStateManager.color(r, g, b, a);
   }
}
