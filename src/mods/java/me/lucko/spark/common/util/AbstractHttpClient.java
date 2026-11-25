package me.lucko.spark.common.util;

import java.io.IOException;
import me.lucko.spark.lib.okhttp3.OkHttpClient;
import me.lucko.spark.lib.okhttp3.Request;
import me.lucko.spark.lib.okhttp3.Response;

public class AbstractHttpClient {
   protected final OkHttpClient okHttp;

   public AbstractHttpClient(OkHttpClient okHttp) {
      this.okHttp = okHttp;
   }

   protected Response makeHttpRequest(Request request) throws IOException {
      Response response = this.okHttp.newCall(request).execute();
      if (!response.isSuccessful()) {
         response.close();
         throw new RuntimeException("Request was unsuccessful: " + response.code() + " - " + response.message());
      } else {
         return response;
      }
   }
}
