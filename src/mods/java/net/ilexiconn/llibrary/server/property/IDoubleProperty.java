package net.ilexiconn.llibrary.server.property;

public interface IDoubleProperty {
   double getDouble();

   void setDouble(double var1);

   boolean isValidDouble(double var1);

   default boolean trySetDouble(double value) {
      if (this.isValidDouble(value)) {
         this.setDouble(value);
         return true;
      } else {
         return false;
      }
   }

   public static class WithState implements IDoubleProperty {
      private double value;

      public WithState(double value) {
         this.value = value;
      }

      public double getDouble() {
         return this.value;
      }

      public void setDouble(double value) {
         this.value = value;
      }

      public boolean isValidDouble(double value) {
         return true;
      }
   }
}
