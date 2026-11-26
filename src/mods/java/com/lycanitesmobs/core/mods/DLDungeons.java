package com.lycanitesmobs.core.mods;

import jaredbgreat.dldungeons.api.DLDungeonsAPI;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraftforge.fml.common.Loader;

public class DLDungeons {
   public static String API_CLASS_NAME = "jaredbgreat.dldungeons.api.DLDungeonsAPI";

   public DLDungeons() {
   }

   public static void init() {
      if (Loader.isModLoaded("DLDungeonsJBG") || Loader.isModLoaded("dldungeonsjbg")) {
         LycanitesMobs.logInfo("", "Doomlike Dungeons Mod Detected...");
      }
   }

   public static void addMob(CreatureInfo creatureInfo) {
      String mobName = creatureInfo.getEntityId();
      String themes = "";
      boolean first = true;

      for(String biomeTag : creatureInfo.creatureSpawn.biomeTags) {
         if (!first) {
            themes = themes + ",";
         }

         first = false;
         if (biomeTag.charAt(0) == '-' || biomeTag.charAt(0) == '+') {
            biomeTag = biomeTag.substring(1);
         }

         if ("DRY".equalsIgnoreCase(biomeTag)) {
            biomeTag = "DESERT";
         } else if ("COLD".equalsIgnoreCase(biomeTag)) {
            biomeTag = "FROZEN";
         } else if ("VOID".equalsIgnoreCase(biomeTag)) {
            biomeTag = "SHADOW";
         } else if ("SPOOKY".equalsIgnoreCase(biomeTag)) {
            biomeTag = "NECRO";
         }

         themes = themes + biomeTag;
      }

      if (!"".equalsIgnoreCase(themes) && creatureInfo.dungeonLevel > 0) {
         try {
            DLDungeonsAPI.addMob(mobName, creatureInfo.dungeonLevel, themes.split(","));
            LycanitesMobs.logDebug("MobSetup", "[DLDungeons] Added " + mobName + " with the level: " + creatureInfo.dungeonLevel + " and themes: " + themes);
         } catch (Exception var6) {
            LycanitesMobs.logWarning("", "Unable to add " + mobName + " to DLDungeons API:");
         }
      }

   }
}
