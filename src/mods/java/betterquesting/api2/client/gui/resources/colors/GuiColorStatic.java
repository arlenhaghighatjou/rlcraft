package betterquesting.api2.client.gui.resources.colors;

import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;

public class GuiColorStatic implements IGuiColor {
   private final int argb;

   public GuiColorStatic(int color) {
      this.argb = color;
   }

   public GuiColorStatic(int red, int green, int blue, int alpha) {
      this.argb = (alpha & 255) << 24 | (red & 255) << 16 | (green & 255) << 8 | blue & 255;
   }

   public GuiColorStatic(float red, float green, float blue, float alpha) {
      this((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
   }

   public GuiColorStatic(Color color) {
      this(color.getRGB());
   }

   public int getRGB() {
      return this.argb;
   }

   public float getRed() {
      return (float)(this.argb >> 16 & 255) / 255.0F;
   }

   public float getGreen() {
      return (float)(this.argb >> 8 & 255) / 255.0F;
   }

   public float getBlue() {
      return (float)(this.argb & 255) / 255.0F;
   }

   public float getAlpha() {
      return (float)(this.argb >> 24 & 255) / 255.0F;
   }

   public void applyGlColor() {
      GlStateManager.color(this.getRed(), this.getGreen(), this.getBlue(), this.getAlpha());
   }
}
