package goblinbob.mobends.core.connection;

import com.google.gson.JsonObject;
import goblinbob.mobends.core.env.EnvironmentModule;
import goblinbob.mobends.core.module.IModule;
import goblinbob.mobends.core.util.ConnectionHelper;
import goblinbob.mobends.core.util.ErrorReporter;
import goblinbob.mobends.standard.main.MoBends;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ConnectionManager {
   public static ConnectionManager INSTANCE;
   private boolean initialized;
   private PingTask pingTask;
   private Thread pingTaskThread;
   private PlayerSettingsDownloader playerSettingsDownloader;
   private Thread playerSettingsDownloaderThread;

   private ConnectionManager() {
      this.initialized = false;
      this.setup();
   }

   public void setup() {
      if (!this.initialized) {
         String apiUrl = EnvironmentModule.getConfig().getApiUrl();
         ConnectionManager.JoinResponse response = null;

         try {
            JsonObject body = new JsonObject();
            body.addProperty("app", "mobends");
            body.addProperty("version", "1.2.1/1.12.2-forge");
            response = (ConnectionManager.JoinResponse)ConnectionHelper.sendPostRequest(new URL(apiUrl + "/api/activity/join"), body, ConnectionManager.JoinResponse.class);
            MoBends.LOG.info("Ping interval: " + response.pingInterval);
         } catch (IOException var4) {
            ErrorReporter.showErrorToPlayer("Couldn't join the API. Some features may be disabled. Contact the developers if this is a prolonged issue.");
            var4.printStackTrace();
            MoBends.LOG.log(Level.SEVERE, var4.getMessage());
            return;
         }

         this.pingTask = new PingTask(apiUrl, response.pingInterval);
         this.pingTaskThread = new Thread(this.pingTask);
         this.pingTaskThread.setDaemon(true);
         this.pingTaskThread.start();
         this.playerSettingsDownloader = new PlayerSettingsDownloader(apiUrl);
         this.playerSettingsDownloaderThread = new Thread(this.playerSettingsDownloader);
         this.playerSettingsDownloaderThread.setDaemon(true);
         this.playerSettingsDownloaderThread.start();
         this.initialized = true;
      }
   }

   public void fetchSettingsForPlayer(String playerName) {
      if (this.playerSettingsDownloader != null) {
         this.playerSettingsDownloader.fetchSettingsForPlayer(playerName);
      }

   }

   public static class Factory implements IModule {
      public Factory() {
      }

      public void preInit(FMLPreInitializationEvent event) {
         ConnectionManager.INSTANCE = new ConnectionManager();
      }

      public void onRefresh() {
         ConnectionManager.INSTANCE.setup();
      }
   }

   private static class JoinResponse {
      public float pingInterval;

      private JoinResponse() {
      }
   }
}
