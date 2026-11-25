package stanhebben.zenscript.impl;

import java.io.PrintStream;
import stanhebben.zenscript.IZenErrorLogger;
import stanhebben.zenscript.util.ZenPosition;

public class GenericErrorLogger implements IZenErrorLogger {
   private final PrintStream output;

   public GenericErrorLogger(PrintStream output) {
      this.output = output;
   }

   public void error(ZenPosition position, String message) {
      if (position == null) {
         this.output.println("System> " + message);
      } else {
         this.output.println(position + "> " + message);
      }

   }

   public void warning(ZenPosition position, String message) {
      if (position == null) {
         this.output.println("system> " + message);
      } else {
         this.output.println(position + "> " + message);
      }

   }

   public void info(ZenPosition position, String message) {
      if (position == null) {
         this.output.println("system> " + message);
      } else {
         this.output.println(position + "> " + message);
      }

   }

   public void error(String message) {
      this.error((ZenPosition)null, message);
   }

   public void error(String message, Throwable e) {
      this.error((ZenPosition)null, message);
      e.printStackTrace(this.output);
   }

   public void warning(String message) {
      this.warning((ZenPosition)null, message);
   }

   public void info(String message) {
      this.info((ZenPosition)null, message);
   }
}
