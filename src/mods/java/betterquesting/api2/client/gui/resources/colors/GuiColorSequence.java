package betterquesting.api2.client.gui.resources.colors;

public class GuiColorSequence implements IGuiColor {
   private final IGuiColor[] colors;
   private final float interval;

   public GuiColorSequence(float interval, IGuiColor... colors) {
      this.colors = colors;
      this.interval = interval;
   }

   public int getRGB() {
      return this.getCurrentColor().getRGB();
   }

   public float getRed() {
      return this.getCurrentColor().getRed();
   }

   public float getGreen() {
      return this.getCurrentColor().getGreen();
   }

   public float getBlue() {
      return this.getCurrentColor().getBlue();
   }

   public float getAlpha() {
      return this.getCurrentColor().getAlpha();
   }

   public void applyGlColor() {
      this.getCurrentColor().applyGlColor();
   }

   public IGuiColor getCurrentColor() {
      return this.colors.length <= 0 ? null : this.colors[(int)Math.floor((double)System.currentTimeMillis() / 1000.0D % (double)((float)this.colors.length * this.interval) / (double)this.interval)];
   }

   public IGuiColor[] getAllColors() {
      return this.colors;
   }
}
