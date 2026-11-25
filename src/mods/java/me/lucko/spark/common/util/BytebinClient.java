package me.lucko.spark.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import me.lucko.spark.lib.okhttp3.MediaType;
import me.lucko.spark.lib.okhttp3.OkHttpClient;
import me.lucko.spark.lib.okhttp3.Request;
import me.lucko.spark.lib.okhttp3.RequestBody;
import me.lucko.spark.lib.okhttp3.Response;
import me.lucko.spark.lib.protobuf.AbstractMessageLite;

public class BytebinClient extends AbstractHttpClient {
   private final String url;
   private final String userAgent;

   public BytebinClient(OkHttpClient okHttpClient, String url, String userAgent) {
      super(okHttpClient);
      this.url = url + (url.endsWith("/") ? "" : "/");
      this.userAgent = userAgent;
   }

   public BytebinClient.Content postContent(byte[] buf, MediaType contentType) throws IOException {
      RequestBody body = RequestBody.create(contentType, buf);
      Request.Builder requestBuilder = (new Request.Builder()).url(this.url + "post").header("User-Agent", this.userAgent).header("Content-Encoding", "gzip");
      Request request = requestBuilder.post(body).build();
      Response response = this.makeHttpRequest(request);

      BytebinClient.Content var8;
      try {
         String key = response.header("Location");
         if (key == null) {
            throw new IllegalStateException("Key not returned");
         }

         var8 = new BytebinClient.Content(key);
      } catch (Throwable var10) {
         if (response != null) {
            try {
               response.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (response != null) {
         response.close();
      }

      return var8;
   }

   public BytebinClient.Content postContent(AbstractMessageLite<?, ?> proto, MediaType contentType) throws IOException {
      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

      try {
         OutputStream out = new GZIPOutputStream(byteOut);

         try {
            proto.writeTo(out);
         } catch (Throwable var8) {
            try {
               out.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         out.close();
      } catch (IOException var9) {
         throw new RuntimeException(var9);
      }

      return this.postContent(byteOut.toByteArray(), contentType);
   }

   public static final class Content {
      private final String key;

      Content(String key) {
         this.key = key;
      }

      public String key() {
         return this.key;
      }
   }
}
