package me.lucko.spark.lib.okhttp3;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import me.lucko.spark.lib.okhttp3.internal.NamedRunnable;
import me.lucko.spark.lib.okhttp3.internal.Util;
import me.lucko.spark.lib.okhttp3.internal.cache.CacheInterceptor;
import me.lucko.spark.lib.okhttp3.internal.connection.ConnectInterceptor;
import me.lucko.spark.lib.okhttp3.internal.connection.Exchange;
import me.lucko.spark.lib.okhttp3.internal.connection.Transmitter;
import me.lucko.spark.lib.okhttp3.internal.http.BridgeInterceptor;
import me.lucko.spark.lib.okhttp3.internal.http.CallServerInterceptor;
import me.lucko.spark.lib.okhttp3.internal.http.RealInterceptorChain;
import me.lucko.spark.lib.okhttp3.internal.http.RetryAndFollowUpInterceptor;
import me.lucko.spark.lib.okhttp3.internal.platform.Platform;
import me.lucko.spark.lib.okio.Timeout;

final class RealCall implements Call {
   final OkHttpClient client;
   private Transmitter transmitter;
   final Request originalRequest;
   final boolean forWebSocket;
   private boolean executed;

   private RealCall(OkHttpClient client, Request originalRequest, boolean forWebSocket) {
      this.client = client;
      this.originalRequest = originalRequest;
      this.forWebSocket = forWebSocket;
   }

   static RealCall newRealCall(OkHttpClient client, Request originalRequest, boolean forWebSocket) {
      RealCall call = new RealCall(client, originalRequest, forWebSocket);
      call.transmitter = new Transmitter(client, call);
      return call;
   }

   public Request request() {
      return this.originalRequest;
   }

   public Response execute() throws IOException {
      synchronized(this) {
         if (this.executed) {
            throw new IllegalStateException("Already Executed");
         }

         this.executed = true;
      }

      this.transmitter.timeoutEnter();
      this.transmitter.callStart();

      Response var1;
      try {
         this.client.dispatcher().executed(this);
         var1 = this.getResponseWithInterceptorChain();
      } finally {
         this.client.dispatcher().finished(this);
      }

      return var1;
   }

   public void enqueue(Callback responseCallback) {
      synchronized(this) {
         if (this.executed) {
            throw new IllegalStateException("Already Executed");
         }

         this.executed = true;
      }

      this.transmitter.callStart();
      this.client.dispatcher().enqueue(new RealCall.AsyncCall(responseCallback));
   }

   public void cancel() {
      this.transmitter.cancel();
   }

   public Timeout timeout() {
      return this.transmitter.timeout();
   }

   public synchronized boolean isExecuted() {
      return this.executed;
   }

   public boolean isCanceled() {
      return this.transmitter.isCanceled();
   }

   public RealCall clone() {
      return newRealCall(this.client, this.originalRequest, this.forWebSocket);
   }

   String toLoggableString() {
      return (this.isCanceled() ? "canceled " : "") + (this.forWebSocket ? "web socket" : "call") + " to " + this.redactedUrl();
   }

   String redactedUrl() {
      return this.originalRequest.url().redact();
   }

   Response getResponseWithInterceptorChain() throws IOException {
      List<Interceptor> interceptors = new ArrayList();
      interceptors.addAll(this.client.interceptors());
      interceptors.add(new RetryAndFollowUpInterceptor(this.client));
      interceptors.add(new BridgeInterceptor(this.client.cookieJar()));
      interceptors.add(new CacheInterceptor(this.client.internalCache()));
      interceptors.add(new ConnectInterceptor(this.client));
      if (!this.forWebSocket) {
         interceptors.addAll(this.client.networkInterceptors());
      }

      interceptors.add(new CallServerInterceptor(this.forWebSocket));
      Interceptor.Chain chain = new RealInterceptorChain(interceptors, this.transmitter, (Exchange)null, 0, this.originalRequest, this, this.client.connectTimeoutMillis(), this.client.readTimeoutMillis(), this.client.writeTimeoutMillis());
      boolean calledNoMoreExchanges = false;

      Response var5;
      try {
         Response response = chain.proceed(this.originalRequest);
         if (this.transmitter.isCanceled()) {
            Util.closeQuietly(response);
            throw new IOException("Canceled");
         }

         var5 = response;
      } catch (IOException var9) {
         calledNoMoreExchanges = true;
         throw this.transmitter.noMoreExchanges(var9);
      } finally {
         if (!calledNoMoreExchanges) {
            this.transmitter.noMoreExchanges((IOException)null);
         }

      }

      return var5;
   }

   final class AsyncCall extends NamedRunnable {
      private final Callback responseCallback;
      private volatile AtomicInteger callsPerHost = new AtomicInteger(0);

      AsyncCall(Callback responseCallback) {
         super("OkHttp %s", RealCall.this.redactedUrl());
         this.responseCallback = responseCallback;
      }

      AtomicInteger callsPerHost() {
         return this.callsPerHost;
      }

      void reuseCallsPerHostFrom(RealCall.AsyncCall other) {
         this.callsPerHost = other.callsPerHost;
      }

      String host() {
         return RealCall.this.originalRequest.url().host();
      }

      Request request() {
         return RealCall.this.originalRequest;
      }

      RealCall get() {
         return RealCall.this;
      }

      void executeOn(ExecutorService executorService) {
         assert !Thread.holdsLock(RealCall.this.client.dispatcher());

         boolean success = false;

         try {
            executorService.execute(this);
            success = true;
         } catch (RejectedExecutionException var8) {
            InterruptedIOException ioException = new InterruptedIOException("executor rejected");
            ioException.initCause(var8);
            RealCall.this.transmitter.noMoreExchanges(ioException);
            this.responseCallback.onFailure(RealCall.this, ioException);
         } finally {
            if (!success) {
               RealCall.this.client.dispatcher().finished(this);
            }

         }

      }

      protected void execute() {
         boolean signalledCallback = false;
         RealCall.this.transmitter.timeoutEnter();

         try {
            Response response = RealCall.this.getResponseWithInterceptorChain();
            signalledCallback = true;
            this.responseCallback.onResponse(RealCall.this, response);
         } catch (IOException var6) {
            if (signalledCallback) {
               Platform.get().log(4, "Callback failure for " + RealCall.this.toLoggableString(), var6);
            } else {
               this.responseCallback.onFailure(RealCall.this, var6);
            }
         } finally {
            RealCall.this.client.dispatcher().finished(this);
         }

      }
   }
}
