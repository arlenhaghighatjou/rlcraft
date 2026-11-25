package goblinbob.mobends.core.connection;

import goblinbob.mobends.core.supporters.SupporterContent;
import goblinbob.mobends.core.util.ConnectionHelper;
import goblinbob.mobends.standard.main.MoBends;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.http.conn.HttpHostConnectException;

public class PlayerSettingsDownloader implements Runnable {
   private final String apiUrl;
   private final Set<String> loadingPlayersSet = new HashSet();
   private BlockingQueue<PlayerSettingsTask> playerSettingsTasks = new LinkedBlockingQueue();

   public PlayerSettingsDownloader(String apiUrl) {
      this.apiUrl = apiUrl;
   }

   public void run() {
      while(true) {
         try {
            PlayerSettingsTask task = (PlayerSettingsTask)this.playerSettingsTasks.take();

            try {
               this.performTask(task);
            } catch (HttpHostConnectException var5) {
               ;
            } catch (URISyntaxException | IOException var6) {
               MoBends.LOG.warning("API player settings fetch failed.");
               var6.printStackTrace();
            }

            synchronized(this.loadingPlayersSet) {
               this.loadingPlayersSet.remove(task.playerName);
            }
         } catch (InterruptedException var7) {
            MoBends.LOG.warning("AssetDownloader has been interrupted");
            return;
         }
      }
   }

   private void performTask(PlayerSettingsTask task) throws IOException, URISyntaxException {
      Map<String, String> params = new HashMap();
      params.put("username", task.playerName);
      ConnectionHelper var10000 = ConnectionHelper.INSTANCE;
      PlayerSettingsResponse response = (PlayerSettingsResponse)ConnectionHelper.sendGetRequest(new URL(this.apiUrl + "/api/accessory"), params, PlayerSettingsResponse.class);
      SupporterContent.registerPlayerAccessorySettings(task.playerName, response);
      MoBends.LOG.info("Player settings fetch successful");
   }

   public void fetchSettingsForPlayer(String playerName) {
      synchronized(this.loadingPlayersSet) {
         if (!this.loadingPlayersSet.contains(playerName)) {
            this.playerSettingsTasks.add(new PlayerSettingsTask(playerName));
            this.loadingPlayersSet.add(playerName);
         }

      }
   }
}
