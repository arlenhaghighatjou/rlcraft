package net.ilexiconn.llibrary.server.property.wrapper;

import java.text.DecimalFormat;
import net.ilexiconn.llibrary.server.property.IDoubleRangeProperty;
import net.ilexiconn.llibrary.server.property.IFloatRangeProperty;
import net.ilexiconn.llibrary.server.property.IIntRangeProperty;

public class FloatRangePropertyWrapper extends FloatPropertyWrapperBase<IFloatRangeProperty> implements IFloatRangeProperty, IDoubleRangeProperty, IIntRangeProperty {
   public FloatRangePropertyWrapper(float value, float minFloatValue, float maxFloatValue, DecimalFormat decimalFormat) {
      this(new IFloatRangeProperty.WithState(value, minFloatValue, maxFloatValue), decimalFormat);
   }

   public FloatRangePropertyWrapper(IFloatRangeProperty delegateFor, DecimalFormat decimalFormat) {
      super(delegateFor, decimalFormat);
   }

   public float getMinFloatValue() {
      return ((IFloatRangeProperty)this.delegateFor).getMinFloatValue();
   }

   public float getMaxFloatValue() {
      return ((IFloatRangeProperty)this.delegateFor).getMaxFloatValue();
   }

   public double getMinDoubleValue() {
      return (double)this.getMinFloatValue();
   }

   public double getMaxDoubleValue() {
      return (double)this.getMaxFloatValue();
   }

   public int getMinIntValue() {
      return (int)Math.ceil((double)this.getMinFloatValue());
   }

   public int getMaxIntValue() {
      return (int)Math.floor((double)this.getMaxFloatValue());
   }
}
