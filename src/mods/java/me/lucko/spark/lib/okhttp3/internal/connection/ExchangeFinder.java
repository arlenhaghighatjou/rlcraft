package me.lucko.spark.lib.okhttp3.internal.connection;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import me.lucko.spark.lib.okhttp3.Address;
import me.lucko.spark.lib.okhttp3.Call;
import me.lucko.spark.lib.okhttp3.EventListener;
import me.lucko.spark.lib.okhttp3.Interceptor;
import me.lucko.spark.lib.okhttp3.OkHttpClient;
import me.lucko.spark.lib.okhttp3.Route;
import me.lucko.spark.lib.okhttp3.internal.Util;
import me.lucko.spark.lib.okhttp3.internal.http.ExchangeCodec;

final class ExchangeFinder {
   private final Transmitter transmitter;
   private final Address address;
   private final RealConnectionPool connectionPool;
   private final Call call;
   private final EventListener eventListener;
   private RouteSelector.Selection routeSelection;
   private final RouteSelector routeSelector;
   private RealConnection connectingConnection;
   private boolean hasStreamFailure;

   ExchangeFinder(Transmitter transmitter, RealConnectionPool connectionPool, Address address, Call call, EventListener eventListener) {
      this.transmitter = transmitter;
      this.connectionPool = connectionPool;
      this.address = address;
      this.call = call;
      this.eventListener = eventListener;
      this.routeSelector = new RouteSelector(address, connectionPool.routeDatabase, call, eventListener);
   }

   public ExchangeCodec find(OkHttpClient client, Interceptor.Chain chain, boolean doExtensiveHealthChecks) {
      int connectTimeout = chain.connectTimeoutMillis();
      int readTimeout = chain.readTimeoutMillis();
      int writeTimeout = chain.writeTimeoutMillis();
      int pingIntervalMillis = client.pingIntervalMillis();
      boolean connectionRetryEnabled = client.retryOnConnectionFailure();

      try {
         RealConnection resultConnection = this.findHealthyConnection(connectTimeout, readTimeout, writeTimeout, pingIntervalMillis, connectionRetryEnabled, doExtensiveHealthChecks);
         return resultConnection.newCodec(client, chain);
      } catch (RouteException var10) {
         this.trackFailure();
         throw var10;
      } catch (IOException var11) {
         this.trackFailure();
         throw new RouteException(var11);
      }
   }

   private RealConnection findHealthyConnection(int connectTimeout, int readTimeout, int writeTimeout, int pingIntervalMillis, boolean connectionRetryEnabled, boolean doExtensiveHealthChecks) throws IOException {
      while(true) {
         RealConnection candidate = this.findConnection(connectTimeout, readTimeout, writeTimeout, pingIntervalMillis, connectionRetryEnabled);
         synchronized(this.connectionPool) {
            if (candidate.successCount == 0) {
               return candidate;
            }
         }

         if (candidate.isHealthy(doExtensiveHealthChecks)) {
            return candidate;
         }

         candidate.noNewExchanges();
      }
   }

   private RealConnection findConnection(int connectTimeout, int readTimeout, int writeTimeout, int pingIntervalMillis, boolean connectionRetryEnabled) throws IOException {
      boolean foundPooledConnection = false;
      RealConnection result = null;
      Route selectedRoute = null;
      RealConnection releasedConnection;
      Socket toClose;
      synchronized(this.connectionPool) {
         if (this.transmitter.isCanceled()) {
            throw new IOException("Canceled");
         }

         this.hasStreamFailure = false;
         Route previousRoute = this.retryCurrentRoute() ? this.transmitter.connection.route() : null;
         releasedConnection = this.transmitter.connection;
         toClose = this.transmitter.connection != null && this.transmitter.connection.noNewExchanges ? this.transmitter.releaseConnectionNoEvents() : null;
         if (this.transmitter.connection != null) {
            result = this.transmitter.connection;
            releasedConnection = null;
         }

         if (result == null) {
            if (this.connectionPool.transmitterAcquirePooledConnection(this.address, this.transmitter, (List)null, false)) {
               foundPooledConnection = true;
               result = this.transmitter.connection;
            } else {
               selectedRoute = previousRoute;
            }
         }
      }

      Util.closeQuietly(toClose);
      if (releasedConnection != null) {
         this.eventListener.connectionReleased(this.call, releasedConnection);
      }

      if (foundPooledConnection) {
         this.eventListener.connectionAcquired(this.call, result);
      }

      if (result != null) {
         return result;
      } else {
         boolean newRouteSelection = false;
         if (selectedRoute == null && (this.routeSelection == null || !this.routeSelection.hasNext())) {
            newRouteSelection = true;
            this.routeSelection = this.routeSelector.next();
         }

         List<Route> routes = null;
         synchronized(this.connectionPool) {
            if (this.transmitter.isCanceled()) {
               throw new IOException("Canceled");
            }

            if (newRouteSelection) {
               routes = this.routeSelection.getAll();
               if (this.connectionPool.transmitterAcquirePooledConnection(this.address, this.transmitter, routes, false)) {
                  foundPooledConnection = true;
                  result = this.transmitter.connection;
               }
            }

            if (!foundPooledConnection) {
               if (selectedRoute == null) {
                  selectedRoute = this.routeSelection.next();
               }

               result = new RealConnection(this.connectionPool, selectedRoute);
               this.connectingConnection = result;
            }
         }

         if (foundPooledConnection) {
            this.eventListener.connectionAcquired(this.call, result);
            return result;
         } else {
            result.connect(connectTimeout, readTimeout, writeTimeout, pingIntervalMillis, connectionRetryEnabled, this.call, this.eventListener);
            this.connectionPool.routeDatabase.connected(result.route());
            Socket socket = null;
            synchronized(this.connectionPool) {
               this.connectingConnection = null;
               if (this.connectionPool.transmitterAcquirePooledConnection(this.address, this.transmitter, routes, true)) {
                  result.noNewExchanges = true;
                  socket = result.socket();
                  result = this.transmitter.connection;
               } else {
                  this.connectionPool.put(result);
                  this.transmitter.acquireConnectionNoEvents(result);
               }
            }

            Util.closeQuietly(socket);
            this.eventListener.connectionAcquired(this.call, result);
            return result;
         }
      }
   }

   RealConnection connectingConnection() {
      assert Thread.holdsLock(this.connectionPool);

      return this.connectingConnection;
   }

   void trackFailure() {
      assert !Thread.holdsLock(this.connectionPool);

      synchronized(this.connectionPool) {
         this.hasStreamFailure = true;
      }
   }

   boolean hasStreamFailure() {
      synchronized(this.connectionPool) {
         return this.hasStreamFailure;
      }
   }

   boolean hasRouteToTry() {
      synchronized(this.connectionPool) {
         return this.retryCurrentRoute() || this.routeSelection != null && this.routeSelection.hasNext() || this.routeSelector.hasNext();
      }
   }

   private boolean retryCurrentRoute() {
      return this.transmitter.connection != null && this.transmitter.connection.routeFailureCount == 0 && Util.sameConnection(this.transmitter.connection.route().address().url(), this.address.url());
   }
}
