package me.lucko.spark.lib.okhttp3;

import javax.annotation.Nullable;
import me.lucko.spark.lib.okio.ByteString;

public abstract class WebSocketListener {
   public WebSocketListener() {
   }

   public void onOpen(WebSocket webSocket, Response response) {
   }

   public void onMessage(WebSocket webSocket, String text) {
   }

   public void onMessage(WebSocket webSocket, ByteString bytes) {
   }

   public void onClosing(WebSocket webSocket, int code, String reason) {
   }

   public void onClosed(WebSocket webSocket, int code, String reason) {
   }

   public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
   }
}
