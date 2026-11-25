package slimeknights.mantle.configurate.objectmapping;

public class ObjectMappingException extends Exception {
   public ObjectMappingException() {
   }

   public ObjectMappingException(String message) {
      super(message);
   }

   public ObjectMappingException(String message, Throwable cause) {
      super(message, cause);
   }

   public ObjectMappingException(Throwable cause) {
      super(cause);
   }

   protected ObjectMappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
   }
}
