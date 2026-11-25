package com.charles445.rltweaker.util;

public class CriticalException extends RuntimeException {
   public CriticalException(Throwable t) {
      super(t);
   }

   public CriticalException(String string) {
      super(string);
   }
}
