package net.ilexiconn.llibrary.server.property;

public interface IDoubleRangeProperty extends IDoubleProperty {
   default boolean isValidDouble(double value) {
      return value >= this.getMinDoubleValue() && value <= this.getMaxDoubleValue();
   }

   double getMinDoubleValue();

   double getMaxDoubleValue();

   default double getDoubleValueRange() {
      return this.getMaxDoubleValue() - this.getMinDoubleValue();
   }

   public static class WithState extends IDoubleProperty.WithState implements IDoubleRangeProperty {
      private final double minDoubleValue;
      private final double maxDoubleValue;

      public WithState(double value, double minDoubleValue, double maxDoubleValue) {
         super(value);
         this.minDoubleValue = minDoubleValue;
         this.maxDoubleValue = maxDoubleValue;
      }

      public double getMinDoubleValue() {
         return this.minDoubleValue;
      }

      public double getMaxDoubleValue() {
         return this.maxDoubleValue;
      }
   }
}
