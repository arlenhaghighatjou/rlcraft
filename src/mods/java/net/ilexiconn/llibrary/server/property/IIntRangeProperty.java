package net.ilexiconn.llibrary.server.property;

public interface IIntRangeProperty extends IIntProperty {
   default boolean isValidInt(int value) {
      return value >= this.getMinIntValue() && value <= this.getMaxIntValue();
   }

   int getMinIntValue();

   int getMaxIntValue();

   default int getIntValueRange() {
      return this.getMaxIntValue() - this.getMinIntValue();
   }

   public static class WithState extends IIntProperty.WithState implements IIntRangeProperty {
      private final int minIntValue;
      private final int maxIntValue;

      public WithState(int value, int minIntValue, int maxIntValue) {
         super(value);
         this.minIntValue = minIntValue;
         this.maxIntValue = maxIntValue;
      }

      public int getMinIntValue() {
         return this.minIntValue;
      }

      public int getMaxIntValue() {
         return this.maxIntValue;
      }
   }
}
