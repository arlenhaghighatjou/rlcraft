package com.lycanitesmobs.core.mods;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import java.lang.reflect.Method;
import net.minecraftforge.fml.common.Loader;

public class DLDungeons {
   public static String API_CLASS_NAME = "jaredbgreat.dldungeons.api.DLDungeonsAPI";

   public DLDungeons() {
   }

   public static void init() {
      if (Loader.isModLoaded("DLDungeonsJBG") || Loader.isModLoaded("dldungeonsjbg")) {
         LycanitesMobs.logInfo("", "Doomlike Dungeons Mod Detected...");
         Class dlDungeonsAPI = null;
         Method isLoaded = null;

         try {
            dlDungeonsAPI = Class.forName(API_CLASS_NAME);
            isLoaded = dlDungeonsAPI.getMethod("isLoaded");
         } catch (Exception var4) {
            LycanitesMobs.logWarning("", "Unable to find DLDungeons API Class/Method via reflection:");
            var4.printStackTrace();
         }

         if (dlDungeonsAPI != null && isLoaded != null) {
            try {
               if (((Boolean)isLoaded.invoke((Object)null)).booleanValue()) {
                  CreatureManager.getInstance().dlDungeonsLoaded = true;
               }
            } catch (Exception var3) {
               LycanitesMobs.logWarning("", "Unable to invoke DLDungeons API method isLoaded():");
               var3.printStackTrace();
            }

         }
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
            Class dlDungeonsAPI = Class.forName(API_CLASS_NAME);
            Method addMob = dlDungeonsAPI.getMethod("addMob", String.class, Integer.TYPE, String[].class);
            addMob.invoke((Object)null, mobName, creatureInfo.dungeonLevel, themes.split(","));
            LycanitesMobs.logDebug("MobSetup", "[DLDungeons] Added " + mobName + " with the level: " + creatureInfo.dungeonLevel + " and themes: " + themes);
         } catch (Exception var6) {
            LycanitesMobs.logWarning("", "Unable to add " + mobName + " to DLDungeons API:");
         }
      }

   }
}
