package net.ilexiconn.llibrary.server.property.wrapper;

import java.text.DecimalFormat;
import net.ilexiconn.llibrary.server.property.IDoubleProperty;

public class DoublePropertyWrapper extends DoublePropertyWrapperBase<IDoubleProperty> {
   public DoublePropertyWrapper(double value, DecimalFormat decimalFormat) {
      this(new IDoubleProperty.WithState(value), decimalFormat);
   }

   public DoublePropertyWrapper(IDoubleProperty delegateFor, DecimalFormat decimalFormat) {
      super(delegateFor, decimalFormat);
   }
}
