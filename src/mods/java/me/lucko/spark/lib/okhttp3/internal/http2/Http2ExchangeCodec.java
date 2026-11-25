package me.lucko.spark.lib.okhttp3.internal.http2;

import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import me.lucko.spark.lib.okhttp3.Headers;
import me.lucko.spark.lib.okhttp3.Interceptor;
import me.lucko.spark.lib.okhttp3.OkHttpClient;
import me.lucko.spark.lib.okhttp3.Protocol;
import me.lucko.spark.lib.okhttp3.Request;
import me.lucko.spark.lib.okhttp3.Response;
import me.lucko.spark.lib.okhttp3.internal.Internal;
import me.lucko.spark.lib.okhttp3.internal.Util;
import me.lucko.spark.lib.okhttp3.internal.connection.RealConnection;
import me.lucko.spark.lib.okhttp3.internal.http.ExchangeCodec;
import me.lucko.spark.lib.okhttp3.internal.http.HttpHeaders;
import me.lucko.spark.lib.okhttp3.internal.http.RequestLine;
import me.lucko.spark.lib.okhttp3.internal.http.StatusLine;
import me.lucko.spark.lib.okio.Sink;
import me.lucko.spark.lib.okio.Source;

public final class Http2ExchangeCodec implements ExchangeCodec {
   private static final String CONNECTION = "connection";
   private static final String HOST = "host";
   private static final String KEEP_ALIVE = "keep-alive";
   private static final String PROXY_CONNECTION = "proxy-connection";
   private static final String TRANSFER_ENCODING = "transfer-encoding";
   private static final String TE = "te";
   private static final String ENCODING = "encoding";
   private static final String UPGRADE = "upgrade";
   private static final List<String> HTTP_2_SKIPPED_REQUEST_HEADERS = Util.immutableList("connection", "host", "keep-alive", "proxy-connection", "te", "transfer-encoding", "encoding", "upgrade", ":method", ":path", ":scheme", ":authority");
   private static final List<String> HTTP_2_SKIPPED_RESPONSE_HEADERS = Util.immutableList("connection", "host", "keep-alive", "proxy-connection", "te", "transfer-encoding", "encoding", "upgrade");
   private final Interceptor.Chain chain;
   private final RealConnection realConnection;
   private final Http2Connection connection;
   private volatile Http2Stream stream;
   private final Protocol protocol;
   private volatile boolean canceled;

   public Http2ExchangeCodec(OkHttpClient client, RealConnection realConnection, Interceptor.Chain chain, Http2Connection connection) {
      this.realConnection = realConnection;
      this.chain = chain;
      this.connection = connection;
      this.protocol = client.protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE) ? Protocol.H2_PRIOR_KNOWLEDGE : Protocol.HTTP_2;
   }

   public RealConnection connection() {
      return this.realConnection;
   }

   public Sink createRequestBody(Request request, long contentLength) {
      return this.stream.getSink();
   }

   public void writeRequestHeaders(Request request) throws IOException {
      if (this.stream == null) {
         boolean hasRequestBody = request.body() != null;
         List<Header> requestHeaders = http2HeadersList(request);
         this.stream = this.connection.newStream(requestHeaders, hasRequestBody);
         if (this.canceled) {
            this.stream.closeLater(ErrorCode.CANCEL);
            throw new IOException("Canceled");
         } else {
            this.stream.readTimeout().timeout((long)this.chain.readTimeoutMillis(), TimeUnit.MILLISECONDS);
            this.stream.writeTimeout().timeout((long)this.chain.writeTimeoutMillis(), TimeUnit.MILLISECONDS);
         }
      }
   }

   public void flushRequest() throws IOException {
      this.connection.flush();
   }

   public void finishRequest() throws IOException {
      this.stream.getSink().close();
   }

   public Response.Builder readResponseHeaders(boolean expectContinue) throws IOException {
      Headers headers = this.stream.takeHeaders();
      Response.Builder responseBuilder = readHttp2HeadersList(headers, this.protocol);
      return expectContinue && Internal.instance.code(responseBuilder) == 100 ? null : responseBuilder;
   }

   public static List<Header> http2HeadersList(Request request) {
      Headers headers = request.headers();
      List<Header> result = new ArrayList(headers.size() + 4);
      result.add(new Header(Header.TARGET_METHOD, request.method()));
      result.add(new Header(Header.TARGET_PATH, RequestLine.requestPath(request.url())));
      String host = request.header("Host");
      if (host != null) {
         result.add(new Header(Header.TARGET_AUTHORITY, host));
      }

      result.add(new Header(Header.TARGET_SCHEME, request.url().scheme()));
      int i = 0;

      for(int size = headers.size(); i < size; ++i) {
         String name = headers.name(i).toLowerCase(Locale.US);
         if (!HTTP_2_SKIPPED_REQUEST_HEADERS.contains(name) || name.equals("te") && headers.value(i).equals("trailers")) {
            result.add(new Header(name, headers.value(i)));
         }
      }

      return result;
   }

   public static Response.Builder readHttp2HeadersList(Headers headerBlock, Protocol protocol) throws IOException {
      StatusLine statusLine = null;
      Headers.Builder headersBuilder = new Headers.Builder();
      int i = 0;

      for(int size = headerBlock.size(); i < size; ++i) {
         String name = headerBlock.name(i);
         String value = headerBlock.value(i);
         if (name.equals(":status")) {
            statusLine = StatusLine.parse("HTTP/1.1 " + value);
         } else if (!HTTP_2_SKIPPED_RESPONSE_HEADERS.contains(name)) {
            Internal.instance.addLenient(headersBuilder, name, value);
         }
      }

      if (statusLine == null) {
         throw new ProtocolException("Expected ':status' header not present");
      } else {
         return (new Response.Builder()).protocol(protocol).code(statusLine.code).message(statusLine.message).headers(headersBuilder.build());
      }
   }

   public long reportedContentLength(Response response) {
      return HttpHeaders.contentLength(response);
   }

   public Source openResponseBodySource(Response response) {
      return this.stream.getSource();
   }

   public Headers trailers() throws IOException {
      return this.stream.trailers();
   }

   public void cancel() {
      this.canceled = true;
      if (this.stream != null) {
         this.stream.closeLater(ErrorCode.CANCEL);
      }

   }
}
