package net.ilexiconn.llibrary.server.property.wrapper;

import java.util.HashSet;
import java.util.Set;
import net.ilexiconn.llibrary.server.property.IBooleanProperty;
import net.ilexiconn.llibrary.server.property.IStringSelectionProperty;

public class BooleanPropertyWrapper implements IBooleanProperty, IStringSelectionProperty {
   private final IBooleanProperty delegateFor;
   private final String trueName;
   private final String falseName;

   public BooleanPropertyWrapper(boolean value, String trueName, String falseName) {
      this(new IBooleanProperty.WithState(value), trueName, falseName);
   }

   public BooleanPropertyWrapper(IBooleanProperty delegateFor, String trueName, String falseName) {
      this.delegateFor = delegateFor;
      this.trueName = trueName;
      this.falseName = falseName;
   }

   public boolean getBoolean() {
      return this.delegateFor.getBoolean();
   }

   public void setBoolean(boolean value) {
      this.delegateFor.setBoolean(value);
   }

   public boolean isValidBoolean(boolean value) {
      return true;
   }

   public String getString() {
      return this.getBoolean() ? this.trueName : this.falseName;
   }

   public void setString(String value) {
      if (value.equals(this.trueName)) {
         this.setBoolean(true);
      } else if (value.equals(this.falseName)) {
         this.setBoolean(false);
      }

   }

   public boolean isValidString(String value) {
      if (value.equals(this.trueName)) {
         return this.isValidBoolean(true);
      } else {
         return value.equals(this.falseName) ? this.isValidBoolean(false) : this.isValidBoolean(Boolean.parseBoolean(value));
      }
   }

   public Set<String> getValidStringValues() {
      Set<String> result = new HashSet();
      if (this.isValidBoolean(true)) {
         result.add(this.trueName);
      }

      if (this.isValidBoolean(false)) {
         result.add(this.falseName);
      }

      return result;
   }
}
