package net.ilexiconn.llibrary.server.property.wrapper;

import java.text.DecimalFormat;
import net.ilexiconn.llibrary.server.property.IFloatProperty;

public class FloatPropertyWrapper extends FloatPropertyWrapperBase<IFloatProperty> {
   public FloatPropertyWrapper(float value, DecimalFormat decimalFormat) {
      this(new IFloatProperty.WithState(value), decimalFormat);
   }

   public FloatPropertyWrapper(IFloatProperty delegateFor, DecimalFormat decimalFormat) {
      super(delegateFor, decimalFormat);
   }
}
