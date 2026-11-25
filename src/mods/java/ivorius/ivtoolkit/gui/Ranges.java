package ivorius.ivtoolkit.gui;

import net.minecraft.util.math.MathHelper;

public class Ranges {
   public Ranges() {
   }

   public static IntegerRange roundedIntRange(FloatRange floatRange) {
      return new IntegerRange(MathHelper.floor(floatRange.getMin() + 0.5F), MathHelper.floor(floatRange.getMax() + 0.5F));
   }
}
