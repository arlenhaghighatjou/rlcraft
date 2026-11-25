package net.ilexiconn.llibrary.server.property;

public interface IFloatRangeProperty extends IFloatProperty {
   default boolean isValidFloat(float value) {
      return value >= this.getMinFloatValue() && value <= this.getMaxFloatValue();
   }

   float getMinFloatValue();

   float getMaxFloatValue();

   default float getFloatValueRange() {
      return this.getMaxFloatValue() - this.getMinFloatValue();
   }

   public static class WithState extends IFloatProperty.WithState implements IFloatRangeProperty {
      private final float minFloatValue;
      private final float maxFloatValue;

      public WithState(float value, float minFloatValue, float maxFloatValue) {
         super(value);
         this.minFloatValue = minFloatValue;
         this.maxFloatValue = maxFloatValue;
      }

      public float getMinFloatValue() {
         return this.minFloatValue;
      }

      public float getMaxFloatValue() {
         return this.maxFloatValue;
      }
   }
}
