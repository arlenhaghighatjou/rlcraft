package net.ilexiconn.llibrary.server.property;

public interface IFloatProperty {
   float getFloat();

   void setFloat(float var1);

   boolean isValidFloat(float var1);

   default boolean trySetFloat(float value) {
      if (this.isValidFloat(value)) {
         this.setFloat(value);
         return true;
      } else {
         return false;
      }
   }

   public static class WithState implements IFloatProperty {
      private float value;

      public WithState(float value) {
         this.value = value;
      }

      public float getFloat() {
         return this.value;
      }

      public void setFloat(float value) {
         this.value = value;
      }

      public boolean isValidFloat(float value) {
         return true;
      }
   }
}
