package net.ilexiconn.llibrary.server.property;

public interface IBooleanProperty {
   boolean getBoolean();

   void setBoolean(boolean var1);

   boolean isValidBoolean(boolean var1);

   default boolean trySetBoolean(boolean value) {
      if (this.isValidBoolean(value)) {
         this.setBoolean(value);
         return true;
      } else {
         return false;
      }
   }

   public static class WithState implements IBooleanProperty {
      private boolean value;

      public WithState(boolean value) {
         this.value = value;
      }

      public boolean getBoolean() {
         return this.value;
      }

      public void setBoolean(boolean value) {
         this.value = value;
      }

      public boolean isValidBoolean(boolean value) {
         return true;
      }
   }
}
