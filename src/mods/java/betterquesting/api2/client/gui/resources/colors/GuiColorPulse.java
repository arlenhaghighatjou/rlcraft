package betterquesting.api2.client.gui.resources.colors;

import betterquesting.api.utils.RenderUtils;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class GuiColorPulse implements IGuiColor {
   private static final double RAD = Math.toRadians(360.0D);
   private final IGuiColor c1;
   private final IGuiColor c2;
   private final double period;
   private final float phase;

   public GuiColorPulse(int color1, int color2, double period, float phase) {
      this(new GuiColorStatic(color1), new GuiColorStatic(color2), period, phase);
   }

   public GuiColorPulse(IGuiColor color1, IGuiColor color2, double period, float phase) {
      this.c1 = color1;
      this.c2 = color2;
      this.period = period;
      this.phase = phase;
   }

   public int getRGB() {
      double pms = 1000.0D * this.period;
      double time = (double)System.currentTimeMillis() % pms;
      time = (time + pms * (double)this.phase) % pms / pms;
      float blend = (float)(Math.cos(time * RAD) / 2.0D + 0.5D);
      return RenderUtils.lerpRGB(this.c1.getRGB(), this.c2.getRGB(), blend);
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
      int color = this.getRGB();
      float a = (float)(color >> 24 & 255) / 255.0F;
      float r = (float)(color >> 16 & 255) / 255.0F;
      float g = (float)(color >> 8 & 255) / 255.0F;
      float b = (float)(color & 255) / 255.0F;
      GlStateManager.color(r, g, b, a);
   }
}
