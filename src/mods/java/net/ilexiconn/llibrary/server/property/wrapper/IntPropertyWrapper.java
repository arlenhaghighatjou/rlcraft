package net.ilexiconn.llibrary.server.property.wrapper;

import java.text.NumberFormat;
import net.ilexiconn.llibrary.server.property.IIntProperty;

public class IntPropertyWrapper extends IntPropertyWrapperBase<IIntProperty> {
   public IntPropertyWrapper(int value, NumberFormat numberFormat) {
      this(new IIntProperty.WithState(value), numberFormat);
   }

   public IntPropertyWrapper(IIntProperty delegateFor, NumberFormat numberFormat) {
      super(delegateFor, numberFormat);
   }
}
