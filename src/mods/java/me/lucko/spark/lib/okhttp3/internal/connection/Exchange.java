package me.lucko.spark.lib.okhttp3.internal.connection;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.SocketException;
import javax.annotation.Nullable;
import me.lucko.spark.lib.okhttp3.Call;
import me.lucko.spark.lib.okhttp3.EventListener;
import me.lucko.spark.lib.okhttp3.Headers;
import me.lucko.spark.lib.okhttp3.Request;
import me.lucko.spark.lib.okhttp3.Response;
import me.lucko.spark.lib.okhttp3.ResponseBody;
import me.lucko.spark.lib.okhttp3.internal.Internal;
import me.lucko.spark.lib.okhttp3.internal.http.ExchangeCodec;
import me.lucko.spark.lib.okhttp3.internal.http.RealResponseBody;
import me.lucko.spark.lib.okhttp3.internal.ws.RealWebSocket;
import me.lucko.spark.lib.okio.Buffer;
import me.lucko.spark.lib.okio.ForwardingSink;
import me.lucko.spark.lib.okio.ForwardingSource;
import me.lucko.spark.lib.okio.Okio;
import me.lucko.spark.lib.okio.Sink;
import me.lucko.spark.lib.okio.Source;

public final class Exchange {
   final Transmitter transmitter;
   final Call call;
   final EventListener eventListener;
   final ExchangeFinder finder;
   final ExchangeCodec codec;
   private boolean duplex;

   public Exchange(Transmitter transmitter, Call call, EventListener eventListener, ExchangeFinder finder, ExchangeCodec codec) {
      this.transmitter = transmitter;
      this.call = call;
      this.eventListener = eventListener;
      this.finder = finder;
      this.codec = codec;
   }

   public RealConnection connection() {
      return this.codec.connection();
   }

   public boolean isDuplex() {
      return this.duplex;
   }

   public void writeRequestHeaders(Request request) throws IOException {
      try {
         this.eventListener.requestHeadersStart(this.call);
         this.codec.writeRequestHeaders(request);
         this.eventListener.requestHeadersEnd(this.call, request);
      } catch (IOException var3) {
         this.eventListener.requestFailed(this.call, var3);
         this.trackFailure(var3);
         throw var3;
      }
   }

   public Sink createRequestBody(Request request, boolean duplex) throws IOException {
      this.duplex = duplex;
      long contentLength = request.body().contentLength();
      this.eventListener.requestBodyStart(this.call);
      Sink rawRequestBody = this.codec.createRequestBody(request, contentLength);
      return new Exchange.RequestBodySink(rawRequestBody, contentLength);
   }

   public void flushRequest() throws IOException {
      try {
         this.codec.flushRequest();
      } catch (IOException var2) {
         this.eventListener.requestFailed(this.call, var2);
         this.trackFailure(var2);
         throw var2;
      }
   }

   public void finishRequest() throws IOException {
      try {
         this.codec.finishRequest();
      } catch (IOException var2) {
         this.eventListener.requestFailed(this.call, var2);
         this.trackFailure(var2);
         throw var2;
      }
   }

   public void responseHeadersStart() {
      this.eventListener.responseHeadersStart(this.call);
   }

   @Nullable
   public Response.Builder readResponseHeaders(boolean expectContinue) throws IOException {
      try {
         Response.Builder result = this.codec.readResponseHeaders(expectContinue);
         if (result != null) {
            Internal.instance.initExchange(result, this);
         }

         return result;
      } catch (IOException var3) {
         this.eventListener.responseFailed(this.call, var3);
         this.trackFailure(var3);
         throw var3;
      }
   }

   public void responseHeadersEnd(Response response) {
      this.eventListener.responseHeadersEnd(this.call, response);
   }

   public ResponseBody openResponseBody(Response response) throws IOException {
      try {
         this.eventListener.responseBodyStart(this.call);
         String contentType = response.header("Content-Type");
         long contentLength = this.codec.reportedContentLength(response);
         Source rawSource = this.codec.openResponseBodySource(response);
         Exchange.ResponseBodySource source = new Exchange.ResponseBodySource(rawSource, contentLength);
         return new RealResponseBody(contentType, contentLength, Okio.buffer(source));
      } catch (IOException var7) {
         this.eventListener.responseFailed(this.call, var7);
         this.trackFailure(var7);
         throw var7;
      }
   }

   public Headers trailers() throws IOException {
      return this.codec.trailers();
   }

   public void timeoutEarlyExit() {
      this.transmitter.timeoutEarlyExit();
   }

   public RealWebSocket.Streams newWebSocketStreams() throws SocketException {
      this.transmitter.timeoutEarlyExit();
      return this.codec.connection().newWebSocketStreams(this);
   }

   public void webSocketUpgradeFailed() {
      this.bodyComplete(-1L, true, true, (IOException)null);
   }

   public void noNewExchangesOnConnection() {
      this.codec.connection().noNewExchanges();
   }

   public void cancel() {
      this.codec.cancel();
   }

   public void detachWithViolence() {
      this.codec.cancel();
      this.transmitter.exchangeMessageDone(this, true, true, (IOException)null);
   }

   void trackFailure(IOException e) {
      this.finder.trackFailure();
      this.codec.connection().trackFailure(e);
   }

   @Nullable
   IOException bodyComplete(long bytesRead, boolean responseDone, boolean requestDone, @Nullable IOException e) {
      if (e != null) {
         this.trackFailure(e);
      }

      if (requestDone) {
         if (e != null) {
            this.eventListener.requestFailed(this.call, e);
         } else {
            this.eventListener.requestBodyEnd(this.call, bytesRead);
         }
      }

      if (responseDone) {
         if (e != null) {
            this.eventListener.responseFailed(this.call, e);
         } else {
            this.eventListener.responseBodyEnd(this.call, bytesRead);
         }
      }

      return this.transmitter.exchangeMessageDone(this, requestDone, responseDone, e);
   }

   public void noRequestBody() {
      this.transmitter.exchangeMessageDone(this, true, false, (IOException)null);
   }

   private final class RequestBodySink extends ForwardingSink {
      private boolean completed;
      private long contentLength;
      private long bytesReceived;
      private boolean closed;

      RequestBodySink(Sink delegate, long contentLength) {
         super(delegate);
         this.contentLength = contentLength;
      }

      public void write(Buffer source, long byteCount) throws IOException {
         if (this.closed) {
            throw new IllegalStateException("closed");
         } else if (this.contentLength != -1L && this.bytesReceived + byteCount > this.contentLength) {
            throw new ProtocolException("expected " + this.contentLength + " bytes but received " + (this.bytesReceived + byteCount));
         } else {
            try {
               super.write(source, byteCount);
               this.bytesReceived += byteCount;
            } catch (IOException var5) {
               throw this.complete(var5);
            }
         }
      }

      public void flush() throws IOException {
         try {
            super.flush();
         } catch (IOException var2) {
            throw this.complete(var2);
         }
      }

      public void close() throws IOException {
         if (!this.closed) {
            this.closed = true;
            if (this.contentLength != -1L && this.bytesReceived != this.contentLength) {
               throw new ProtocolException("unexpected end of stream");
            } else {
               try {
                  super.close();
                  this.complete((IOException)null);
               } catch (IOException var2) {
                  throw this.complete(var2);
               }
            }
         }
      }

      @Nullable
      private IOException complete(@Nullable IOException e) {
         if (this.completed) {
            return e;
         } else {
            this.completed = true;
            return Exchange.this.bodyComplete(this.bytesReceived, false, true, e);
         }
      }
   }

   final class ResponseBodySource extends ForwardingSource {
      private final long contentLength;
      private long bytesReceived;
      private boolean completed;
      private boolean closed;

      ResponseBodySource(Source delegate, long contentLength) {
         super(delegate);
         this.contentLength = contentLength;
         if (contentLength == 0L) {
            this.complete((IOException)null);
         }

      }

      public long read(Buffer sink, long byteCount) throws IOException {
         if (this.closed) {
            throw new IllegalStateException("closed");
         } else {
            try {
               long read = this.delegate().read(sink, byteCount);
               if (read == -1L) {
                  this.complete((IOException)null);
                  return -1L;
               } else {
                  long newBytesReceived = this.bytesReceived + read;
                  if (this.contentLength != -1L && newBytesReceived > this.contentLength) {
                     throw new ProtocolException("expected " + this.contentLength + " bytes but received " + newBytesReceived);
                  } else {
                     this.bytesReceived = newBytesReceived;
                     if (newBytesReceived == this.contentLength) {
                        this.complete((IOException)null);
                     }

                     return read;
                  }
               }
            } catch (IOException var8) {
               throw this.complete(var8);
            }
         }
      }

      public void close() throws IOException {
         if (!this.closed) {
            this.closed = true;

            try {
               super.close();
               this.complete((IOException)null);
            } catch (IOException var2) {
               throw this.complete(var2);
            }
         }
      }

      @Nullable
      IOException complete(@Nullable IOException e) {
         if (this.completed) {
            return e;
         } else {
            this.completed = true;
            return Exchange.this.bodyComplete(this.bytesReceived, true, false, e);
         }
      }
   }
}
