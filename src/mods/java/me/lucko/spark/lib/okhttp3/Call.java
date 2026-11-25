package me.lucko.spark.lib.okhttp3;

import java.io.IOException;
import me.lucko.spark.lib.okio.Timeout;

public interface Call extends Cloneable {
   Request request();

   Response execute() throws IOException;

   void enqueue(Callback var1);

   void cancel();

   boolean isExecuted();

   boolean isCanceled();

   Timeout timeout();

   Call clone();

   public interface Factory {
      Call newCall(Request var1);
   }
}
