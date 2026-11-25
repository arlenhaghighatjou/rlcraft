package net.ilexiconn.llibrary.server.property;

public interface IIntProperty {
   int getInt();

   void setInt(int var1);

   boolean isValidInt(int var1);

   default boolean trySetInt(int value) {
      if (this.isValidInt(value)) {
         this.setInt(value);
         return true;
      } else {
         return false;
      }
   }

   public static class WithState implements IIntProperty {
      private int value;

      public WithState(int value) {
         this.value = value;
      }

      public int getInt() {
         return this.value;
      }

      public void setInt(int value) {
         this.value = value;
      }

      public boolean isValidInt(int value) {
         return true;
      }
   }
}
