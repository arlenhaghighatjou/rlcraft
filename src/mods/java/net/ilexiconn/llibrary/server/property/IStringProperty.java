package net.ilexiconn.llibrary.server.property;

public interface IStringProperty {
   String getString();

   void setString(String var1);

   boolean isValidString(String var1);

   default boolean trySetString(String value) {
      if (this.isValidString(value)) {
         this.setString(value);
         return true;
      } else {
         return false;
      }
   }

   public static class WithState implements IStringProperty {
      private String value;

      public WithState(String value) {
         this.value = value;
      }

      public String getString() {
         return this.value;
      }

      public void setString(String value) {
         this.value = value;
      }

      public boolean isValidString(String value) {
         return true;
      }
   }
}
