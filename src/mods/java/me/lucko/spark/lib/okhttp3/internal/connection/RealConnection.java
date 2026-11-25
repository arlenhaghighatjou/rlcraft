package me.lucko.spark.lib.okhttp3.internal.connection;

import java.io.IOException;
import java.lang.ref.Reference;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownServiceException;
import java.net.Proxy.Type;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import me.lucko.spark.lib.okhttp3.Address;
import me.lucko.spark.lib.okhttp3.Call;
import me.lucko.spark.lib.okhttp3.CertificatePinner;
import me.lucko.spark.lib.okhttp3.Connection;
import me.lucko.spark.lib.okhttp3.ConnectionSpec;
import me.lucko.spark.lib.okhttp3.EventListener;
import me.lucko.spark.lib.okhttp3.Handshake;
import me.lucko.spark.lib.okhttp3.HttpUrl;
import me.lucko.spark.lib.okhttp3.Interceptor;
import me.lucko.spark.lib.okhttp3.OkHttpClient;
import me.lucko.spark.lib.okhttp3.Protocol;
import me.lucko.spark.lib.okhttp3.Request;
import me.lucko.spark.lib.okhttp3.RequestBody;
import me.lucko.spark.lib.okhttp3.Response;
import me.lucko.spark.lib.okhttp3.Route;
import me.lucko.spark.lib.okhttp3.internal.Internal;
import me.lucko.spark.lib.okhttp3.internal.Util;
import me.lucko.spark.lib.okhttp3.internal.Version;
import me.lucko.spark.lib.okhttp3.internal.http.ExchangeCodec;
import me.lucko.spark.lib.okhttp3.internal.http1.Http1ExchangeCodec;
import me.lucko.spark.lib.okhttp3.internal.http2.ConnectionShutdownException;
import me.lucko.spark.lib.okhttp3.internal.http2.ErrorCode;
import me.lucko.spark.lib.okhttp3.internal.http2.Http2Connection;
import me.lucko.spark.lib.okhttp3.internal.http2.Http2ExchangeCodec;
import me.lucko.spark.lib.okhttp3.internal.http2.Http2Stream;
import me.lucko.spark.lib.okhttp3.internal.http2.StreamResetException;
import me.lucko.spark.lib.okhttp3.internal.platform.Platform;
import me.lucko.spark.lib.okhttp3.internal.tls.OkHostnameVerifier;
import me.lucko.spark.lib.okhttp3.internal.ws.RealWebSocket;
import me.lucko.spark.lib.okio.BufferedSink;
import me.lucko.spark.lib.okio.BufferedSource;
import me.lucko.spark.lib.okio.Okio;

public final class RealConnection extends Http2Connection.Listener implements Connection {
   private static final String NPE_THROW_WITH_NULL = "throw with null exception";
   private static final int MAX_TUNNEL_ATTEMPTS = 21;
   public final RealConnectionPool connectionPool;
   private final Route route;
   private Socket rawSocket;
   private Socket socket;
   private Handshake handshake;
   private Protocol protocol;
   private Http2Connection http2Connection;
   private BufferedSource source;
   private BufferedSink sink;
   boolean noNewExchanges;
   int routeFailureCount;
   int successCount;
   private int refusedStreamCount;
   private int allocationLimit = 1;
   final List<Reference<Transmitter>> transmitters = new ArrayList();
   long idleAtNanos = Long.MAX_VALUE;

   public RealConnection(RealConnectionPool connectionPool, Route route) {
      this.connectionPool = connectionPool;
      this.route = route;
   }

   public void noNewExchanges() {
      assert !Thread.holdsLock(this.connectionPool);

      synchronized(this.connectionPool) {
         this.noNewExchanges = true;
      }
   }

   static RealConnection testConnection(RealConnectionPool connectionPool, Route route, Socket socket, long idleAtNanos) {
      RealConnection result = new RealConnection(connectionPool, route);
      result.socket = socket;
      result.idleAtNanos = idleAtNanos;
      return result;
   }

   public void connect(int connectTimeout, int readTimeout, int writeTimeout, int pingIntervalMillis, boolean connectionRetryEnabled, Call call, EventListener eventListener) {
      if (this.protocol != null) {
         throw new IllegalStateException("already connected");
      } else {
         RouteException routeException = null;
         List<ConnectionSpec> connectionSpecs = this.route.address().connectionSpecs();
         ConnectionSpecSelector connectionSpecSelector = new ConnectionSpecSelector(connectionSpecs);
         if (this.route.address().sslSocketFactory() == null) {
            if (!connectionSpecs.contains(ConnectionSpec.CLEARTEXT)) {
               throw new RouteException(new UnknownServiceException("CLEARTEXT communication not enabled for client"));
            }

            String host = this.route.address().url().host();
            if (!Platform.get().isCleartextTrafficPermitted(host)) {
               throw new RouteException(new UnknownServiceException("CLEARTEXT communication to " + host + " not permitted by network security policy"));
            }
         } else if (this.route.address().protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE)) {
            throw new RouteException(new UnknownServiceException("H2_PRIOR_KNOWLEDGE cannot be used with HTTPS"));
         }

         while(true) {
            try {
               if (this.route.requiresTunnel()) {
                  this.connectTunnel(connectTimeout, readTimeout, writeTimeout, call, eventListener);
                  if (this.rawSocket == null) {
                     break;
                  }
               } else {
                  this.connectSocket(connectTimeout, readTimeout, call, eventListener);
               }

               this.establishProtocol(connectionSpecSelector, pingIntervalMillis, call, eventListener);
               eventListener.connectEnd(call, this.route.socketAddress(), this.route.proxy(), this.protocol);
               break;
            } catch (IOException var14) {
               Util.closeQuietly(this.socket);
               Util.closeQuietly(this.rawSocket);
               this.socket = null;
               this.rawSocket = null;
               this.source = null;
               this.sink = null;
               this.handshake = null;
               this.protocol = null;
               this.http2Connection = null;
               eventListener.connectFailed(call, this.route.socketAddress(), this.route.proxy(), (Protocol)null, var14);
               if (routeException == null) {
                  routeException = new RouteException(var14);
               } else {
                  routeException.addConnectException(var14);
               }

               if (!connectionRetryEnabled || !connectionSpecSelector.connectionFailed(var14)) {
                  throw routeException;
               }
            }
         }

         if (this.route.requiresTunnel() && this.rawSocket == null) {
            ProtocolException exception = new ProtocolException("Too many tunnel connections attempted: 21");
            throw new RouteException(exception);
         } else {
            if (this.http2Connection != null) {
               synchronized(this.connectionPool) {
                  this.allocationLimit = this.http2Connection.maxConcurrentStreams();
               }
            }

         }
      }
   }

   private void connectTunnel(int connectTimeout, int readTimeout, int writeTimeout, Call call, EventListener eventListener) throws IOException {
      Request tunnelRequest = this.createTunnelRequest();
      HttpUrl url = tunnelRequest.url();

      for(int i = 0; i < 21; ++i) {
         this.connectSocket(connectTimeout, readTimeout, call, eventListener);
         tunnelRequest = this.createTunnel(readTimeout, writeTimeout, tunnelRequest, url);
         if (tunnelRequest == null) {
            break;
         }

         Util.closeQuietly(this.rawSocket);
         this.rawSocket = null;
         this.sink = null;
         this.source = null;
         eventListener.connectEnd(call, this.route.socketAddress(), this.route.proxy(), (Protocol)null);
      }

   }

   private void connectSocket(int connectTimeout, int readTimeout, Call call, EventListener eventListener) throws IOException {
      Proxy proxy = this.route.proxy();
      Address address = this.route.address();
      this.rawSocket = proxy.type() != Type.DIRECT && proxy.type() != Type.HTTP ? new Socket(proxy) : address.socketFactory().createSocket();
      eventListener.connectStart(call, this.route.socketAddress(), proxy);
      this.rawSocket.setSoTimeout(readTimeout);

      try {
         Platform.get().connectSocket(this.rawSocket, this.route.socketAddress(), connectTimeout);
      } catch (ConnectException var9) {
         ConnectException ce = new ConnectException("Failed to connect to " + this.route.socketAddress());
         ce.initCause(var9);
         throw ce;
      }

      try {
         this.source = Okio.buffer(Okio.source(this.rawSocket));
         this.sink = Okio.buffer(Okio.sink(this.rawSocket));
      } catch (NullPointerException var10) {
         if ("throw with null exception".equals(var10.getMessage())) {
            throw new IOException(var10);
         }
      }

   }

   private void establishProtocol(ConnectionSpecSelector connectionSpecSelector, int pingIntervalMillis, Call call, EventListener eventListener) throws IOException {
      if (this.route.address().sslSocketFactory() == null) {
         if (this.route.address().protocols().contains(Protocol.H2_PRIOR_KNOWLEDGE)) {
            this.socket = this.rawSocket;
            this.protocol = Protocol.H2_PRIOR_KNOWLEDGE;
            this.startHttp2(pingIntervalMillis);
         } else {
            this.socket = this.rawSocket;
            this.protocol = Protocol.HTTP_1_1;
         }
      } else {
         eventListener.secureConnectStart(call);
         this.connectTls(connectionSpecSelector);
         eventListener.secureConnectEnd(call, this.handshake);
         if (this.protocol == Protocol.HTTP_2) {
            this.startHttp2(pingIntervalMillis);
         }

      }
   }

   private void startHttp2(int pingIntervalMillis) throws IOException {
      this.socket.setSoTimeout(0);
      this.http2Connection = (new Http2Connection.Builder(true)).socket(this.socket, this.route.address().url().host(), this.source, this.sink).listener(this).pingIntervalMillis(pingIntervalMillis).build();
      this.http2Connection.start();
   }

   private void connectTls(ConnectionSpecSelector connectionSpecSelector) throws IOException {
      Address address = this.route.address();
      SSLSocketFactory sslSocketFactory = address.sslSocketFactory();
      boolean success = false;
      SSLSocket sslSocket = null;

      try {
         sslSocket = (SSLSocket)sslSocketFactory.createSocket(this.rawSocket, address.url().host(), address.url().port(), true);
         ConnectionSpec connectionSpec = connectionSpecSelector.configureSecureSocket(sslSocket);
         if (connectionSpec.supportsTlsExtensions()) {
            Platform.get().configureTlsExtensions(sslSocket, address.url().host(), address.protocols());
         }

         sslSocket.startHandshake();
         SSLSession sslSocketSession = sslSocket.getSession();
         Handshake unverifiedHandshake = Handshake.get(sslSocketSession);
         if (!address.hostnameVerifier().verify(address.url().host(), sslSocketSession)) {
            List<Certificate> peerCertificates = unverifiedHandshake.peerCertificates();
            if (!peerCertificates.isEmpty()) {
               X509Certificate cert = (X509Certificate)peerCertificates.get(0);
               throw new SSLPeerUnverifiedException("Hostname " + address.url().host() + " not verified:\n    certificate: " + CertificatePinner.pin(cert) + "\n    DN: " + cert.getSubjectDN().getName() + "\n    subjectAltNames: " + OkHostnameVerifier.allSubjectAltNames(cert));
            }

            throw new SSLPeerUnverifiedException("Hostname " + address.url().host() + " not verified (no certificates)");
         }

         address.certificatePinner().check(address.url().host(), unverifiedHandshake.peerCertificates());
         String maybeProtocol = connectionSpec.supportsTlsExtensions() ? Platform.get().getSelectedProtocol(sslSocket) : null;
         this.socket = sslSocket;
         this.source = Okio.buffer(Okio.source(this.socket));
         this.sink = Okio.buffer(Okio.sink(this.socket));
         this.handshake = unverifiedHandshake;
         this.protocol = maybeProtocol != null ? Protocol.get(maybeProtocol) : Protocol.HTTP_1_1;
         success = true;
      } catch (AssertionError var14) {
         if (Util.isAndroidGetsocknameError(var14)) {
            throw new IOException(var14);
         }

         throw var14;
      } finally {
         if (sslSocket != null) {
            Platform.get().afterHandshake(sslSocket);
         }

         if (!success) {
            Util.closeQuietly(sslSocket);
         }

      }

   }

   private Request createTunnel(int readTimeout, int writeTimeout, Request tunnelRequest, HttpUrl url) throws IOException {
      String requestLine = "CONNECT " + Util.hostHeader(url, true) + " HTTP/1.1";

      label25:
      while(true) {
         Http1ExchangeCodec tunnelCodec = new Http1ExchangeCodec((OkHttpClient)null, (RealConnection)null, this.source, this.sink);
         this.source.timeout().timeout((long)readTimeout, TimeUnit.MILLISECONDS);
         this.sink.timeout().timeout((long)writeTimeout, TimeUnit.MILLISECONDS);
         tunnelCodec.writeRequest(tunnelRequest.headers(), requestLine);
         tunnelCodec.finishRequest();
         Response response = tunnelCodec.readResponseHeaders(false).request(tunnelRequest).build();
         tunnelCodec.skipConnectBody(response);
         switch(response.code()) {
         case 200:
            if (this.source.getBuffer().exhausted() && this.sink.buffer().exhausted()) {
               return null;
            }

            throw new IOException("TLS tunnel buffered too many bytes!");
         case 407:
            tunnelRequest = this.route.address().proxyAuthenticator().authenticate(this.route, response);
            if (tunnelRequest == null) {
               throw new IOException("Failed to authenticate with proxy");
            }

            if ("close".equalsIgnoreCase(response.header("Connection"))) {
               break label25;
            }
            break;
         default:
            throw new IOException("Unexpected response code for CONNECT: " + response.code());
         }
      }

      return tunnelRequest;
   }

   private Request createTunnelRequest() throws IOException {
      Request proxyConnectRequest = (new Request.Builder()).url(this.route.address().url()).method("CONNECT", (RequestBody)null).header("Host", Util.hostHeader(this.route.address().url(), true)).header("Proxy-Connection", "Keep-Alive").header("User-Agent", Version.userAgent()).build();
      Response fakeAuthChallengeResponse = (new Response.Builder()).request(proxyConnectRequest).protocol(Protocol.HTTP_1_1).code(407).message("Preemptive Authenticate").body(Util.EMPTY_RESPONSE).sentRequestAtMillis(-1L).receivedResponseAtMillis(-1L).header("Proxy-Authenticate", "OkHttp-Preemptive").build();
      Request authenticatedRequest = this.route.address().proxyAuthenticator().authenticate(this.route, fakeAuthChallengeResponse);
      return authenticatedRequest != null ? authenticatedRequest : proxyConnectRequest;
   }

   boolean isEligible(Address address, @Nullable List<Route> routes) {
      if (this.transmitters.size() < this.allocationLimit && !this.noNewExchanges) {
         if (!Internal.instance.equalsNonHost(this.route.address(), address)) {
            return false;
         } else if (address.url().host().equals(this.route().address().url().host())) {
            return true;
         } else if (this.http2Connection == null) {
            return false;
         } else if (routes != null && this.routeMatchesAny(routes)) {
            if (address.hostnameVerifier() != OkHostnameVerifier.INSTANCE) {
               return false;
            } else if (!this.supportsUrl(address.url())) {
               return false;
            } else {
               try {
                  address.certificatePinner().check(address.url().host(), this.handshake().peerCertificates());
                  return true;
               } catch (SSLPeerUnverifiedException var4) {
                  return false;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean routeMatchesAny(List<Route> candidates) {
      int i = 0;

      for(int size = candidates.size(); i < size; ++i) {
         Route candidate = (Route)candidates.get(i);
         if (candidate.proxy().type() == Type.DIRECT && this.route.proxy().type() == Type.DIRECT && this.route.socketAddress().equals(candidate.socketAddress())) {
            return true;
         }
      }

      return false;
   }

   public boolean supportsUrl(HttpUrl url) {
      if (url.port() != this.route.address().url().port()) {
         return false;
      } else if (url.host().equals(this.route.address().url().host())) {
         return true;
      } else {
         return this.handshake != null && OkHostnameVerifier.INSTANCE.verify(url.host(), (X509Certificate)this.handshake.peerCertificates().get(0));
      }
   }

   ExchangeCodec newCodec(OkHttpClient client, Interceptor.Chain chain) throws SocketException {
      if (this.http2Connection != null) {
         return new Http2ExchangeCodec(client, this, chain, this.http2Connection);
      } else {
         this.socket.setSoTimeout(chain.readTimeoutMillis());
         this.source.timeout().timeout((long)chain.readTimeoutMillis(), TimeUnit.MILLISECONDS);
         this.sink.timeout().timeout((long)chain.writeTimeoutMillis(), TimeUnit.MILLISECONDS);
         return new Http1ExchangeCodec(client, this, this.source, this.sink);
      }
   }

   RealWebSocket.Streams newWebSocketStreams(final Exchange exchange) throws SocketException {
      this.socket.setSoTimeout(0);
      this.noNewExchanges();
      return new RealWebSocket.Streams(true, this.source, this.sink) {
         public void close() throws IOException {
            exchange.bodyComplete(-1L, true, true, (IOException)null);
         }
      };
   }

   public Route route() {
      return this.route;
   }

   public void cancel() {
      Util.closeQuietly(this.rawSocket);
   }

   public Socket socket() {
      return this.socket;
   }

   public boolean isHealthy(boolean doExtensiveChecks) {
      if (!this.socket.isClosed() && !this.socket.isInputShutdown() && !this.socket.isOutputShutdown()) {
         if (this.http2Connection != null) {
            return !this.http2Connection.isShutdown();
         } else {
            if (doExtensiveChecks) {
               try {
                  int readTimeout = this.socket.getSoTimeout();

                  boolean var3;
                  try {
                     this.socket.setSoTimeout(1);
                     if (!this.source.exhausted()) {
                        var3 = true;
                        return var3;
                     }

                     var3 = false;
                  } finally {
                     this.socket.setSoTimeout(readTimeout);
                  }

                  return var3;
               } catch (SocketTimeoutException var9) {
                  ;
               } catch (IOException var10) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public void onStream(Http2Stream stream) throws IOException {
      stream.close(ErrorCode.REFUSED_STREAM, (IOException)null);
   }

   public void onSettings(Http2Connection connection) {
      synchronized(this.connectionPool) {
         this.allocationLimit = connection.maxConcurrentStreams();
      }
   }

   public Handshake handshake() {
      return this.handshake;
   }

   public boolean isMultiplexed() {
      return this.http2Connection != null;
   }

   void trackFailure(@Nullable IOException e) {
      assert !Thread.holdsLock(this.connectionPool);

      synchronized(this.connectionPool) {
         if (e instanceof StreamResetException) {
            ErrorCode errorCode = ((StreamResetException)e).errorCode;
            if (errorCode == ErrorCode.REFUSED_STREAM) {
               ++this.refusedStreamCount;
               if (this.refusedStreamCount > 1) {
                  this.noNewExchanges = true;
                  ++this.routeFailureCount;
               }
            } else if (errorCode != ErrorCode.CANCEL) {
               this.noNewExchanges = true;
               ++this.routeFailureCount;
            }
         } else if (!this.isMultiplexed() || e instanceof ConnectionShutdownException) {
            this.noNewExchanges = true;
            if (this.successCount == 0) {
               if (e != null) {
                  this.connectionPool.connectFailed(this.route, e);
               }

               ++this.routeFailureCount;
            }
         }

      }
   }

   public Protocol protocol() {
      return this.protocol;
   }

   public String toString() {
      return "Connection{" + this.route.address().url().host() + ":" + this.route.address().url().port() + ", proxy=" + this.route.proxy() + " hostAddress=" + this.route.socketAddress() + " cipherSuite=" + (this.handshake != null ? this.handshake.cipherSuite() : "none") + " protocol=" + this.protocol + '}';
   }
}
