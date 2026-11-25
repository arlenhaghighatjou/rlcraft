package io.github.classgraph;

public class ClassGraphException extends IllegalArgumentException {
   static final long serialVersionUID = 1L;

   private ClassGraphException(String message) {
      super(message);
   }

   private ClassGraphException(String message, Throwable cause) {
      super(message, cause);
   }

   public static ClassGraphException newClassGraphException(String message) {
      return new ClassGraphException(message);
   }

   public static ClassGraphException newClassGraphException(String message, Throwable cause) throws ClassGraphException {
      return new ClassGraphException(message, cause);
   }
}
