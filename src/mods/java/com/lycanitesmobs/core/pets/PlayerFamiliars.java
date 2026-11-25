package com.lycanitesmobs.core.pets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.Variant;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.io.IOUtils;

public class PlayerFamiliars {
   public static PlayerFamiliars INSTANCE = new PlayerFamiliars();
   public Map<EaglercraftUUID, Map<EaglercraftUUID, PetEntry>> playerFamiliars = new HashMap();
   public Map<EaglercraftUUID, Long> playerFamiliarLoadedTimes = new HashMap();
   public List<String> familiarBlacklist = new ArrayList();
   public SSLContext sslContext;

   public PlayerFamiliars() {
      TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
         public X509Certificate[] getAcceptedIssuers() {
            return null;
         }

         public void checkClientTrusted(X509Certificate[] certs, String authType) {
         }

         public void checkServerTrusted(X509Certificate[] certs, String authType) {
         }
      }};

      try {
         this.sslContext = SSLContext.getInstance("SSL");
         this.sslContext.init((KeyManager[])null, trustAllCerts, new SecureRandom());
      } catch (Exception var3) {
         var3.printStackTrace();
      }

   }

   public void parseFamiliarJSON(String jsonString) {
      try {
         JsonParser jsonParser = new JsonParser();
         JsonObject json = jsonParser.parse(jsonString).getAsJsonObject();
         JsonArray jsonArray = json.getAsJsonArray("data");
         Iterator<JsonElement> jsonIterator = jsonArray.iterator();

         while(jsonIterator.hasNext()) {
            try {
               JsonObject familiarJson = ((JsonElement)jsonIterator.next()).getAsJsonObject();
               EaglercraftUUID minecraft_uuid = EaglercraftUUID.fromString(familiarJson.get("minecraft_uuid").getAsString());
               if (!this.familiarBlacklist.contains(minecraft_uuid.toString())) {
                  EaglercraftUUID familiar_uuid = EaglercraftUUID.fromString(familiarJson.get("familiar_uuid").getAsString());
                  String familiar_species = familiarJson.get("familiar_species").getAsString();
                  CreatureInfo creatureInfo = CreatureManager.getInstance().getCreature(familiar_species);
                  int familiar_subspecies = familiarJson.get("familiar_subspecies").getAsInt();
                  familiar_species = this.handleLegacySpecies(familiar_species);
                  int familiar_variant = familiarJson.get("familiar_variant").getAsInt();
                  Variant creatureVariant = creatureInfo != null ? creatureInfo.getSubspecies(familiar_subspecies).getVariant(familiar_variant) : null;
                  String familiar_name = familiarJson.get("familiar_name").getAsString();
                  String familiar_color = familiarJson.get("familiar_color").getAsString();
                  double familiar_size = familiarJson.get("familiar_size").getAsDouble();
                  if (familiar_size <= 0.0D) {
                     familiar_size = 0.5D;
                     if (creatureVariant != null && creatureVariant.scale != 1.0D) {
                        familiar_size *= 1.0D / creatureVariant.scale;
                     }
                  }

                  PetEntryFamiliar familiarEntry = new PetEntryFamiliar(familiar_uuid, (EntityLivingBase)null, familiar_species.toLowerCase());
                  familiarEntry.setEntitySubspecies(familiar_subspecies);
                  familiarEntry.setEntityVariant(familiar_variant);
                  familiarEntry.setEntitySize(familiar_size);
                  if (!"".equals(familiar_name)) {
                     familiarEntry.setEntityName(familiar_name);
                  }

                  familiarEntry.setColor(familiar_color);
                  if (!this.playerFamiliars.containsKey(minecraft_uuid)) {
                     this.playerFamiliars.put(minecraft_uuid, new HashMap());
                  }

                  if (!((Map)this.playerFamiliars.get(minecraft_uuid)).containsKey(familiar_uuid)) {
                     ((Map)this.playerFamiliars.get(minecraft_uuid)).put(familiar_uuid, familiarEntry);
                  } else {
                     PetEntry existingEntry = (PetEntry)((Map)this.playerFamiliars.get(minecraft_uuid)).get(familiar_uuid);
                     existingEntry.copy(familiarEntry);
                  }
               }
            } catch (Exception var20) {
               ;
            }
         }
      } catch (Exception var21) {
         ;
      }

   }

   public Map<EaglercraftUUID, PetEntry> getFamiliarsForPlayer(EntityPlayer player) {
      long currentTime = System.currentTimeMillis() / 1000L;
      long loadedTime = this.getPlayerFamiliarLoadedTime(player);
      if (loadedTime < 0L || currentTime - loadedTime > 1800L) {
         this.updatePlayerFamiliarLoadedTime(player);
         PlayerFamiliars.FamiliarLoader familiarLoader = new PlayerFamiliars.FamiliarLoader(player);
         Thread thread = new Thread(familiarLoader);
         thread.start();
      }

      Map<EaglercraftUUID, PetEntry> playerFamiliarEntries = new HashMap();
      if (this.playerFamiliars.containsKey(player.getUniqueID())) {
         playerFamiliarEntries = (Map)this.playerFamiliars.get(player.getUniqueID());

         for(PetEntry familiarEntry : playerFamiliarEntries.values()) {
            if (familiarEntry.host == null) {
               familiarEntry.host = player;
            }
         }
      }

      return playerFamiliarEntries;
   }

   public long getPlayerFamiliarLoadedTime(EntityPlayer player) {
      EaglercraftUUID uuid = player.getUniqueID();
      return !this.playerFamiliarLoadedTimes.containsKey(uuid) ? -1L : ((Long)this.playerFamiliarLoadedTimes.get(uuid)).longValue();
   }

   public void updatePlayerFamiliarLoadedTime(EntityPlayer player) {
      this.playerFamiliarLoadedTimes.put(player.getUniqueID(), Long.valueOf(System.currentTimeMillis() / 1000L));
   }

   public String handleLegacySpecies(String species) {
      if (species.equalsIgnoreCase("apollyon")) {
         return "archvile";
      } else if (species.equalsIgnoreCase("belphegor")) {
         return "belph";
      } else if (species.equalsIgnoreCase("behemophet")) {
         return "behemoth";
      } else if (species.equalsIgnoreCase("malwrath")) {
         return "cacodemon";
      } else if (species.equalsIgnoreCase("naxiris")) {
         return "beholder";
      } else if (species.equalsIgnoreCase("zephyr")) {
         return "djinn";
      } else if (species.equalsIgnoreCase("raidra")) {
         return "zephyr";
      } else if (species.equalsIgnoreCase("ningen")) {
         return "dweller";
      } else if (species.equalsIgnoreCase("cherufe")) {
         return "lobber";
      } else if (species.equalsIgnoreCase("ostimien")) {
         return "lurker";
      } else if (species.equalsIgnoreCase("kathoga")) {
         return "pinky";
      } else if (species.equalsIgnoreCase("brucha")) {
         return "quillbeast";
      } else if (species.equalsIgnoreCase("stryder")) {
         return "strider";
      } else if (species.equalsIgnoreCase("lycosa")) {
         return "tarantula";
      } else {
         return species.equalsIgnoreCase("jouste") ? "joust" : species;
      }
   }

   public static class FamiliarLoader implements Runnable {
      EntityPlayer player;

      public FamiliarLoader(EntityPlayer player) {
         this.player = player;
      }

      public void run() {
         this.getForPlayerUUID(this.player.getUniqueID());
         ExtendedPlayer extendedPlayer = ExtendedPlayer.getForPlayer(this.player);
         if (extendedPlayer != null) {
            extendedPlayer.loadFamiliars();
         }

         PlayerFamiliars.INSTANCE.updatePlayerFamiliarLoadedTime(this.player);
      }

      private void getForPlayerUUID(EaglercraftUUID uuid) {
         String jsonString;
         try {
            URL url = new URL("https://service.lycanitesmobs.com/api/v1/familiars?minecraft_uuid=" + uuid.toString());
            HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
            urlConnection.setSSLSocketFactory(PlayerFamiliars.INSTANCE.sslContext.getSocketFactory());
            urlConnection.setRequestProperty("Authorization", "Bearer 7ed1f44cbc1aff693e604075f23d56402983a4a0");
            String osName = System.getProperty("os.name");
            urlConnection.setRequestProperty("User-Agent", "Minecraft 1.12.2 (" + osName + ") LycanitesMobs " + "2.0.8.9");
            InputStream inputStream = urlConnection.getInputStream();

            try {
               jsonString = IOUtils.toString(inputStream, (Charset)null);
            } catch (Exception var12) {
               throw var12;
            } finally {
               inputStream.close();
            }

            LycanitesMobs.logInfo("", "Online familiars loaded successfully for " + uuid + ".");
         } catch (Throwable var14) {
            LycanitesMobs.logInfo("", "Unable to access the online familiars service.");
            var14.printStackTrace();
            return;
         }

         PlayerFamiliars.INSTANCE.parseFamiliarJSON(jsonString);
      }
   }
}
