package ivorius.ivtoolkit.gui;

import net.minecraft.util.math.MathHelper;

public class IntegerRange {
   public final int min;
   public final int max;

   public IntegerRange(int min, int max) {
      this.min = min;
      this.max = max;
   }

   /** @deprecated */
   @Deprecated
   public IntegerRange(FloatRange floatRange) {
      this.min = MathHelper.floor(floatRange.getMin());
      this.max = MathHelper.floor(floatRange.getMax());
   }

   public int getMin() {
      return this.min;
   }

   public int getMax() {
      return this.max;
   }

   public String toString() {
      return "IntegerRange{min=" + this.min + ", max=" + this.max + '}';
   }
}
