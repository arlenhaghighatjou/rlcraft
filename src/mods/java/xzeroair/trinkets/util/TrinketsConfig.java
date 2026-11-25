package xzeroair.trinkets.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Type;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.util.config.ClientConfig;
import xzeroair.trinkets.util.config.ServerConfig;
import xzeroair.trinkets.util.config.compat.CompatabilityConfigs;
import xzeroair.trinkets.util.helpers.StringUtils;

@Config(
   name = "/trinkets/Trinkets_And_Baubles",
   modid = "xat"
)
@LangKey("xat.config.title")
public class TrinketsConfig {
   private static final String cfgPrefix = "xat.config";
   @Name("Client Settings")
   @LangKey("xat.config.client.settings")
   public static ClientConfig CLIENT = new ClientConfig();
   @Name("Server Settings")
   @LangKey("xat.config.server.settings")
   public static ServerConfig SERVER = new ServerConfig();
   @Name("Compatability Settings")
   @LangKey("xat.config.compatability")
   public static CompatabilityConfigs compat = new CompatabilityConfigs();

   public TrinketsConfig() {
   }

   public static Map<String, String> writeConfigMap() {
      Map<String, String> configMap = new HashMap();
      configMap.put("oreEnabled", "" + SERVER.Items.DRAGON_EYE.oreFinder);
      configMap.put("oreEnabled.closest", "" + SERVER.Items.DRAGON_EYE.BLOCKS.closest);
      configMap.put("oreEnabled.blocks", StringUtils.combineStringArray(SERVER.Items.DRAGON_EYE.BLOCKS.Blocks));
      configMap.put("oreEnabled.hd", "" + SERVER.Items.DRAGON_EYE.BLOCKS.DR.C001_HD);
      configMap.put("oreEnabled.vd", "" + SERVER.Items.DRAGON_EYE.BLOCKS.DR.C00_VD);
      configMap.put("dragonFlight", "" + SERVER.races.dragon.creative_flight);
      configMap.put("dragonFlightCost", "" + SERVER.races.dragon.flight_cost);
      configMap.put("dragonFlightSpeed", "" + SERVER.races.dragon.flight_speed);
      configMap.put("dragonBreathCost", "" + SERVER.races.dragon.breath_cost);
      configMap.put("dragonBreathDamage", "" + SERVER.races.dragon.breath_damage);
      configMap.put("fairyFlight", "" + SERVER.races.fairy.creative_flight);
      configMap.put("fairyFlightSpeed", "" + SERVER.races.fairy.flight_speed);
      configMap.put("fairyFlightCost", "" + SERVER.races.fairy.flight_cost);
      configMap.put("TrinketContainer", "" + SERVER.GUI.guiEnabled);
      configMap.put("compatTAN", "" + compat.toughasnails);
      configMap.put("compatSD", "" + compat.simpledifficulty);
      configMap.put("compatArtemisLib", "" + compat.artemislib);
      configMap.put("compatElenaiDodge1", "" + compat.elenaiDodge);
      configMap.put("compatElenaiDodge2", "" + compat.elenaiDodge);
      configMap.put("compatEnhancedVisuals", "" + compat.enhancedvisuals);
      configMap.put("compatLycanites", "" + compat.lycanites);
      configMap.put("compatDefiledLands", "" + compat.defiledlands);
      configMap.put("miscDepthStacking", "" + SERVER.misc.depthStacks);
      configMap.put("miscSMEStriderStacking", "" + SERVER.misc.underwaterStriderStacks);
      configMap.put("miscBlockMovement", "" + SERVER.misc.movement);
      configMap.put("raceBlacklist", StringUtils.combineStringArray(SERVER.races.selectionBlacklist));
      return configMap;
   }

   public static void readConfigMap(Map<String, String> configMap) {
      if (configMap != null && !configMap.isEmpty()) {
         Trinkets.log.info("Found Server Config");

         for(Entry<String, String> config : configMap.entrySet()) {
            try {
               if (((String)config.getKey()).contentEquals("oreEnabled")) {
                  SERVER.Items.DRAGON_EYE.oreFinder = Boolean.parseBoolean((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("oreEnabled.closest")) {
                  SERVER.Items.DRAGON_EYE.BLOCKS.closest = Boolean.parseBoolean((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("oreEnabled.blocks")) {
                  SERVER.Items.DRAGON_EYE.BLOCKS.Blocks = StringUtils.deconstructStringArray((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("oreEnabled.hd")) {
                  SERVER.Items.DRAGON_EYE.BLOCKS.DR.C001_HD = Integer.parseInt((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("oreEnabled.vd")) {
                  SERVER.Items.DRAGON_EYE.BLOCKS.DR.C00_VD = Integer.parseInt((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("dragonFlight")) {
                  SERVER.races.dragon.creative_flight = Boolean.parseBoolean((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("dragonFlightCost")) {
                  SERVER.races.dragon.flight_cost = Float.parseFloat((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("dragonFlightSpeed")) {
                  SERVER.races.dragon.flight_speed = Double.parseDouble((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("dragonBreathCost")) {
                  SERVER.races.dragon.breath_cost = Float.parseFloat((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("dragonBreathDamage")) {
                  SERVER.races.dragon.breath_damage = Float.parseFloat((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("fairyFlight")) {
                  SERVER.races.fairy.creative_flight = Boolean.parseBoolean((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("fairyFlightSpeed")) {
                  SERVER.races.fairy.flight_speed = Double.parseDouble((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("fairyFlightCost")) {
                  SERVER.races.fairy.flight_cost = Float.parseFloat((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("TrinketContainer")) {
                  SERVER.GUI.guiEnabled = Boolean.parseBoolean((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("compatTAN")) {
                  compat.toughasnails = Boolean.parseBoolean((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("compatSD")) {
                  compat.simpledifficulty = Boolean.parseBoolean((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("compatArtemisLib")) {
                  compat.artemislib = Boolean.parseBoolean((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("compatElenaiDodge1")) {
                  compat.elenaiDodge = Boolean.parseBoolean((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("compatElenaiDodge2")) {
                  compat.elenaiDodge = Boolean.parseBoolean((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("compatEnhancedVisuals")) {
                  compat.enhancedvisuals = Boolean.parseBoolean((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("compatLycanites")) {
                  compat.lycanites = Boolean.parseBoolean((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("compatDefiledLands")) {
                  compat.defiledlands = Boolean.parseBoolean((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("miscDepthStacking")) {
                  SERVER.misc.depthStacks = Boolean.parseBoolean((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("miscSMEStriderStacking")) {
                  SERVER.misc.underwaterStriderStacks = Boolean.parseBoolean((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("miscBlockMovement")) {
                  SERVER.misc.movement = Boolean.parseBoolean((String)config.getValue());
               }

               if (((String)config.getKey()).contentEquals("raceBlacklist")) {
                  SERVER.races.selectionBlacklist = StringUtils.deconstructStringArray((String)config.getValue());
               }
            } catch (Exception var4) {
               var4.printStackTrace();
            }
         }
      }

   }

   public static void Save() {
      ConfigManager.sync("xat", Type.INSTANCE);
   }

   public static void readConfig() {
      Configuration cfg = Trinkets.config;

      try {
         cfg.load();
      } catch (Exception var5) {
         Trinkets.log.error("Xat had a problem loading it's configuration");
      } finally {
         if (cfg.hasChanged()) {
            cfg.save();
         }

      }

   }
}
