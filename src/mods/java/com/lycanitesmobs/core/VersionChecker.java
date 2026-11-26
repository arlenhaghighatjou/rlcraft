package com.lycanitesmobs.core;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.math.NumberUtils;

public class VersionChecker {
   public static VersionChecker INSTANCE = new VersionChecker();
   public boolean enabled = true;
   protected VersionChecker.VersionInfo currentVersion = new VersionChecker.VersionInfo("2.0.8.9", "1.12.2");
   protected VersionChecker.VersionInfo latestVersion = null;
   protected long lastChecked = -1L;

   public VersionChecker() {
   }

   public VersionChecker.VersionInfo getLatestVersion() {
      if (this.latestVersion != null) {
         return this.latestVersion;
      } else {
         return this.currentVersion;
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
}
