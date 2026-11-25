package net.ilexiconn.llibrary.server.property.wrapper;

import java.text.NumberFormat;
import net.ilexiconn.llibrary.server.property.IDoubleRangeProperty;
import net.ilexiconn.llibrary.server.property.IFloatRangeProperty;
import net.ilexiconn.llibrary.server.property.IIntRangeProperty;

public class IntRangePropertyWrapper extends IntPropertyWrapperBase<IIntRangeProperty> implements IIntRangeProperty, IDoubleRangeProperty, IFloatRangeProperty {
   public IntRangePropertyWrapper(int value, int minFloatValue, int maxFloatValue, NumberFormat numberFormat) {
      this(new IIntRangeProperty.WithState(value, minFloatValue, maxFloatValue), numberFormat);
   }

   public IntRangePropertyWrapper(IIntRangeProperty delegateFor, NumberFormat numberFormat) {
      super(delegateFor, numberFormat);
   }

   public int getMinIntValue() {
      return ((IIntRangeProperty)this.delegateFor).getMinIntValue();
   }

   public int getMaxIntValue() {
      return ((IIntRangeProperty)this.delegateFor).getMaxIntValue();
   }

   public double getMinDoubleValue() {
      return (double)this.getMinIntValue();
   }

   public double getMaxDoubleValue() {
      return (double)this.getMaxIntValue();
   }

   public float getMinFloatValue() {
      return (float)this.getMinIntValue();
   }

   public float getMaxFloatValue() {
      return (float)this.getMaxIntValue();
   }
}
