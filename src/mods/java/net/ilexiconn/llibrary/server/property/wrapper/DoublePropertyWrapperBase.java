package net.ilexiconn.llibrary.server.property.wrapper;

import java.text.DecimalFormat;
import java.text.ParseException;
import net.ilexiconn.llibrary.server.property.IDoubleProperty;
import net.ilexiconn.llibrary.server.property.IFloatProperty;
import net.ilexiconn.llibrary.server.property.IIntProperty;
import net.ilexiconn.llibrary.server.property.IStringProperty;

public abstract class DoublePropertyWrapperBase<T extends IDoubleProperty> implements IDoubleProperty, IFloatProperty, IIntProperty, IStringProperty {
   protected final T delegateFor;
   protected final DecimalFormat decimalFormat;

   public DoublePropertyWrapperBase(T delegateFor, DecimalFormat decimalFormat) {
      this.delegateFor = delegateFor;
      this.decimalFormat = decimalFormat;
   }

   public double getDouble() {
      return this.delegateFor.getDouble();
   }

   public void setDouble(double value) {
      this.delegateFor.setDouble(value);
   }

   public boolean isValidDouble(double value) {
      return this.delegateFor.isValidDouble(value);
   }

   public float getFloat() {
      return (float)this.getDouble();
   }

   public void setFloat(float value) {
      this.setDouble((double)value);
   }

   public boolean isValidFloat(float value) {
      return this.isValidDouble((double)value);
   }

   public int getInt() {
      return (int)Math.round(this.getDouble());
   }

   public void setInt(int value) {
      this.setDouble((double)value);
   }

   public boolean isValidInt(int value) {
      return this.isValidDouble((double)value);
   }

   public String getString() {
      return this.decimalFormat.format(this.getDouble());
   }

   public void setString(String value) {
      try {
         this.setDouble(this.decimalFormat.parse(value).doubleValue());
      } catch (ParseException var3) {
         ;
      }

   }

   public boolean isValidString(String value) {
      try {
         return this.isValidDouble(this.decimalFormat.parse(value).doubleValue());
      } catch (ParseException var3) {
         return false;
      }
   }
}
