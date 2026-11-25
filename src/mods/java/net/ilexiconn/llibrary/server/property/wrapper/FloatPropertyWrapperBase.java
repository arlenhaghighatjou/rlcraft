package net.ilexiconn.llibrary.server.property.wrapper;

import java.text.DecimalFormat;
import java.text.ParseException;
import net.ilexiconn.llibrary.server.property.IDoubleProperty;
import net.ilexiconn.llibrary.server.property.IFloatProperty;
import net.ilexiconn.llibrary.server.property.IIntProperty;
import net.ilexiconn.llibrary.server.property.IStringProperty;

public abstract class FloatPropertyWrapperBase<T extends IFloatProperty> implements IFloatProperty, IDoubleProperty, IIntProperty, IStringProperty {
   protected final T delegateFor;
   protected final DecimalFormat decimalFormat;

   public FloatPropertyWrapperBase(T delegateFor, DecimalFormat decimalFormat) {
      this.delegateFor = delegateFor;
      this.decimalFormat = decimalFormat;
   }

   public float getFloat() {
      return this.delegateFor.getFloat();
   }

   public void setFloat(float value) {
      this.delegateFor.setFloat(value);
   }

   public boolean isValidFloat(float value) {
      return this.delegateFor.isValidFloat(value);
   }

   public double getDouble() {
      return (double)this.getFloat();
   }

   public void setDouble(double value) {
      this.setFloat((float)value);
   }

   public boolean isValidDouble(double value) {
      return this.isValidFloat((float)value);
   }

   public int getInt() {
      return Math.round(this.getFloat());
   }

   public void setInt(int value) {
      this.setFloat((float)value);
   }

   public boolean isValidInt(int value) {
      return this.isValidFloat((float)value);
   }

   public String getString() {
      return this.decimalFormat.format((double)this.getFloat());
   }

   public void setString(String value) {
      try {
         this.setFloat(this.decimalFormat.parse(value).floatValue());
      } catch (ParseException var3) {
         ;
      }

   }

   public boolean isValidString(String value) {
      try {
         return this.isValidFloat(this.decimalFormat.parse(value).floatValue());
      } catch (ParseException var3) {
         return false;
      }
   }
}
