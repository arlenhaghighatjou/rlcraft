package goblinbob.mobends.core.connection;

import com.google.gson.JsonObject;
import goblinbob.mobends.core.util.ConnectionHelper;
import goblinbob.mobends.standard.main.MoBends;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URL;

public class PingTask implements Runnable {
   private final String apiUrl;
   private final float interval;

   public PingTask(String apiUrl, float interval) {
      this.apiUrl = apiUrl;
      this.interval = interval;
   }

   public void run() {
      while(true) {
         try {
            this.sendPing();
            Thread.sleep((long)(this.interval * 1000.0F));
         } catch (InterruptedException var2) {
            MoBends.LOG.warning("PingTask has been interrupted");
            return;
         }
      }
   }

   private void sendPing() {
      JsonObject body = new JsonObject();
      body.addProperty("app", "mobends");
      body.addProperty("version", "1.2.1/1.12.2-forge");

      try {
         ConnectionHelper.sendPostRequest(new URL(this.apiUrl + "/api/activity/ping"), body, PingTask.PingResponse.class);
      } catch (ConnectException var3) {
         ;
      } catch (IOException var4) {
         MoBends.LOG.warning("API ping failed.");
      }

   }

   private static class PingResponse {
      public boolean success;

      private PingResponse() {
      }
   }
}
