package ivorius.ivtoolkit.gui;

public class GuiSliderRange extends GuiSliderMultivalue {
   public GuiSliderRange(int id, int x, int y, int width, int height, String displayString) {
      super(id, x, y, width, height, 2, displayString);
   }

   public void setRange(FloatRange range) {
      boolean firstLower = this.getValue(0) < this.getValue(1);
      this.setValue(firstLower ? 0 : 1, range.getMin());
      this.setValue(firstLower ? 1 : 0, range.getMax());
   }

   public FloatRange getRange() {
      return new FloatRange(Math.min(this.getValue(0), this.getValue(1)), Math.max(this.getValue(0), this.getValue(1)));
   }
}
