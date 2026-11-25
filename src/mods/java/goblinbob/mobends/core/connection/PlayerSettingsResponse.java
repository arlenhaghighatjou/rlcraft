package goblinbob.mobends.core.connection;

import goblinbob.mobends.core.supporters.AccessorySettings;
import java.util.Map;

public class PlayerSettingsResponse {
   private Map<String, AccessorySettings> settings;

   public PlayerSettingsResponse() {
   }

   public Map<String, AccessorySettings> getSettings() {
      return this.settings;
   }
}
