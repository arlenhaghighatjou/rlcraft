package net.ilexiconn.llibrary.server.property.wrapper;

import java.text.DecimalFormat;
import net.ilexiconn.llibrary.server.property.IDoubleRangeProperty;
import net.ilexiconn.llibrary.server.property.IFloatRangeProperty;
import net.ilexiconn.llibrary.server.property.IIntRangeProperty;

public class DoubleRangePropertyWrapper extends DoublePropertyWrapperBase<IDoubleRangeProperty> implements IDoubleRangeProperty, IFloatRangeProperty, IIntRangeProperty {
   public DoubleRangePropertyWrapper(double value, double minDoubleValue, double maxDoubleValue, DecimalFormat decimalFormat) {
      super(new IDoubleRangeProperty.WithState(value, minDoubleValue, maxDoubleValue), decimalFormat);
   }

   public DoubleRangePropertyWrapper(IDoubleRangeProperty delegateFor, DecimalFormat decimalFormat) {
      super(delegateFor, decimalFormat);
   }

   public double getMinDoubleValue() {
      return ((IDoubleRangeProperty)this.delegateFor).getMinDoubleValue();
   }

   public double getMaxDoubleValue() {
      return ((IDoubleRangeProperty)this.delegateFor).getMaxDoubleValue();
   }

   public float getMinFloatValue() {
      return (float)this.getMinDoubleValue();
   }

   public float getMaxFloatValue() {
      return (float)this.getMaxDoubleValue();
   }

   public int getMinIntValue() {
      return (int)Math.ceil(this.getMinDoubleValue());
   }

   public int getMaxIntValue() {
      return (int)Math.floor(this.getMaxDoubleValue());
   }
}
