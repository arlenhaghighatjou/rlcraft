package me.lucko.spark.lib.okhttp3.internal.http;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.Proxy.Type;
import java.security.cert.CertificateException;
import javax.annotation.Nullable;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import me.lucko.spark.lib.okhttp3.HttpUrl;
import me.lucko.spark.lib.okhttp3.Interceptor;
import me.lucko.spark.lib.okhttp3.OkHttpClient;
import me.lucko.spark.lib.okhttp3.Request;
import me.lucko.spark.lib.okhttp3.RequestBody;
import me.lucko.spark.lib.okhttp3.Response;
import me.lucko.spark.lib.okhttp3.ResponseBody;
import me.lucko.spark.lib.okhttp3.Route;
import me.lucko.spark.lib.okhttp3.internal.Internal;
import me.lucko.spark.lib.okhttp3.internal.Util;
import me.lucko.spark.lib.okhttp3.internal.connection.Exchange;
import me.lucko.spark.lib.okhttp3.internal.connection.RouteException;
import me.lucko.spark.lib.okhttp3.internal.connection.Transmitter;
import me.lucko.spark.lib.okhttp3.internal.http2.ConnectionShutdownException;

public final class RetryAndFollowUpInterceptor implements Interceptor {
   private static final int MAX_FOLLOW_UPS = 20;
   private final OkHttpClient client;

   public RetryAndFollowUpInterceptor(OkHttpClient client) {
      this.client = client;
   }

   public Response intercept(Interceptor.Chain chain) throws IOException {
      Request request = chain.request();
      RealInterceptorChain realChain = (RealInterceptorChain)chain;
      Transmitter transmitter = realChain.transmitter();
      int followUpCount = 0;
      Response priorResponse = null;

      while(true) {
         transmitter.prepareToConnect(request);
         if (transmitter.isCanceled()) {
            throw new IOException("Canceled");
         }

         boolean success = false;

         Response response;
         try {
            response = realChain.proceed(request, transmitter, (Exchange)null);
            success = true;
         } catch (RouteException var16) {
            if (this.recover(var16.getLastConnectException(), transmitter, false, request)) {
               continue;
            }

            throw var16.getFirstConnectException();
         } catch (IOException var17) {
            boolean requestSendStarted = !(var17 instanceof ConnectionShutdownException);
            if (!this.recover(var17, transmitter, requestSendStarted, request)) {
               throw var17;
            }
            continue;
         } finally {
            if (!success) {
               transmitter.exchangeDoneDueToException();
            }

         }

         if (priorResponse != null) {
            response = response.newBuilder().priorResponse(priorResponse.newBuilder().body((ResponseBody)null).build()).build();
         }

         Exchange exchange = Internal.instance.exchange(response);
         Route route = exchange != null ? exchange.connection().route() : null;
         Request followUp = this.followUpRequest(response, route);
         if (followUp == null) {
            if (exchange != null && exchange.isDuplex()) {
               transmitter.timeoutEarlyExit();
            }

            return response;
         }

         RequestBody followUpBody = followUp.body();
         if (followUpBody != null && followUpBody.isOneShot()) {
            return response;
         }

         Util.closeQuietly(response.body());
         if (transmitter.hasExchange()) {
            exchange.detachWithViolence();
         }

         ++followUpCount;
         if (followUpCount > 20) {
            throw new ProtocolException("Too many follow-up requests: " + followUpCount);
         }

         request = followUp;
         priorResponse = response;
      }
   }

   private boolean recover(IOException e, Transmitter transmitter, boolean requestSendStarted, Request userRequest) {
      if (!this.client.retryOnConnectionFailure()) {
         return false;
      } else if (requestSendStarted && this.requestIsOneShot(e, userRequest)) {
         return false;
      } else if (!this.isRecoverable(e, requestSendStarted)) {
         return false;
      } else {
         return transmitter.canRetry();
      }
   }

   private boolean requestIsOneShot(IOException e, Request userRequest) {
      RequestBody requestBody = userRequest.body();
      return requestBody != null && requestBody.isOneShot() || e instanceof FileNotFoundException;
   }

   private boolean isRecoverable(IOException e, boolean requestSendStarted) {
      if (e instanceof ProtocolException) {
         return false;
      } else if (!(e instanceof InterruptedIOException)) {
         if (e instanceof SSLHandshakeException && e.getCause() instanceof CertificateException) {
            return false;
         } else {
            return !(e instanceof SSLPeerUnverifiedException);
         }
      } else {
         return e instanceof SocketTimeoutException && !requestSendStarted;
      }
   }

   private Request followUpRequest(Response userResponse, @Nullable Route route) throws IOException {
      if (userResponse == null) {
         throw new IllegalStateException();
      } else {
         int responseCode = userResponse.code();
         String method = userResponse.request().method();
         switch(responseCode) {
         case 307:
         case 308:
            if (!method.equals("GET") && !method.equals("HEAD")) {
               return null;
            }
         case 300:
         case 301:
         case 302:
         case 303:
            if (!this.client.followRedirects()) {
               return null;
            } else {
               String location = userResponse.header("Location");
               if (location == null) {
                  return null;
               } else {
                  HttpUrl url = userResponse.request().url().resolve(location);
                  if (url == null) {
                     return null;
                  } else {
                     boolean sameScheme = url.scheme().equals(userResponse.request().url().scheme());
                     if (!sameScheme && !this.client.followSslRedirects()) {
                        return null;
                     }

                     Request.Builder requestBuilder = userResponse.request().newBuilder();
                     if (HttpMethod.permitsRequestBody(method)) {
                        boolean maintainBody = HttpMethod.redirectsWithBody(method);
                        if (HttpMethod.redirectsToGet(method)) {
                           requestBuilder.method("GET", (RequestBody)null);
                        } else {
                           RequestBody requestBody = maintainBody ? userResponse.request().body() : null;
                           requestBuilder.method(method, requestBody);
                        }

                        if (!maintainBody) {
                           requestBuilder.removeHeader("Transfer-Encoding");
                           requestBuilder.removeHeader("Content-Length");
                           requestBuilder.removeHeader("Content-Type");
                        }
                     }

                     if (!Util.sameConnection(userResponse.request().url(), url)) {
                        requestBuilder.removeHeader("Authorization");
                     }

                     return requestBuilder.url(url).build();
                  }
               }
            }
         case 401:
            return this.client.authenticator().authenticate(route, userResponse);
         case 407:
            Proxy selectedProxy = route != null ? route.proxy() : this.client.proxy();
            if (selectedProxy.type() != Type.HTTP) {
               throw new ProtocolException("Received HTTP_PROXY_AUTH (407) code while not using proxy");
            }

            return this.client.proxyAuthenticator().authenticate(route, userResponse);
         case 408:
            if (!this.client.retryOnConnectionFailure()) {
               return null;
            } else {
               RequestBody requestBody = userResponse.request().body();
               if (requestBody != null && requestBody.isOneShot()) {
                  return null;
               } else if (userResponse.priorResponse() != null && userResponse.priorResponse().code() == 408) {
                  return null;
               } else {
                  if (this.retryAfter(userResponse, 0) > 0) {
                     return null;
                  }

                  return userResponse.request();
               }
            }
         case 503:
            if (userResponse.priorResponse() != null && userResponse.priorResponse().code() == 503) {
               return null;
            } else {
               if (this.retryAfter(userResponse, Integer.MAX_VALUE) == 0) {
                  return userResponse.request();
               }

               return null;
            }
         default:
            return null;
         }
      }
   }

   private int retryAfter(Response userResponse, int defaultDelay) {
      String header = userResponse.header("Retry-After");
      if (header == null) {
         return defaultDelay;
      } else {
         return header.matches("\\d+") ? Integer.valueOf(header).intValue() : Integer.MAX_VALUE;
      }
   }
}
