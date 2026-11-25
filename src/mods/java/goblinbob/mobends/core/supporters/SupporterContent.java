package goblinbob.mobends.core.supporters;

import com.google.gson.JsonParseException;
import goblinbob.mobends.core.Core;
import goblinbob.mobends.core.connection.ConnectionManager;
import goblinbob.mobends.core.connection.PlayerSettingsResponse;
import goblinbob.mobends.core.env.EnvironmentModule;
import goblinbob.mobends.core.module.IModule;
import goblinbob.mobends.core.util.Color;
import goblinbob.mobends.core.util.ConnectionHelper;
import goblinbob.mobends.core.util.IColorRead;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.http.conn.HttpHostConnectException;

public class SupporterContent {
   private static SupporterContent INSTANCE;
   private static final Color DEFAULT_TRAIL_COLOR = new Color(Color.WHITE);
   private final String apiUrl;
   private final Map<String, AccessoryDetails> accessoryDetailsMap;
   private final Map<String, PlayerSettingsResponse> accessorySettingsPerPlayer;

   private SupporterContent() {
      this.accessoryDetailsMap = new HashMap();
      this.accessorySettingsPerPlayer = new HashMap();
      this.apiUrl = EnvironmentModule.getConfig().getApiUrl();
   }

   public static void fetchAccessoryDetails() {
      try {
         AccessoryDetailsResponse response = (AccessoryDetailsResponse)ConnectionHelper.sendGetRequest(new URL(INSTANCE.apiUrl + "/api/accessory/details"), new HashMap(), AccessoryDetailsResponse.class);
         Map<String, AccessoryDetails> details = ((AccessoryDetailsResponse)Objects.requireNonNull(response)).getDetails();
         INSTANCE.accessoryDetailsMap.clear();
         INSTANCE.accessoryDetailsMap.putAll(details);
      } catch (HttpHostConnectException var2) {
         ;
      } catch (JsonParseException var3) {
         Core.LOG.warning("Failed to parse accessory details map.");
         var3.printStackTrace();
      } catch (IOException | URISyntaxException | NullPointerException var4) {
         Core.LOG.warning("Failed to get accessory details map.");
         var4.printStackTrace();
      }

   }

   private void clearCache() {
      synchronized(this.accessorySettingsPerPlayer) {
         this.accessorySettingsPerPlayer.clear();
      }
   }

   public static void registerPlayerAccessorySettings(String playerName, PlayerSettingsResponse settings) {
      synchronized(INSTANCE.accessorySettingsPerPlayer) {
         INSTANCE.accessorySettingsPerPlayer.put(playerName, settings);
      }
   }

   public static Set<Entry<String, AccessoryDetails>> getAccessories() {
      return Collections.unmodifiableSet(INSTANCE.accessoryDetailsMap.entrySet());
   }

   public static Map<String, AccessorySettings> getAccessorySettingsMapFor(EntityLivingBase entity) {
      String name = entity.getName();
      PlayerSettingsResponse settings;
      synchronized(INSTANCE.accessorySettingsPerPlayer) {
         settings = (PlayerSettingsResponse)INSTANCE.accessorySettingsPerPlayer.get(name);
      }

      if (settings == null) {
         ConnectionManager.INSTANCE.fetchSettingsForPlayer(name);
         return Collections.emptyMap();
      } else {
         return settings.getSettings();
      }
   }

   public static IColorRead getTrailColorFor(EntityLivingBase entity) {
      Map<String, AccessorySettings> settingsMap = getAccessorySettingsMapFor(entity);
      AccessorySettings swordTrailSettings = (AccessorySettings)settingsMap.get("sword_trail");
      return (IColorRead)(swordTrailSettings == null ? DEFAULT_TRAIL_COLOR : swordTrailSettings.getColor());
   }

   public static class Factory implements IModule {
      public Factory() {
      }

      public void preInit(FMLPreInitializationEvent event) {
         SupporterContent.INSTANCE = new SupporterContent();
         SupporterContent.fetchAccessoryDetails();
      }

      public void onRefresh() {
         SupporterContent.fetchAccessoryDetails();
         SupporterContent.INSTANCE.clearCache();
      }
   }
}
