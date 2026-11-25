package com.charles445.simpledifficulty.config;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.config.ClientConfig;
import com.charles445.simpledifficulty.api.config.ClientOptions;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.config.compat.ConfigServerCompatibility;
import com.charles445.simpledifficulty.network.MessageConfigLAN;
import com.charles445.simpledifficulty.network.MessageUpdateConfig;
import com.charles445.simpledifficulty.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Config(
   modid = "simpledifficulty"
)
public class ModConfig {
   @Comment({"Client configuration"})
   @Name("Client")
   public static final ModConfig.ConfigClientConfig client = new ModConfig.ConfigClientConfig();
   @Comment({"Server configuration"})
   @Name("Server")
   public static final ModConfig.ConfigServerConfig server = new ModConfig.ConfigServerConfig();

   public ModConfig() {
   }

   public static void sendLocalClientConfigToAPI() {
      ClientConfig.instance.put(ClientOptions.DEBUG, Boolean.valueOf(client.clientdebug));
      ClientConfig.instance.put(ClientOptions.DRAW_THIRST_SATURATION, Boolean.valueOf(client.drawThirstSaturation));
      ClientConfig.instance.put(ClientOptions.ENABLE_THERMOMETER, Boolean.valueOf(client.thermometer.enableThermometer));
      ClientConfig.instance.put(ClientOptions.ALTERNATE_TEMP, Boolean.valueOf(client.alternateTemp));
      ClientConfig.instance.put(ClientOptions.HUD_THERMOMETER, Boolean.valueOf(client.thermometer.hudThermometer));
      ClientConfig.instance.put(ClientOptions.HUD_THERMOMETERX, Integer.valueOf(client.thermometer.hudThermometerX));
      ClientConfig.instance.put(ClientOptions.HUD_THERMOMETERY, Integer.valueOf(client.thermometer.hudThermometerY));
      ClientConfig.instance.put(ClientOptions.TEMPERATURE_READOUT, Boolean.valueOf(client.temperatureReadout));
      ClientConfig.instance.put(ClientOptions.CLASSICHUD_TEMPERATURE, Boolean.valueOf(client.classicHUDTemperature));
      ClientConfig.instance.put(ClientOptions.CLASSICHUD_THIRST, Boolean.valueOf(client.classicHUDThirst));
      ClientConfig.instance.put(ClientOptions.HEATER_PARTICLES, Boolean.valueOf(client.heaterParticles));
   }

   public static void sendLocalServerConfigToAPI() {
      ServerConfig.instance.put(ServerOptions.DEBUG, Boolean.valueOf(server.debug));
      ServerConfig.instance.put(ServerOptions.THIRST_ENABLED, Boolean.valueOf(server.thirstEnabled));
      ServerConfig.instance.put(ServerOptions.THIRST_DRINK_BLOCKS, Boolean.valueOf(server.thirstDrinkBlocks));
      ServerConfig.instance.put(ServerOptions.THIRST_DRINK_RAIN, Boolean.valueOf(server.thirstDrinkRain));
      ServerConfig.instance.put(ServerOptions.PEACEFUL_DANGER, Boolean.valueOf(server.peacefulDanger));
      ServerConfig.instance.put(ServerOptions.TEMPERATURE_ENABLED, Boolean.valueOf(server.temperatureEnabled));
      ServerConfig.instance.put(ServerOptions.TEMPERATURE_TE_ENABLED, Boolean.valueOf(server.temperatureTEEnabled));
      ServerConfig.instance.put(ServerOptions.CANTEEN_DOSES, Integer.valueOf(server.canteenDoses));
      ServerConfig.instance.put(ServerOptions.STRICT_HEATERS, Boolean.valueOf(server.strictHeaters));
      ServerConfig.instance.put(ServerOptions.IRON_CANTEEN_DOSES, Integer.valueOf(server.ironCanteenDoses));
      ServerConfig.instance.put(ServerOptions.INFINITE_PURIFIED_WATER, Boolean.valueOf(server.infinitePurifiedWater));
   }

   private static MessageUpdateConfig getNewConfigMessage() {
      NBTTagCompound compound = new NBTTagCompound();
      compound.setString(ServerOptions.DEBUG.getName(), "" + server.debug);
      compound.setString(ServerOptions.THIRST_ENABLED.getName(), "" + server.thirstEnabled);
      compound.setString(ServerOptions.THIRST_DRINK_BLOCKS.getName(), "" + server.thirstDrinkBlocks);
      compound.setString(ServerOptions.THIRST_DRINK_RAIN.getName(), "" + server.thirstDrinkRain);
      compound.setString(ServerOptions.PEACEFUL_DANGER.getName(), "" + server.peacefulDanger);
      compound.setString(ServerOptions.TEMPERATURE_ENABLED.getName(), "" + server.temperatureEnabled);
      compound.setString(ServerOptions.TEMPERATURE_TE_ENABLED.getName(), "" + server.temperatureTEEnabled);
      compound.setString(ServerOptions.CANTEEN_DOSES.getName(), "" + server.canteenDoses);
      compound.setString(ServerOptions.STRICT_HEATERS.getName(), "" + server.strictHeaters);
      compound.setString(ServerOptions.IRON_CANTEEN_DOSES.getName(), "" + server.ironCanteenDoses);
      compound.setString(ServerOptions.INFINITE_PURIFIED_WATER.getName(), "" + server.infinitePurifiedWater);
      return new MessageUpdateConfig(compound);
   }

   public static void sendServerConfigToPlayer(EntityPlayerMP player) {
      SimpleDifficulty.logger.info("Sending Configuration to player: " + player.getName());
      PacketHandler.instance.sendTo(getNewConfigMessage(), player);
   }

   public static void sendServerConfigToAllPlayers() {
      SimpleDifficulty.logger.info("Sending Configuration to all players");
      PacketHandler.instance.sendToAll(getNewConfigMessage());
   }

   public static class ConfigClientConfig {
      @Comment({"Thermometer Configuration"})
      @Name("Thermometer")
      public final ModConfig.ConfigClientConfig.ConfigClientThermometer thermometer = new ModConfig.ConfigClientConfig.ConfigClientThermometer();
      @Comment({"Whether the alternate temperature display is enabled"})
      @Name("AlternateTemperature")
      public boolean alternateTemp = true;
      @Comment({"Whether to draw thirst saturation on the HUD"})
      @Name("DrawThirstSaturation")
      public boolean drawThirstSaturation = true;
      @Comment({"Debug mode for clients"})
      @Name("Client DebugMode")
      public boolean clientdebug = false;
      @Comment({"Debug temperature readout"})
      @Name("TemperatureReadout")
      public boolean temperatureReadout = false;
      @Comment({"Enables the classic temperature icon"})
      @Name("ClassicHUDTemperature")
      public boolean classicHUDTemperature = false;
      @Comment({"Enables the classic thirst bar"})
      @Name("ClassicHUDThirst")
      public boolean classicHUDThirst = false;
      @Comment({"Enables chiller and heater particles"})
      @Name("HeaterParticles")
      public boolean heaterParticles = true;

      public ConfigClientConfig() {
      }

      public class ConfigClientThermometer {
         @Comment({"Whether thermometers display the correct temperature. Only disable this if you are trying to determine what's lagging."})
         @Name("EnableThermometer")
         public boolean enableThermometer = true;
         @Comment({"Whether thermometers in your inventory will display on your HUD"})
         @Name("HUDThermometer")
         public boolean hudThermometer = true;
         @Comment({"How far left or right the Thermometer HUD is from the default position"})
         @Name("XOffset")
         public int hudThermometerX = 0;
         @Comment({"How far up or down the Thermometer HUD is from the default position"})
         @Name("YOffset")
         public int hudThermometerY = 0;

         public ConfigClientThermometer() {
         }
      }
   }

   public static class ConfigServerConfig {
      @Comment({"Built-in mod compatibility options"})
      @Name("Compatibility")
      public final ConfigServerCompatibility compatibility = new ConfigServerCompatibility();
      @Comment({"Miscellaneous gameplay configurations"})
      @Name("Miscellaneous")
      public final ModConfig.ConfigServerConfig.ConfigMiscellaneous miscellaneous = new ModConfig.ConfigServerConfig.ConfigMiscellaneous();
      @Comment({"Temperature related configurations"})
      @Name("Temperature")
      public final ModConfig.ConfigServerConfig.ConfigTemperature temperature = new ModConfig.ConfigServerConfig.ConfigTemperature();
      @Comment({"Thirst related configurations"})
      @Name("Thirst")
      public final ModConfig.ConfigServerConfig.ConfigThirst thirst = new ModConfig.ConfigServerConfig.ConfigThirst();
      @Comment({"Whether thirst is enabled."})
      @Name("ThirstEnabled")
      public boolean thirstEnabled = true;
      @Comment({"Whether the player is allowed to drink from normal water blocks."})
      @Name("ThirstDrinkBlocks")
      public boolean thirstDrinkBlocks = true;
      @Comment({"Whether the player is allowed to drink from the rain."})
      @Name("ThirstDrinkRain")
      public boolean thirstDrinkRain = true;
      @Comment({"Whether the mod should be dangerous on Peaceful difficulty."})
      @Name("PeacefulDanger")
      public boolean peacefulDanger = false;
      @Comment({"Whether temperature is enabled."})
      @Name("TemperatureEnabled")
      public boolean temperatureEnabled = true;
      @Comment({"Whether temperature tile entities are enabled."})
      @Name("TemperatureTileEntities")
      public boolean temperatureTEEnabled = true;
      @Comment({"Maximum number of doses in a canteen"})
      @Name("CanteenDoses")
      @RangeInt(
         min = 1
      )
      public int canteenDoses = 3;
      @Comment({"Whether heaters and chillers only work indoors"})
      @Name("StrictHeaters")
      public boolean strictHeaters = true;
      @Comment({"Maximum number of doses in an iron canteen"})
      @Name("IronCanteenDoses")
      @RangeInt(
         min = 1
      )
      public int ironCanteenDoses = 8;
      @Comment({"Whether purified water blocks are infinite"})
      @Name("Infinite Purified Water")
      public boolean infinitePurifiedWater = false;
      @Comment({"Spams chat with debug messages, do not enable this unless you are testing!"})
      @Name("DebugMode")
      public boolean debug = false;

      public ConfigServerConfig() {
      }

      public class ConfigMiscellaneous {
         @Comment({"Campfire has a 1/X chance to lose fuel when ticked (default is 2, a 1/2 chance"})
         @Name("CampfireDecayChance")
         @RangeInt(
            min = 1
         )
         public int campfireDecayChance = 2;
         @Comment({"Campfire has a 1/X chance to ignite with a stick (default is 5, a 1/5 chance"})
         @Name("CampfireStickIgniteChance")
         @RangeInt(
            min = 1
         )
         public int campfireStickIgniteChance = 5;
         @Comment({"How many seconds it takes for a campfire spit to cook food"})
         @Name("CampfireSpitDelay")
         @RangeInt(
            min = 1
         )
         public int campfireSpitDelay = 35;
         @Comment({"How many pieces of food can fit on a campfire spit (any existing spits won't change size)"})
         @Name("CampfireSpitSize")
         @RangeInt(
            min = 1,
            max = 10
         )
         public int campfireSpitSize = 3;
         @Comment({"Should cooking food on a campfire spit give experience like a furnace"})
         @Name("CampfireSpitExperience")
         public boolean campfireSpitExperience = true;
         @Comment({"Blacklisted items in the campfire spit (ex. minecraft:beef"})
         @Name("CampfireSpitBlacklist")
         public String[] campfireSpitBlacklist = new String[0];
         @Comment({"Whether the campfire spit blacklist is a whitelist instead"})
         @Name("CampfireSpitBlacklistIsWhitelist")
         public boolean campfireSpitBlacklistIsWhitelist = false;
         @Comment({"Whether Golden Apple Juice gives the golden apple effect"})
         @Name("GoldenAppleJuiceEffect")
         public boolean goldenAppleJuiceEffect = true;
         @Comment({"Whether Ice Blocks drop Ice Chunks"})
         @Name("IceDropsChunks")
         public boolean iceDropsChunks = true;
         @Comment({"Whether Magma Blocks drop Magma Chunks"})
         @Name("MagmaDropsChunks")
         public boolean magmaDropsChunks = true;
         @Comment({"Chance for rain collector to fill (1/n chance, default is 6"})
         @Name("RainCollectorFillChance")
         @RangeInt(
            min = 1
         )
         public int rainCollectorFillChance = 6;
         @Comment({"Whether to register the cooling and heating enchantments"})
         @Name("RegisterEnchantments")
         @RequiresMcRestart
         public boolean registerEnchantments = true;
         @Comment({"Duration of short heat/cold resistance potions, in ticks"})
         @Name("ResistancePotionDurationShort")
         @RequiresMcRestart
         @RangeInt(
            min = 1
         )
         public int resistancePotionDurationShort = 1200;
         @Comment({"Duration of long heat/cold resistance potions, in ticks"})
         @Name("ResistancePotionDurationLong")
         @RequiresMcRestart
         @RangeInt(
            min = 1
         )
         public int resistancePotionDurationLong = 2400;
         @Comment({"How often player temperature and thirst are regularly synced, in ticks"})
         @Name("RoutinePacketDelay")
         @RangeInt(
            min = 0
         )
         public int routinePacketDelay = 30;

         public ConfigMiscellaneous() {
         }
      }

      public class ConfigTemperature {
         @Comment({"Altitude Temperature Multiplier - How strongly altitude affects temperature"})
         @Name("AltitudeMultiplier")
         @RangeInt
         public int altitudeMultiplier = 3;
         @Comment({"Biome Temperature Multiplier - The maximum temperature change in any biome"})
         @Name("BiomeMultiplier")
         @RangeInt
         public int biomeMultiplier = 10;
         @Comment({"Underground Effect - Whether being deep underground reduces some surface temperature effects"})
         @Name("UndergroundEffect")
         public boolean undergroundEffect = true;
         @Comment({"Underground Effect Cutoff - Y Level where surface temperature effects do nothing"})
         @Name("UndergroundEffectCutoff")
         @RangeInt(
            min = 0,
            max = 64
         )
         public int undergroundEffectCutoff = 30;
         @Comment({"Time Temperature Multiplier - How strongly time affects temperature"})
         @Name("TimeMultiplier")
         @RangeInt
         public int timeMultiplier = 3;
         @Comment({"Time Temperature Day - Whether time changes temperature during the day"})
         @Name("TimeTemperatureDay")
         public boolean timeTemperatureDay = true;
         @Comment({"Time Temperature Night - Whether time changes temperature during the night"})
         @Name("TimeTemperatureNight")
         public boolean timeTemperatureNight = true;
         @Comment({"Time Temperature Shade - Effect of shade on time temperature, only applies when time temperature is hot"})
         @Name("TimeTemperatureShade")
         public int timeTemperatureShade = -2;
         @Comment({"Time Biome Temperature Multiplier - How strongly different biomes effect day/night temperature"})
         @Name("TimeBiomeMultiplier")
         @RangeDouble(
            min = 1.0D,
            max = 1000000.0D
         )
         public double timeBiomeMultiplier = 1.25D;
         @Comment({"Snow Temperature Value - Effect of snowfall on temperature"})
         @Name("SnowValue")
         @RangeInt
         public int snowValue = -10;
         @Comment({"Sprinting Temperature Value - Effect of sprinting on temperature"})
         @Name("SprintingValue")
         @RangeInt
         public int sprintingValue = 3;
         @Comment({"Wet Temperature Value - Effect of being wet on temperature"})
         @Name("WetValue")
         @RangeInt
         public int wetValue = -7;
         @Comment({"Temperature Max Speed - Maximum time in ticks for a player temperature change"})
         @Name("TemperatureTickMax")
         @RangeInt(
            min = 20
         )
         public int temperatureTickMax = 400;
         @Comment({"Temperature Min Speed - Minimum time in ticks for a player temperature change"})
         @Name("TemperatureTickMin")
         @RangeInt(
            min = 20
         )
         public int temperatureTickMin = 20;
         @Comment({"TemperatureTickDangerBoost - How much faster in ticks temperature changes happen when escaping dangerous temperatures"})
         @Name("TemperatureTickDangerBoost")
         @RangeInt(
            min = 0
         )
         public int temperatureTickDangerBoost = 60;
         @Comment({"Enchantment Temperature Change - Effect of temperature enchantments"})
         @Name("EnchantmentTemperature")
         @RangeInt
         public int enchantmentTemperature = 1;
         @Comment({"Heater Temperature Change - Strength of heaters / chillers"})
         @Name("HeaterTemperature")
         @RangeInt(
            min = -1000000,
            max = 1000000
         )
         public int heaterTemperature = 10;
         @Comment({"Heater Full Power Range - Distance where a heater / chiller starts to lose strength"})
         @Name("HeaterFullPowerRange")
         @RangeDouble(
            min = 0.0D,
            max = 50.0D
         )
         public double heaterFullPowerRange = 16.0D;
         @Comment({"Heater Max Range - Distance where a heater / chiller has no effect"})
         @Name("HeaterMaxRange")
         @RangeDouble(
            min = 0.0D,
            max = 50.0D
         )
         public double heaterMaxRange = 32.0D;
         @Comment({"Blocks Tiles Separate - Whether blocks and tile entities should have temperature calculated separately, for example, if this is set to true heaters and campfires will both add their heat individually"})
         @Name("BlocksTilesSeparate")
         public boolean blocksTilesSeparate = true;
         @Comment({"Stacking Temperature - Whether multiple blocks in a vicinity should combine their effect"})
         @Name("StackingTemperature")
         public boolean stackingTemperature = true;
         @Comment({"Stacking Temperature Limit - How much more extreme block temperature can be from stacking temperature"})
         @Name("StackingTemperatureLimit")
         @RangeDouble(
            min = 0.0D,
            max = 1000000.0D
         )
         public double stackingTemperatureLimit = 3.0D;
         @Comment({"Temperature Damage Scaling - Extra damage from hyperthermia and hypothermia over time"})
         @Name("TemperatureDamageScaling")
         @RangeDouble(
            min = 0.0D
         )
         public double temperatureDamageScaling = 0.0D;
         @Comment({"TemperatureDamageDuration - Duration in ticks of hypothermia and hyperthermia"})
         @Name("TemperatureDamageDuration")
         @RangeInt(
            min = 0
         )
         public int temperatureDamageDuration = 400;

         public ConfigTemperature() {
         }
      }

      public class ConfigThirst {
         @Comment({"Thirst Exhaustion Limit - How exhausted the player must get before they lose thirst."})
         @Name("ThirstExhaustionLimit")
         @RangeDouble(
            min = 1.0D
         )
         public double thirstExhaustionLimit = 4.0D;
         @Comment({"Thirsty Strength - Strength of the Thirsty Effect"})
         @Name("ThirstyStrength")
         @RangeDouble(
            min = 0.0D
         )
         public double thirstyStrength = 0.025D;
         @Comment({"Thirst Attacking - How exhausting attacking enemies is"})
         @Name("ThirstAttacking")
         @RangeDouble(
            min = 0.0D
         )
         public double thirstAttacking = 0.3D;
         @Comment({"Thirst Break Block - How exhausting breaking blocks is"})
         @Name("ThirstBreakBlock")
         @RangeDouble(
            min = 0.0D
         )
         public double thirstBreakBlock = 0.025D;
         @Comment({"Thirst Sprint Jump - How exhausting jumping while sprinting is"})
         @Name("ThirstSprintJump")
         @RangeDouble(
            min = 0.0D
         )
         public double thirstSprintJump = 0.8D;
         @Comment({"Thirst Jump - How exhausting jumping without sprinting is"})
         @Name("ThirstJump")
         @RangeDouble(
            min = 0.0D
         )
         public double thirstJump = 0.2D;
         @Comment({"Thirst Base Movement - How exhausting any kind of movement is"})
         @Name("ThirstBaseMovement")
         @RangeDouble(
            min = 0.0D
         )
         public double thirstBaseMovement = 0.01D;
         @Comment({"Thirst Swimming Movement - How exhausting swimming movement is"})
         @Name("ThirstSwimmingMovement")
         @RangeDouble(
            min = 0.0D
         )
         public double thirstSwimmingMovement = 0.015D;
         @Comment({"Thirst Sprinting Movement - How exhausting sprinting movement is"})
         @Name("ThirstSprintingMovement")
         @RangeDouble(
            min = 0.0D
         )
         public double thirstSprintingMovement = 0.1D;
         @Comment({"Thirst Walking Movement - How exhausting walking movement is"})
         @Name("ThirstWalkingMovement")
         @RangeDouble(
            min = 0.0D
         )
         public double thirstWalkingMovement = 0.01D;
         @Comment({"Whether the player can get parasites from drinking unclean water"})
         @Name("ThirstParasites")
         public boolean thirstParasites = false;
         @Comment({"The chance of parasites from drinking unclean water"})
         @Name("ThirstParasitesChance")
         @RangeDouble(
            min = 0.0D,
            max = 1.0D
         )
         public double thirstParasitesChance = 0.04D;
         @Comment({"The duration parasites last"})
         @Name("ThirstParasitesDuration")
         @RangeInt(
            min = 1
         )
         public int thirstParasitesDuration = 1200;
         @Comment({"How strongly parasites make a player hungry (0.005 is same speed as hunger, 0 to disable"})
         @Name("ThirstParasitesHunger")
         @RangeDouble(
            min = 0.0D
         )
         public double thirstParasitesHunger = 0.02D;
         @Comment({"The chance a player takes damage from parasites (1 is poison speed, 0 to disable)"})
         @Name("ThirstParasitesDamage")
         @RangeDouble(
            min = 0.0D,
            max = 1.0D
         )
         public double thirstParasitesDamage = 0.2D;
         @Comment({"Thirst Damage Scaling - Extra damage from dehydration over time"})
         @Name("ThirstDamageScaling")
         @RangeDouble(
            min = 0.0D
         )
         public double thirstDamageScaling = 0.0D;

         public ConfigThirst() {
         }
      }
   }

   @EventBusSubscriber(
      modid = "simpledifficulty"
   )
   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
      @SideOnly(Side.CLIENT)
      public static void onConfigChanged(OnConfigChangedEvent event) {
         if (event.getModID().equals("simpledifficulty")) {
            ConfigManager.sync("simpledifficulty", Type.INSTANCE);
            ModConfig.sendLocalClientConfigToAPI();
            if (event.isWorldRunning()) {
               MessageConfigLAN message = new MessageConfigLAN();
               PacketHandler.instance.sendToServer(message);
            } else {
               ModConfig.sendLocalServerConfigToAPI();
            }
         }

      }
   }
}
