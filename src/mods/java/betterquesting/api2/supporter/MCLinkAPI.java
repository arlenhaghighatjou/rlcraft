package betterquesting.api2.supporter;

import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.supporter.mc_link.McLinkEndpoint;
import betterquesting.core.BetterQuesting;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public class MCLinkAPI {
   private static final String MCL_TOKEN = "Yo1nkbXn7uVptLoL3GpkAaT7HsU8QFGJ";
   private static String userAgent = null;

   public MCLinkAPI() {
   }

   @Nonnull
   public JsonObject getApiStatus() {
      return new JsonObject();
   }

   public void updateSupporterInfo(@Nonnull Collection<EaglercraftUUID> playerIDs, Collection<String> tokens) {
      JsonObject jsonBase = new JsonObject();
      JsonObject jsonTokens = new JsonObject();
      JsonObject jsonServices = new JsonObject();
      JsonArray teirArgs = new JsonArray();
      teirArgs.add(Integer.valueOf(0));
      jsonServices.add("Patreon", teirArgs);
      jsonServices.add("Twitch", new JsonArray());
      tokens.forEach((key) -> {
         jsonTokens.add(key, jsonServices);
      });
      jsonBase.add("tokens", jsonTokens);
      JsonArray jsonIds = new JsonArray();
      playerIDs.forEach((id) -> {
         jsonIds.add(id.toString());
      });
      jsonBase.add("uuids", jsonIds);

      try {
         JsonElement e = sendJsonPost(McLinkEndpoint.API_AUTH.URL, jsonBase);
      } catch (Exception var9) {
         BetterQuesting.logger.error("Unable to lookup supporter info", var9);
      }

   }

   private static JsonElement sendJsonPost(String endpoint, JsonElement json) throws IOException {
      if (userAgent == null) {
         setupMetadata();
      }

      URL url = new URL(endpoint);
      String redirect = url.toString();

      HttpURLConnection con;
      while(true) {
         url = new URL(url, redirect);
         con = (HttpURLConnection)url.openConnection();
         con.setRequestMethod("POST");
         con.setInstanceFollowRedirects(true);
         con.setAllowUserInteraction(false);
         con.setConnectTimeout(30000);
         con.setReadTimeout(30000);
         con.setDoOutput(true);
         con.setRequestProperty("User-Agent", userAgent);
         con.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
         con.setRequestProperty("Accept", "application/json");
         con.setRequestProperty("Content-Type", "application/json; charset=" + StandardCharsets.UTF_8.name());
         OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream(), StandardCharsets.UTF_8);
         Throwable isr = null;

         try {
            JsonHelper.GSON.toJson(json, osw);
            osw.flush();
            redirect = con.getHeaderField("Location");
         } catch (Throwable var31) {
            isr = var31;
            throw var31;
         } finally {
            if (osw != null) {
               if (isr != null) {
                  try {
                     osw.close();
                  } catch (Throwable var29) {
                     isr.addSuppressed(var29);
                  }
               } else {
                  osw.close();
               }
            }

         }

         if (redirect == null || con.getResponseCode() / 100 != 3) {
            break;
         }
      }

      InputStream is = con.getErrorStream();
      if (is == null) {
         is = con.getInputStream();
      }

      InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
      Throwable var7 = null;

      Object var9;
      try {
         JsonElement jsonOut = (JsonElement)JsonHelper.GSON.fromJson(isr, JsonElement.class);
         var9 = jsonOut != null ? jsonOut : new JsonObject();
      } catch (Throwable var30) {
         var7 = var30;
         throw var30;
      } finally {
         if (isr != null) {
            if (var7 != null) {
               try {
                  isr.close();
               } catch (Throwable var28) {
                  var7.addSuppressed(var28);
               }
            } else {
               isr.close();
            }
         }

      }

      return (JsonElement)var9;
   }

   private static void setupMetadata() {
      String modVersion = "3.5.329".equalsIgnoreCase("@VERSION@") ? "DEV" : "3.5.329";
      String mcVersion = "1.12.2";
      String branding = "Forge";
      StringBuilder sb = new StringBuilder("BetterQuesting");
      sb.append('/').append(modVersion.replaceAll("[;()\n\r]", ""));
      sb.append(" (APIv").append(1).append("; ");
      sb.append("MCv").append(mcVersion.replaceAll("[;()\n\r]", "")).append("; ");
      sb.append(branding.replaceAll("[;()\n\r]", "")).append("; ");
      String os = System.getProperty("os.name") + ' ' + System.getProperty("os.arch") + ' ' + System.getProperty("os.version");
      sb.append(os.replaceAll("[;()\n\r]", ""));
      userAgent = sb.append(')').toString();
   }
}
