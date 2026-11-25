package me.lucko.spark.lib.okhttp3.internal;

import javax.annotation.Nullable;
import javax.net.ssl.SSLSocket;
import me.lucko.spark.lib.okhttp3.Address;
import me.lucko.spark.lib.okhttp3.Call;
import me.lucko.spark.lib.okhttp3.ConnectionPool;
import me.lucko.spark.lib.okhttp3.ConnectionSpec;
import me.lucko.spark.lib.okhttp3.Headers;
import me.lucko.spark.lib.okhttp3.OkHttpClient;
import me.lucko.spark.lib.okhttp3.Request;
import me.lucko.spark.lib.okhttp3.Response;
import me.lucko.spark.lib.okhttp3.internal.connection.Exchange;
import me.lucko.spark.lib.okhttp3.internal.connection.RealConnectionPool;

public abstract class Internal {
   public static Internal instance;

   public Internal() {
   }

   public static void initializeInstanceForTests() {
      new OkHttpClient();
   }

   public abstract void addLenient(Headers.Builder var1, String var2);

   public abstract void addLenient(Headers.Builder var1, String var2, String var3);

   public abstract RealConnectionPool realConnectionPool(ConnectionPool var1);

   public abstract boolean equalsNonHost(Address var1, Address var2);

   public abstract int code(Response.Builder var1);

   public abstract void apply(ConnectionSpec var1, SSLSocket var2, boolean var3);

   public abstract Call newWebSocketCall(OkHttpClient var1, Request var2);

   public abstract void initExchange(Response.Builder var1, Exchange var2);

   @Nullable
   public abstract Exchange exchange(Response var1);
}
