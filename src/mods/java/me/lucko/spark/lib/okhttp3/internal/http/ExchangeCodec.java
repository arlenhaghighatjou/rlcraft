package me.lucko.spark.lib.okhttp3.internal.http;

import java.io.IOException;
import javax.annotation.Nullable;
import me.lucko.spark.lib.okhttp3.Headers;
import me.lucko.spark.lib.okhttp3.Request;
import me.lucko.spark.lib.okhttp3.Response;
import me.lucko.spark.lib.okhttp3.internal.connection.RealConnection;
import me.lucko.spark.lib.okio.Sink;
import me.lucko.spark.lib.okio.Source;

public interface ExchangeCodec {
   int DISCARD_STREAM_TIMEOUT_MILLIS = 100;

   RealConnection connection();

   Sink createRequestBody(Request var1, long var2) throws IOException;

   void writeRequestHeaders(Request var1) throws IOException;

   void flushRequest() throws IOException;

   void finishRequest() throws IOException;

   @Nullable
   Response.Builder readResponseHeaders(boolean var1) throws IOException;

   long reportedContentLength(Response var1) throws IOException;

   Source openResponseBodySource(Response var1) throws IOException;

   Headers trailers() throws IOException;

   void cancel();
}
