package net.ilexiconn.llibrary.server.property;

import java.util.Set;

public interface IStringSelectionProperty extends IStringProperty {
   default boolean isValidString(String value) {
      Set<String> validStringValues = this.getValidStringValues();
      return validStringValues == null || validStringValues.contains(value);
   }

   Set<String> getValidStringValues();

   public static class WithState extends IStringProperty.WithState implements IStringSelectionProperty {
      private final Set<String> validValues;

      public WithState(String value) {
         this(value, (Set)null);
      }

      public WithState(String value, Set<String> validValues) {
         super(value);
         this.validValues = validValues;
      }

      public Set<String> getValidStringValues() {
         return this.validValues;
      }
   }
}
