package com.lycanitesmobs.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class VersionChecker {
   public static VersionChecker INSTANCE = new VersionChecker();
   public boolean enabled = true;
   protected VersionChecker.VersionInfo currentVersion = new VersionChecker.VersionInfo("2.0.8.9", "1.12.2");
   protected VersionChecker.VersionInfo latestVersion = null;
   protected long lastChecked = -1L;
   public SSLContext sslContext;

   public VersionChecker() {
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

   public VersionChecker.VersionInfo getLatestVersion() {
      if (this.latestVersion != null) {
         return this.latestVersion;
      } else {
         long currentTime = System.currentTimeMillis() / 1000L;
         if (this.lastChecked < 0L || currentTime - this.lastChecked > 3600L) {
            this.lastChecked = currentTime;
            VersionChecker.VersionLoader versionLoader = new VersionChecker.VersionLoader();
            Thread thread = new Thread(versionLoader);
            thread.start();
         }

         return this.latestVersion != null ? this.latestVersion : this.currentVersion;
      }
   }

   public static class VersionInfo {
      public String versionNumber;
      public String mcVersion;
      public String name = "";
      public String newFeatures = "";
      public String configChanges = "";
      public String majorFixes = "";
      public String changes = "";
      public String balancing = "";
      public String minorFixes = "";
      public boolean isNewer = false;

      public VersionInfo(String versionNumber, String mcVersion) {
         this.versionNumber = versionNumber;
         this.mcVersion = mcVersion;
      }

      public void loadFromJSON(JsonObject versionJson) {
         this.versionNumber = versionJson.get("version").getAsString();
         this.mcVersion = versionJson.get("mcversion").getAsString();
         this.name = versionJson.get("name").getAsString();
         this.newFeatures = versionJson.get("new").getAsString();
         this.configChanges = versionJson.get("config_changes").getAsString();
         this.majorFixes = versionJson.get("major_fixes").getAsString();
         this.changes = versionJson.get("changes").getAsString();
         this.balancing = versionJson.get("balancing").getAsString();
         this.minorFixes = versionJson.get("minor_fixes").getAsString();
      }

      public void checkIfNewer(VersionChecker.VersionInfo compareVersion) {
         this.isNewer = false;
         String[] versions = this.versionNumber.split("\\.");
         String[] compareVersions = compareVersion.versionNumber.split("\\.");

         for(int i = 0; i < 4; ++i) {
            int versionNumber = NumberUtils.isCreatable(versions[i].replaceAll("[^\\d.]", "")) ? Integer.parseInt(versions[i].replaceAll("[^\\d.]", "")) : 0;
            int compareVersionNumber = NumberUtils.isCreatable(compareVersions[i].replaceAll("[^\\d.]", "")) ? Integer.parseInt(compareVersions[i].replaceAll("[^\\d.]", "")) : 0;
            if (versionNumber > compareVersionNumber) {
               this.isNewer = true;
               return;
            }

            if (versionNumber != compareVersionNumber) {
               return;
            }
         }

      }
   }

   public static class VersionLoader implements Runnable {
      public VersionLoader() {
      }

      public void run() {
         VersionChecker.INSTANCE.lastChecked = System.currentTimeMillis() / 1000L;

         try {
            URL url = new URL("https://service.lycanitesmobs.com/api/v1/versions?limit=1&sort_by=created_at:desc&mcversion=1.12.2");
            HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
            urlConnection.setSSLSocketFactory(VersionChecker.INSTANCE.sslContext.getSocketFactory());
            urlConnection.setRequestProperty("Authorization", "Bearer 7ed1f44cbc1aff693e604075f23d56402983a4a0");
            String osName = System.getProperty("os.name");
            urlConnection.setRequestProperty("User-Agent", "Minecraft 1.12.2 (" + osName + ") LycanitesMobs " + "2.0.8.9");
            InputStream inputStream = urlConnection.getInputStream();

            String var16;
            try {
               jsonString = IOUtils.toString(inputStream, (Charset)null);
               var16 = var16.replace("\\r", "");
            } finally {
               inputStream.close();
            }

            JsonParser jsonParser = new JsonParser();
            JsonObject json = jsonParser.parse(var16).getAsJsonObject();
            JsonArray jsonArray = json.getAsJsonArray("data");
            JsonObject versionJson = jsonArray.get(0).getAsJsonObject();
            if (!versionJson.has("version") && !versionJson.has("mcversion")) {
               return;
            }

            String versionNumber = versionJson.get("version").getAsString();
            String mcVersion = versionJson.get("mcversion").getAsString();
            VersionChecker.INSTANCE.latestVersion = new VersionChecker.VersionInfo(versionNumber, mcVersion);
            VersionChecker.INSTANCE.latestVersion.loadFromJSON(versionJson);
            VersionChecker.INSTANCE.latestVersion.checkIfNewer(VersionChecker.INSTANCE.currentVersion);
         } catch (Throwable var15) {
            var15.printStackTrace();
         }

      }
   }
}
