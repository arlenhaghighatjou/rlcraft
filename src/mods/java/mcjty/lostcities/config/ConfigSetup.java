package mcjty.lostcities.config;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import mcjty.lostcities.LostCities;
import mcjty.lostcities.setup.ModSetup;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class ConfigSetup {
   private static Configuration mainConfig;
   public static Map<String, Configuration> profileConfigs = new HashMap();

   public ConfigSetup() {
   }

   public static void init() {
      mainConfig = new Configuration(new File(ModSetup.modConfigDir.getPath() + File.separator + "lostcities", "general.cfg"));
      Configuration cfg = mainConfig;
      boolean var8 = false;

      label143: {
         try {
            var8 = true;
            cfg.load();
            String[] e1 = LostCityConfiguration.init(cfg);
            initProfiles(e1, true);
            e1 = LostCityConfiguration.getPrivateProfiles(cfg);
            initProfiles(e1, false);
            fixConfigs();
            var8 = false;
            break label143;
         } catch (Exception var9) {
            FMLLog.log(Level.ERROR, var9, "Problem loading config file!", new Object[0]);
            var8 = false;
         } finally {
            if (var8) {
               if (mainConfig.hasChanged()) {
                  mainConfig.save();
               }

               Iterator var4 = profileConfigs.values().iterator();

               while(true) {
                  if (!var4.hasNext()) {
                     ;
                  } else {
                     Configuration config = (Configuration)var4.next();
                     if (config.hasChanged()) {
                        config.save();
                     }
                  }
               }
            }
         }

         if (mainConfig.hasChanged()) {
            mainConfig.save();
         }

         for(Configuration config : profileConfigs.values()) {
            if (config.hasChanged()) {
               config.save();
            }
         }

         return;
      }

      if (mainConfig.hasChanged()) {
         mainConfig.save();
      }

      for(Configuration config : profileConfigs.values()) {
         if (config.hasChanged()) {
            config.save();
         }
      }

   }

   private static void initProfiles(String[] profileList, boolean isPublic) {
      for(String name : profileList) {
         LostCityProfile profile = new LostCityProfile(name, (LostCityProfile)LostCityConfiguration.standardProfiles.get(name), isPublic);
         Configuration profileCfg = new Configuration(new File(ModSetup.modConfigDir.getPath() + File.separator + "lostcities", "profile_" + name + ".cfg"));
         profileCfg.load();
         profile.init(profileCfg);
         LostCityConfiguration.profiles.put(name, profile);
         profileConfigs.put(name, profileCfg);
      }

   }

   private static void fixConfigs() {
      for(Entry<String, LostCityProfile> entry : LostCityConfiguration.profiles.entrySet()) {
         String name = (String)entry.getKey();
         LostCityProfile profile = (LostCityProfile)entry.getValue();
         if (profile.CITYSPHERE_OUTSIDE_GROUNDLEVEL != -1) {
            if (!profile.CITYSPHERE_OUTSIDE_PROFILE.isEmpty()) {
               String otherName = profile.CITYSPHERE_OUTSIDE_PROFILE;
               LostCityProfile otherProfile = (LostCityProfile)LostCityConfiguration.profiles.get(otherName);
               if (otherProfile != null) {
                  LostCities.setup.getLogger().info("Migrating deprecated 'outsideGroundLevel' from '" + name + "' to '" + otherName + "'");
                  otherProfile.GROUNDLEVEL = profile.CITYSPHERE_OUTSIDE_GROUNDLEVEL;
                  otherProfile.WATERLEVEL_OFFSET = profile.WATERLEVEL_OFFSET;
                  ((Configuration)profileConfigs.get(otherName)).getCategory(otherProfile.getCategoryLostcity()).get("groundLevel").set(otherProfile.GROUNDLEVEL);
                  ((Configuration)profileConfigs.get(otherName)).getCategory(otherProfile.getCategoryLostcity()).get("waterLevelOffset").set(otherProfile.WATERLEVEL_OFFSET);
               }
            }

            profile.CITYSPHERE_OUTSIDE_GROUNDLEVEL = -1;
            profile.WATERLEVEL_OFFSET = 8;
            ((Configuration)profileConfigs.get(name)).getCategory(profile.getCategoryCitySpheres()).get("outsideGroundLevel").set(profile.CITYSPHERE_OUTSIDE_GROUNDLEVEL);
            ((Configuration)profileConfigs.get(name)).getCategory(profile.getCategoryLostcity()).get("waterLevelOffset").set(profile.WATERLEVEL_OFFSET);
         }
      }

   }

   public static void postInit() {
      if (mainConfig.hasChanged()) {
         mainConfig.save();
      }

      for(Configuration config : profileConfigs.values()) {
         if (config.hasChanged()) {
            config.save();
         }
      }

   }
}
