package net.ilexiconn.llibrary.server.property.wrapper;

import java.text.NumberFormat;
import java.text.ParseException;
import net.ilexiconn.llibrary.server.property.IDoubleProperty;
import net.ilexiconn.llibrary.server.property.IFloatProperty;
import net.ilexiconn.llibrary.server.property.IIntProperty;
import net.ilexiconn.llibrary.server.property.IStringProperty;

public abstract class IntPropertyWrapperBase<T extends IIntProperty> implements IIntProperty, IDoubleProperty, IFloatProperty, IStringProperty {
   protected final T delegateFor;
   protected final NumberFormat numberFormat;

   public IntPropertyWrapperBase(T delegateFor, NumberFormat numberFormat) {
      this.delegateFor = delegateFor;
      this.numberFormat = numberFormat;
   }

   public int getInt() {
      return this.delegateFor.getInt();
   }

   public void setInt(int value) {
      this.delegateFor.setInt(value);
   }

   public boolean isValidInt(int value) {
      return this.delegateFor.isValidInt(value);
   }

   public double getDouble() {
      return (double)this.getInt();
   }

   public void setDouble(double value) {
      this.setInt((int)Math.round(value));
   }

   public boolean isValidDouble(double value) {
      return this.isValidInt((int)Math.round(value));
   }

   public float getFloat() {
      return (float)this.getInt();
   }

   public void setFloat(float value) {
      this.setInt(Math.round(value));
   }

   public boolean isValidFloat(float value) {
      return this.isValidInt(Math.round(value));
   }

   public String getString() {
      return this.numberFormat.format((long)this.getInt());
   }

   public void setString(String value) {
      try {
         this.setInt(this.numberFormat.parse(value).intValue());
      } catch (ParseException var3) {
         ;
      }

   }

   public boolean isValidString(String value) {
      try {
         return this.isValidInt(this.numberFormat.parse(value).intValue());
      } catch (ParseException var3) {
         return false;
      }
   }
}
