package me.lucko.spark.lib.okhttp3.internal.http1;

import java.io.EOFException;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.concurrent.TimeUnit;
import me.lucko.spark.lib.okhttp3.Headers;
import me.lucko.spark.lib.okhttp3.HttpUrl;
import me.lucko.spark.lib.okhttp3.OkHttpClient;
import me.lucko.spark.lib.okhttp3.Request;
import me.lucko.spark.lib.okhttp3.Response;
import me.lucko.spark.lib.okhttp3.internal.Internal;
import me.lucko.spark.lib.okhttp3.internal.Util;
import me.lucko.spark.lib.okhttp3.internal.connection.RealConnection;
import me.lucko.spark.lib.okhttp3.internal.http.ExchangeCodec;
import me.lucko.spark.lib.okhttp3.internal.http.HttpHeaders;
import me.lucko.spark.lib.okhttp3.internal.http.RequestLine;
import me.lucko.spark.lib.okhttp3.internal.http.StatusLine;
import me.lucko.spark.lib.okio.Buffer;
import me.lucko.spark.lib.okio.BufferedSink;
import me.lucko.spark.lib.okio.BufferedSource;
import me.lucko.spark.lib.okio.ForwardingTimeout;
import me.lucko.spark.lib.okio.Sink;
import me.lucko.spark.lib.okio.Source;
import me.lucko.spark.lib.okio.Timeout;

public final class Http1ExchangeCodec implements ExchangeCodec {
   private static final int STATE_IDLE = 0;
   private static final int STATE_OPEN_REQUEST_BODY = 1;
   private static final int STATE_WRITING_REQUEST_BODY = 2;
   private static final int STATE_READ_RESPONSE_HEADERS = 3;
   private static final int STATE_OPEN_RESPONSE_BODY = 4;
   private static final int STATE_READING_RESPONSE_BODY = 5;
   private static final int STATE_CLOSED = 6;
   private static final int HEADER_LIMIT = 262144;
   private final OkHttpClient client;
   private final RealConnection realConnection;
   private final BufferedSource source;
   private final BufferedSink sink;
   private int state = 0;
   private long headerLimit = 262144L;
   private Headers trailers;

   public Http1ExchangeCodec(OkHttpClient client, RealConnection realConnection, BufferedSource source, BufferedSink sink) {
      this.client = client;
      this.realConnection = realConnection;
      this.source = source;
      this.sink = sink;
   }

   public RealConnection connection() {
      return this.realConnection;
   }

   public Sink createRequestBody(Request request, long contentLength) throws IOException {
      if (request.body() != null && request.body().isDuplex()) {
         throw new ProtocolException("Duplex connections are not supported for HTTP/1");
      } else if ("chunked".equalsIgnoreCase(request.header("Transfer-Encoding"))) {
         return this.newChunkedSink();
      } else if (contentLength != -1L) {
         return this.newKnownLengthSink();
      } else {
         throw new IllegalStateException("Cannot stream a request body without chunked encoding or a known content length!");
      }
   }

   public void cancel() {
      if (this.realConnection != null) {
         this.realConnection.cancel();
      }

   }

   public void writeRequestHeaders(Request request) throws IOException {
      String requestLine = RequestLine.get(request, this.realConnection.route().proxy().type());
      this.writeRequest(request.headers(), requestLine);
   }

   public long reportedContentLength(Response response) {
      if (!HttpHeaders.hasBody(response)) {
         return 0L;
      } else {
         return "chunked".equalsIgnoreCase(response.header("Transfer-Encoding")) ? -1L : HttpHeaders.contentLength(response);
      }
   }

   public Source openResponseBodySource(Response response) {
      if (!HttpHeaders.hasBody(response)) {
         return this.newFixedLengthSource(0L);
      } else if ("chunked".equalsIgnoreCase(response.header("Transfer-Encoding"))) {
         return this.newChunkedSource(response.request().url());
      } else {
         long contentLength = HttpHeaders.contentLength(response);
         return contentLength != -1L ? this.newFixedLengthSource(contentLength) : this.newUnknownLengthSource();
      }
   }

   public Headers trailers() {
      if (this.state != 6) {
         throw new IllegalStateException("too early; can't read the trailers yet");
      } else {
         return this.trailers != null ? this.trailers : Util.EMPTY_HEADERS;
      }
   }

   public boolean isClosed() {
      return this.state == 6;
   }

   public void flushRequest() throws IOException {
      this.sink.flush();
   }

   public void finishRequest() throws IOException {
      this.sink.flush();
   }

   public void writeRequest(Headers headers, String requestLine) throws IOException {
      if (this.state != 0) {
         throw new IllegalStateException("state: " + this.state);
      } else {
         this.sink.writeUtf8(requestLine).writeUtf8("\r\n");
         int i = 0;

         for(int size = headers.size(); i < size; ++i) {
            this.sink.writeUtf8(headers.name(i)).writeUtf8(": ").writeUtf8(headers.value(i)).writeUtf8("\r\n");
         }

         this.sink.writeUtf8("\r\n");
         this.state = 1;
      }
   }

   public Response.Builder readResponseHeaders(boolean expectContinue) throws IOException {
      if (this.state != 1 && this.state != 3) {
         throw new IllegalStateException("state: " + this.state);
      } else {
         try {
            StatusLine statusLine = StatusLine.parse(this.readHeaderLine());
            Response.Builder responseBuilder = (new Response.Builder()).protocol(statusLine.protocol).code(statusLine.code).message(statusLine.message).headers(this.readHeaders());
            if (expectContinue && statusLine.code == 100) {
               return null;
            } else if (statusLine.code == 100) {
               this.state = 3;
               return responseBuilder;
            } else {
               this.state = 4;
               return responseBuilder;
            }
         } catch (EOFException var4) {
            throw new IOException("unexpected end of stream on " + this.realConnection.route().address().url().redact(), var4);
         }
      }
   }

   private String readHeaderLine() throws IOException {
      String line = this.source.readUtf8LineStrict(this.headerLimit);
      this.headerLimit -= (long)line.length();
      return line;
   }

   private Headers readHeaders() throws IOException {
      Headers.Builder headers = new Headers.Builder();

      String line;
      while((line = this.readHeaderLine()).length() != 0) {
         Internal.instance.addLenient(headers, line);
      }

      return headers.build();
   }

   private Sink newChunkedSink() {
      if (this.state != 1) {
         throw new IllegalStateException("state: " + this.state);
      } else {
         this.state = 2;
         return new Http1ExchangeCodec.ChunkedSink();
      }
   }

   private Sink newKnownLengthSink() {
      if (this.state != 1) {
         throw new IllegalStateException("state: " + this.state);
      } else {
         this.state = 2;
         return new Http1ExchangeCodec.KnownLengthSink();
      }
   }

   private Source newFixedLengthSource(long length) {
      if (this.state != 4) {
         throw new IllegalStateException("state: " + this.state);
      } else {
         this.state = 5;
         return new Http1ExchangeCodec.FixedLengthSource(length);
      }
   }

   private Source newChunkedSource(HttpUrl url) {
      if (this.state != 4) {
         throw new IllegalStateException("state: " + this.state);
      } else {
         this.state = 5;
         return new Http1ExchangeCodec.ChunkedSource(url);
      }
   }

   private Source newUnknownLengthSource() {
      if (this.state != 4) {
         throw new IllegalStateException("state: " + this.state);
      } else {
         this.state = 5;
         this.realConnection.noNewExchanges();
         return new Http1ExchangeCodec.UnknownLengthSource();
      }
   }

   private void detachTimeout(ForwardingTimeout timeout) {
      Timeout oldDelegate = timeout.delegate();
      timeout.setDelegate(Timeout.NONE);
      oldDelegate.clearDeadline();
      oldDelegate.clearTimeout();
   }

   public void skipConnectBody(Response response) throws IOException {
      long contentLength = HttpHeaders.contentLength(response);
      if (contentLength != -1L) {
         Source body = this.newFixedLengthSource(contentLength);
         Util.skipAll(body, Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
         body.close();
      }
   }

   private abstract class AbstractSource implements Source {
      protected final ForwardingTimeout timeout;
      protected boolean closed;

      private AbstractSource() {
         this.timeout = new ForwardingTimeout(Http1ExchangeCodec.this.source.timeout());
      }

      public Timeout timeout() {
         return this.timeout;
      }

      public long read(Buffer sink, long byteCount) throws IOException {
         try {
            return Http1ExchangeCodec.this.source.read(sink, byteCount);
         } catch (IOException var5) {
            Http1ExchangeCodec.this.realConnection.noNewExchanges();
            this.responseBodyComplete();
            throw var5;
         }
      }

      final void responseBodyComplete() {
         if (Http1ExchangeCodec.this.state != 6) {
            if (Http1ExchangeCodec.this.state != 5) {
               throw new IllegalStateException("state: " + Http1ExchangeCodec.this.state);
            } else {
               Http1ExchangeCodec.this.detachTimeout(this.timeout);
               Http1ExchangeCodec.this.state = 6;
            }
         }
      }
   }

   private final class ChunkedSink implements Sink {
      private final ForwardingTimeout timeout;
      private boolean closed;

      ChunkedSink() {
         this.timeout = new ForwardingTimeout(Http1ExchangeCodec.this.sink.timeout());
      }

      public Timeout timeout() {
         return this.timeout;
      }

      public void write(Buffer source, long byteCount) throws IOException {
         if (this.closed) {
            throw new IllegalStateException("closed");
         } else if (byteCount != 0L) {
            Http1ExchangeCodec.this.sink.writeHexadecimalUnsignedLong(byteCount);
            Http1ExchangeCodec.this.sink.writeUtf8("\r\n");
            Http1ExchangeCodec.this.sink.write(source, byteCount);
            Http1ExchangeCodec.this.sink.writeUtf8("\r\n");
         }
      }

      public synchronized void flush() throws IOException {
         if (!this.closed) {
            Http1ExchangeCodec.this.sink.flush();
         }
      }

      public synchronized void close() throws IOException {
         if (!this.closed) {
            this.closed = true;
            Http1ExchangeCodec.this.sink.writeUtf8("0\r\n\r\n");
            Http1ExchangeCodec.this.detachTimeout(this.timeout);
            Http1ExchangeCodec.this.state = 3;
         }
      }
   }

   private class ChunkedSource extends Http1ExchangeCodec.AbstractSource {
      private static final long NO_CHUNK_YET = -1L;
      private final HttpUrl url;
      private long bytesRemainingInChunk = -1L;
      private boolean hasMoreChunks = true;

      ChunkedSource(HttpUrl url) {
         this.url = url;
      }

      public long read(Buffer sink, long byteCount) throws IOException {
         if (byteCount < 0L) {
            throw new IllegalArgumentException("byteCount < 0: " + byteCount);
         } else if (this.closed) {
            throw new IllegalStateException("closed");
         } else if (!this.hasMoreChunks) {
            return -1L;
         } else {
            if (this.bytesRemainingInChunk == 0L || this.bytesRemainingInChunk == -1L) {
               this.readChunkSize();
               if (!this.hasMoreChunks) {
                  return -1L;
               }
            }

            long read = super.read(sink, Math.min(byteCount, this.bytesRemainingInChunk));
            if (read == -1L) {
               Http1ExchangeCodec.this.realConnection.noNewExchanges();
               ProtocolException e = new ProtocolException("unexpected end of stream");
               this.responseBodyComplete();
               throw e;
            } else {
               this.bytesRemainingInChunk -= read;
               return read;
            }
         }
      }

      private void readChunkSize() throws IOException {
         if (this.bytesRemainingInChunk != -1L) {
            Http1ExchangeCodec.this.source.readUtf8LineStrict();
         }

         try {
            this.bytesRemainingInChunk = Http1ExchangeCodec.this.source.readHexadecimalUnsignedLong();
            String extensions = Http1ExchangeCodec.this.source.readUtf8LineStrict().trim();
            if (this.bytesRemainingInChunk < 0L || !extensions.isEmpty() && !extensions.startsWith(";")) {
               throw new ProtocolException("expected chunk size and optional extensions but was \"" + this.bytesRemainingInChunk + extensions + "\"");
            }
         } catch (NumberFormatException var2) {
            throw new ProtocolException(var2.getMessage());
         }

         if (this.bytesRemainingInChunk == 0L) {
            this.hasMoreChunks = false;
            Http1ExchangeCodec.this.trailers = Http1ExchangeCodec.this.readHeaders();
            HttpHeaders.receiveHeaders(Http1ExchangeCodec.this.client.cookieJar(), this.url, Http1ExchangeCodec.this.trailers);
            this.responseBodyComplete();
         }

      }

      public void close() throws IOException {
         if (!this.closed) {
            if (this.hasMoreChunks && !Util.discard(this, 100, TimeUnit.MILLISECONDS)) {
               Http1ExchangeCodec.this.realConnection.noNewExchanges();
               this.responseBodyComplete();
            }

            this.closed = true;
         }
      }
   }

   private class FixedLengthSource extends Http1ExchangeCodec.AbstractSource {
      private long bytesRemaining;

      FixedLengthSource(long length) {
         this.bytesRemaining = length;
         if (this.bytesRemaining == 0L) {
            this.responseBodyComplete();
         }

      }

      public long read(Buffer sink, long byteCount) throws IOException {
         if (byteCount < 0L) {
            throw new IllegalArgumentException("byteCount < 0: " + byteCount);
         } else if (this.closed) {
            throw new IllegalStateException("closed");
         } else if (this.bytesRemaining == 0L) {
            return -1L;
         } else {
            long read = super.read(sink, Math.min(this.bytesRemaining, byteCount));
            if (read == -1L) {
               Http1ExchangeCodec.this.realConnection.noNewExchanges();
               ProtocolException e = new ProtocolException("unexpected end of stream");
               this.responseBodyComplete();
               throw e;
            } else {
               this.bytesRemaining -= read;
               if (this.bytesRemaining == 0L) {
                  this.responseBodyComplete();
               }

               return read;
            }
         }
      }

      public void close() throws IOException {
         if (!this.closed) {
            if (this.bytesRemaining != 0L && !Util.discard(this, 100, TimeUnit.MILLISECONDS)) {
               Http1ExchangeCodec.this.realConnection.noNewExchanges();
               this.responseBodyComplete();
            }

            this.closed = true;
         }
      }
   }

   private final class KnownLengthSink implements Sink {
      private final ForwardingTimeout timeout;
      private boolean closed;

      private KnownLengthSink() {
         this.timeout = new ForwardingTimeout(Http1ExchangeCodec.this.sink.timeout());
      }

      public Timeout timeout() {
         return this.timeout;
      }

      public void write(Buffer source, long byteCount) throws IOException {
         if (this.closed) {
            throw new IllegalStateException("closed");
         } else {
            Util.checkOffsetAndCount(source.size(), 0L, byteCount);
            Http1ExchangeCodec.this.sink.write(source, byteCount);
         }
      }

      public void flush() throws IOException {
         if (!this.closed) {
            Http1ExchangeCodec.this.sink.flush();
         }
      }

      public void close() throws IOException {
         if (!this.closed) {
            this.closed = true;
            Http1ExchangeCodec.this.detachTimeout(this.timeout);
            Http1ExchangeCodec.this.state = 3;
         }
      }
   }

   private class UnknownLengthSource extends Http1ExchangeCodec.AbstractSource {
      private boolean inputExhausted;

      private UnknownLengthSource() {
      }

      public long read(Buffer sink, long byteCount) throws IOException {
         if (byteCount < 0L) {
            throw new IllegalArgumentException("byteCount < 0: " + byteCount);
         } else if (this.closed) {
            throw new IllegalStateException("closed");
         } else if (this.inputExhausted) {
            return -1L;
         } else {
            long read = super.read(sink, byteCount);
            if (read == -1L) {
               this.inputExhausted = true;
               this.responseBodyComplete();
               return -1L;
            } else {
               return read;
            }
         }
      }

      public void close() throws IOException {
         if (!this.closed) {
            if (!this.inputExhausted) {
               this.responseBodyComplete();
            }

            this.closed = true;
         }
      }
   }
}
