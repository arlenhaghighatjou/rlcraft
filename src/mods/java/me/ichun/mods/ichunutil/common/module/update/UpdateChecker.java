package me.ichun.mods.ichunutil.common.module.update;

import com.google.common.collect.Ordering;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

public class UpdateChecker {
   private static TreeSet<UpdateChecker.ModVersionInfo> registeredMods = new TreeSet(Ordering.natural());
   private static TreeSet<UpdateChecker.ModVersionInfo> modsWithUpdates = new TreeSet(Ordering.natural());
   private static boolean updatesChecked;
   private static boolean requireLogging;

   public UpdateChecker() {
   }

   public static TreeSet<UpdateChecker.ModVersionInfo> getModsWithUpdates() {
      return modsWithUpdates;
   }

   public static void registerMod(UpdateChecker.ModVersionInfo info) {
      registeredMods.add(info);
   }

   public static void processModsList(Map<String, Object> json) {
      for(UpdateChecker.ModVersionInfo info : registeredMods) {
         Map<String, String> versionInfo = (Map)json.get(info.modName);
         if (versionInfo != null && versionInfo.containsKey(info.mcVersion)) {
            ArtifactVersion current = new DefaultArtifactVersion(info.modVersion);
            String lat = (String)versionInfo.get(info.mcVersion);
            ArtifactVersion latest = new DefaultArtifactVersion(lat);
            int diff = latest.compareTo(current);
            if (diff > 0) {
               info.modVersionNew = lat.trim();
               modsWithUpdates.add(info);
               if (requireLogging && !info.isModClientOnly) {
                  iChunUtil.LOGGER.info("[NEW UPDATE AVAILABLE] " + info.modName + " - " + info.modVersionNew);
               }
            }
         }
      }

      requireLogging = false;
      updatesChecked = true;
   }

   public static boolean hasCheckedForUpdates() {
      return updatesChecked;
   }

   public static void serverStarted() {
      if (hasCheckedForUpdates()) {
         getModsWithUpdates().stream().filter((info) -> {
            return !info.isModClientOnly;
         }).forEach((info) -> {
            iChunUtil.LOGGER.info("[NEW UPDATE AVAILABLE] " + info.modName + " - " + info.modVersionNew);
         });
      } else {
         requireLogging = true;
      }

   }

   public static class ModVersionInfo implements Comparable<UpdateChecker.ModVersionInfo> {
      public final String modName;
      public final String mcVersion;
      public final String modVersion;
      public final boolean isModClientOnly;
      public String modVersionNew;

      public ModVersionInfo(String modName, String mcVersion, String modVersion, boolean isModClientOnly) {
         this.modName = modName;
         this.mcVersion = mcVersion;
         this.modVersion = modVersion;
         this.isModClientOnly = isModClientOnly;
      }

      public boolean equals(Object o) {
         return o instanceof UpdateChecker.ModVersionInfo && ((UpdateChecker.ModVersionInfo)o).modName.equals(this.modName);
      }

      public int compareTo(UpdateChecker.ModVersionInfo o) {
         return this.modName.compareTo(o.modName);
      }
   }
}
