package me.lucko.spark.lib.okhttp3.internal.http;

import java.io.IOException;
import java.util.List;
import me.lucko.spark.lib.okhttp3.Cookie;
import me.lucko.spark.lib.okhttp3.CookieJar;
import me.lucko.spark.lib.okhttp3.Headers;
import me.lucko.spark.lib.okhttp3.Interceptor;
import me.lucko.spark.lib.okhttp3.MediaType;
import me.lucko.spark.lib.okhttp3.Request;
import me.lucko.spark.lib.okhttp3.RequestBody;
import me.lucko.spark.lib.okhttp3.Response;
import me.lucko.spark.lib.okhttp3.internal.Util;
import me.lucko.spark.lib.okhttp3.internal.Version;
import me.lucko.spark.lib.okio.GzipSource;
import me.lucko.spark.lib.okio.Okio;

public final class BridgeInterceptor implements Interceptor {
   private final CookieJar cookieJar;

   public BridgeInterceptor(CookieJar cookieJar) {
      this.cookieJar = cookieJar;
   }

   public Response intercept(Interceptor.Chain chain) throws IOException {
      Request userRequest = chain.request();
      Request.Builder requestBuilder = userRequest.newBuilder();
      RequestBody body = userRequest.body();
      if (body != null) {
         MediaType contentType = body.contentType();
         if (contentType != null) {
            requestBuilder.header("Content-Type", contentType.toString());
         }

         long contentLength = body.contentLength();
         if (contentLength != -1L) {
            requestBuilder.header("Content-Length", Long.toString(contentLength));
            requestBuilder.removeHeader("Transfer-Encoding");
         } else {
            requestBuilder.header("Transfer-Encoding", "chunked");
            requestBuilder.removeHeader("Content-Length");
         }
      }

      if (userRequest.header("Host") == null) {
         requestBuilder.header("Host", Util.hostHeader(userRequest.url(), false));
      }

      if (userRequest.header("Connection") == null) {
         requestBuilder.header("Connection", "Keep-Alive");
      }

      boolean transparentGzip = false;
      if (userRequest.header("Accept-Encoding") == null && userRequest.header("Range") == null) {
         transparentGzip = true;
         requestBuilder.header("Accept-Encoding", "gzip");
      }

      List<Cookie> cookies = this.cookieJar.loadForRequest(userRequest.url());
      if (!cookies.isEmpty()) {
         requestBuilder.header("Cookie", this.cookieHeader(cookies));
      }

      if (userRequest.header("User-Agent") == null) {
         requestBuilder.header("User-Agent", Version.userAgent());
      }

      Response networkResponse = chain.proceed(requestBuilder.build());
      HttpHeaders.receiveHeaders(this.cookieJar, userRequest.url(), networkResponse.headers());
      Response.Builder responseBuilder = networkResponse.newBuilder().request(userRequest);
      if (transparentGzip && "gzip".equalsIgnoreCase(networkResponse.header("Content-Encoding")) && HttpHeaders.hasBody(networkResponse)) {
         GzipSource responseBody = new GzipSource(networkResponse.body().source());
         Headers strippedHeaders = networkResponse.headers().newBuilder().removeAll("Content-Encoding").removeAll("Content-Length").build();
         responseBuilder.headers(strippedHeaders);
         String contentType = networkResponse.header("Content-Type");
         responseBuilder.body(new RealResponseBody(contentType, -1L, Okio.buffer(responseBody)));
      }

      return responseBuilder.build();
   }

   private String cookieHeader(List<Cookie> cookies) {
      StringBuilder cookieHeader = new StringBuilder();
      int i = 0;

      for(int size = cookies.size(); i < size; ++i) {
         if (i > 0) {
            cookieHeader.append("; ");
         }

         Cookie cookie = (Cookie)cookies.get(i);
         cookieHeader.append(cookie.name()).append('=').append(cookie.value());
      }

      return cookieHeader.toString();
   }
}
