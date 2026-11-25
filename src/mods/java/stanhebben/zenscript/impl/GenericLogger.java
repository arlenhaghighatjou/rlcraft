package stanhebben.zenscript.impl;

import stanhebben.zenscript.IZenLogger;

public class GenericLogger implements IZenLogger {
   public GenericLogger() {
   }

   public void error(String message) {
      System.out.println("[ERROR] " + message);
   }

   public void error(String message, Throwable e) {
      System.out.println("[ERROR] " + message);
      e.printStackTrace();
   }

   public void warning(String message) {
      System.out.println("[WARNING] " + message);
   }

   public void info(String message) {
      System.out.println("[INFO] " + message);
   }
}
