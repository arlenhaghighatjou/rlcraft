package me.lucko.spark.lib.okhttp3.internal.cache;

import java.io.IOException;
import me.lucko.spark.lib.okio.Sink;

public interface CacheRequest {
   Sink body() throws IOException;

   void abort();
}
