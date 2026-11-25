package me.lucko.spark.lib.okhttp3.internal.connection;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import me.lucko.spark.lib.okhttp3.Address;
import me.lucko.spark.lib.okhttp3.Call;
import me.lucko.spark.lib.okhttp3.CertificatePinner;
import me.lucko.spark.lib.okhttp3.Connection;
import me.lucko.spark.lib.okhttp3.EventListener;
import me.lucko.spark.lib.okhttp3.HttpUrl;
import me.lucko.spark.lib.okhttp3.Interceptor;
import me.lucko.spark.lib.okhttp3.OkHttpClient;
import me.lucko.spark.lib.okhttp3.Request;
import me.lucko.spark.lib.okhttp3.internal.Internal;
import me.lucko.spark.lib.okhttp3.internal.Util;
import me.lucko.spark.lib.okhttp3.internal.http.ExchangeCodec;
import me.lucko.spark.lib.okhttp3.internal.platform.Platform;
import me.lucko.spark.lib.okio.AsyncTimeout;
import me.lucko.spark.lib.okio.Timeout;

public final class Transmitter {
   private final OkHttpClient client;
   private final RealConnectionPool connectionPool;
   private final Call call;
   private final EventListener eventListener;
   private final AsyncTimeout timeout = new AsyncTimeout() {
      protected void timedOut() {
         Transmitter.this.cancel();
      }
   };
   @Nullable
   private Object callStackTrace;
   private Request request;
   private ExchangeFinder exchangeFinder;
   public RealConnection connection;
   @Nullable
   private Exchange exchange;
   private boolean exchangeRequestDone;
   private boolean exchangeResponseDone;
   private boolean canceled;
   private boolean timeoutEarlyExit;
   private boolean noMoreExchanges;

   public Transmitter(OkHttpClient client, Call call) {
      this.client = client;
      this.connectionPool = Internal.instance.realConnectionPool(client.connectionPool());
      this.call = call;
      this.eventListener = client.eventListenerFactory().create(call);
      this.timeout.timeout((long)client.callTimeoutMillis(), TimeUnit.MILLISECONDS);
   }

   public Timeout timeout() {
      return this.timeout;
   }

   public void timeoutEnter() {
      this.timeout.enter();
   }

   public void timeoutEarlyExit() {
      if (this.timeoutEarlyExit) {
         throw new IllegalStateException();
      } else {
         this.timeoutEarlyExit = true;
         this.timeout.exit();
      }
   }

   @Nullable
   private IOException timeoutExit(@Nullable IOException cause) {
      if (this.timeoutEarlyExit) {
         return cause;
      } else if (!this.timeout.exit()) {
         return cause;
      } else {
         InterruptedIOException e = new InterruptedIOException("timeout");
         if (cause != null) {
            e.initCause(cause);
         }

         return e;
      }
   }

   public void callStart() {
      this.callStackTrace = Platform.get().getStackTraceForCloseable("response.body().close()");
      this.eventListener.callStart(this.call);
   }

   public void prepareToConnect(Request request) {
      if (this.request != null) {
         if (Util.sameConnection(this.request.url(), request.url()) && this.exchangeFinder.hasRouteToTry()) {
            return;
         }

         if (this.exchange != null) {
            throw new IllegalStateException();
         }

         if (this.exchangeFinder != null) {
            this.maybeReleaseConnection((IOException)null, true);
            this.exchangeFinder = null;
         }
      }

      this.request = request;
      this.exchangeFinder = new ExchangeFinder(this, this.connectionPool, this.createAddress(request.url()), this.call, this.eventListener);
   }

   private Address createAddress(HttpUrl url) {
      SSLSocketFactory sslSocketFactory = null;
      HostnameVerifier hostnameVerifier = null;
      CertificatePinner certificatePinner = null;
      if (url.isHttps()) {
         sslSocketFactory = this.client.sslSocketFactory();
         hostnameVerifier = this.client.hostnameVerifier();
         certificatePinner = this.client.certificatePinner();
      }

      return new Address(url.host(), url.port(), this.client.dns(), this.client.socketFactory(), sslSocketFactory, hostnameVerifier, certificatePinner, this.client.proxyAuthenticator(), this.client.proxy(), this.client.protocols(), this.client.connectionSpecs(), this.client.proxySelector());
   }

   Exchange newExchange(Interceptor.Chain chain, boolean doExtensiveHealthChecks) {
      synchronized(this.connectionPool) {
         if (this.noMoreExchanges) {
            throw new IllegalStateException("released");
         }

         if (this.exchange != null) {
            throw new IllegalStateException("cannot make a new request because the previous response is still open: please call response.close()");
         }
      }

      ExchangeCodec codec = this.exchangeFinder.find(this.client, chain, doExtensiveHealthChecks);
      Exchange result = new Exchange(this, this.call, this.eventListener, this.exchangeFinder, codec);
      synchronized(this.connectionPool) {
         this.exchange = result;
         this.exchangeRequestDone = false;
         this.exchangeResponseDone = false;
         return result;
      }
   }

   void acquireConnectionNoEvents(RealConnection connection) {
      assert Thread.holdsLock(this.connectionPool);

      if (this.connection != null) {
         throw new IllegalStateException();
      } else {
         this.connection = connection;
         connection.transmitters.add(new Transmitter.TransmitterReference(this, this.callStackTrace));
      }
   }

   @Nullable
   Socket releaseConnectionNoEvents() {
      assert Thread.holdsLock(this.connectionPool);

      int index = -1;
      int i = 0;

      for(int size = this.connection.transmitters.size(); i < size; ++i) {
         Reference<Transmitter> reference = (Reference)this.connection.transmitters.get(i);
         if (reference.get() == this) {
            index = i;
            break;
         }
      }

      if (index == -1) {
         throw new IllegalStateException();
      } else {
         RealConnection released = this.connection;
         released.transmitters.remove(index);
         this.connection = null;
         if (released.transmitters.isEmpty()) {
            released.idleAtNanos = System.nanoTime();
            if (this.connectionPool.connectionBecameIdle(released)) {
               return released.socket();
            }
         }

         return null;
      }
   }

   public void exchangeDoneDueToException() {
      synchronized(this.connectionPool) {
         if (this.noMoreExchanges) {
            throw new IllegalStateException();
         } else {
            this.exchange = null;
         }
      }
   }

   @Nullable
   IOException exchangeMessageDone(Exchange exchange, boolean requestDone, boolean responseDone, @Nullable IOException e) {
      boolean exchangeDone = false;
      synchronized(this.connectionPool) {
         if (exchange != this.exchange) {
            return e;
         }

         boolean changed = false;
         if (requestDone) {
            if (!this.exchangeRequestDone) {
               changed = true;
            }

            this.exchangeRequestDone = true;
         }

         if (responseDone) {
            if (!this.exchangeResponseDone) {
               changed = true;
            }

            this.exchangeResponseDone = true;
         }

         if (this.exchangeRequestDone && this.exchangeResponseDone && changed) {
            exchangeDone = true;
            ++this.exchange.connection().successCount;
            this.exchange = null;
         }
      }

      if (exchangeDone) {
         e = this.maybeReleaseConnection(e, false);
      }

      return e;
   }

   @Nullable
   public IOException noMoreExchanges(@Nullable IOException e) {
      synchronized(this.connectionPool) {
         this.noMoreExchanges = true;
      }

      return this.maybeReleaseConnection(e, false);
   }

   @Nullable
   private IOException maybeReleaseConnection(@Nullable IOException e, boolean force) {
      Socket socket;
      Connection releasedConnection;
      boolean callEnd;
      synchronized(this.connectionPool) {
         if (force && this.exchange != null) {
            throw new IllegalStateException("cannot release connection while it is in use");
         }

         releasedConnection = this.connection;
         socket = this.connection == null || this.exchange != null || !force && !this.noMoreExchanges ? null : this.releaseConnectionNoEvents();
         if (this.connection != null) {
            releasedConnection = null;
         }

         callEnd = this.noMoreExchanges && this.exchange == null;
      }

      Util.closeQuietly(socket);
      if (releasedConnection != null) {
         this.eventListener.connectionReleased(this.call, releasedConnection);
      }

      if (callEnd) {
         boolean callFailed = e != null;
         e = this.timeoutExit(e);
         if (callFailed) {
            this.eventListener.callFailed(this.call, e);
         } else {
            this.eventListener.callEnd(this.call);
         }
      }

      return e;
   }

   public boolean canRetry() {
      return this.exchangeFinder.hasStreamFailure() && this.exchangeFinder.hasRouteToTry();
   }

   public boolean hasExchange() {
      synchronized(this.connectionPool) {
         return this.exchange != null;
      }
   }

   public void cancel() {
      Exchange exchangeToCancel;
      RealConnection connectionToCancel;
      synchronized(this.connectionPool) {
         this.canceled = true;
         exchangeToCancel = this.exchange;
         connectionToCancel = this.exchangeFinder != null && this.exchangeFinder.connectingConnection() != null ? this.exchangeFinder.connectingConnection() : this.connection;
      }

      if (exchangeToCancel != null) {
         exchangeToCancel.cancel();
      } else if (connectionToCancel != null) {
         connectionToCancel.cancel();
      }

   }

   public boolean isCanceled() {
      synchronized(this.connectionPool) {
         return this.canceled;
      }
   }

   static final class TransmitterReference extends WeakReference<Transmitter> {
      final Object callStackTrace;

      TransmitterReference(Transmitter referent, Object callStackTrace) {
         super(referent);
         this.callStackTrace = callStackTrace;
      }
   }
}
