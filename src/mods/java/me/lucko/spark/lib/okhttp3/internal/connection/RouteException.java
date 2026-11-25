package me.lucko.spark.lib.okhttp3.internal.connection;

import java.io.IOException;
import me.lucko.spark.lib.okhttp3.internal.Util;

public final class RouteException extends RuntimeException {
   private IOException firstException;
   private IOException lastException;

   RouteException(IOException cause) {
      super(cause);
      this.firstException = cause;
      this.lastException = cause;
   }

   public IOException getFirstConnectException() {
      return this.firstException;
   }

   public IOException getLastConnectException() {
      return this.lastException;
   }

   void addConnectException(IOException e) {
      Util.addSuppressedIfPossible(this.firstException, e);
      this.lastException = e;
   }
}
