package ivorius.ivtoolkit.gui;

public class FloatRange {
   public final float min;
   public final float max;

   public FloatRange(float min, float max) {
      this.min = min;
      this.max = max;
   }

   public FloatRange(IntegerRange integerRange) {
      this.min = (float)integerRange.getMin();
      this.max = (float)integerRange.getMax();
   }

   public float getMin() {
      return this.min;
   }

   public float getMax() {
      return this.max;
   }

   public String toString() {
      return "FloatRange{min=" + this.min + ", max=" + this.max + '}';
   }
}
