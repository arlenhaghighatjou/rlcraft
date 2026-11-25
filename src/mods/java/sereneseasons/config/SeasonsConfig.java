package sereneseasons.config;

import java.io.File;
import sereneseasons.api.config.SeasonsOption;
import sereneseasons.core.SereneSeasons;
import sereneseasons.init.ModConfig;

public class SeasonsConfig extends ConfigHandler {
   public static final String TIME_SETTINGS = "Time Settings";
   public static final String WEATHER_SETTINGS = "Weather Settings";
   public static final String AESTHETIC_SETTINGS = "Aesthetic Settings";
   public static final String DIMENSION_SETTINGS = "Dimension Settings";
   public boolean generateSnowAndIce;
   public boolean changeWeatherFrequency;
   public boolean changeGrassColour;
   public boolean changeFoliageColour;
   public boolean changeBirchColour;
   public String[] whitelistedDimensions;

   public SeasonsConfig(File configFile) {
      super(configFile, "Seasons Settings");
   }

   protected void loadConfiguration() {
      try {
         this.addSyncedValue(SeasonsOption.DAY_DURATION, Integer.valueOf(24000), "Time Settings", "The duration of a Minecraft day in ticks", new Integer[]{Integer.valueOf(20), Integer.MAX_VALUE});
         this.addSyncedValue(SeasonsOption.SUB_SEASON_DURATION, Integer.valueOf(7), "Time Settings", "The duration of a sub season in days", new Integer[]{Integer.valueOf(1), Integer.MAX_VALUE});
         this.addSyncedValue(SeasonsOption.STARTING_SUB_SEASON, Integer.valueOf(5), "Time Settings", "The starting sub season for new worlds.  0 = Random, 1 - 3 = Early/Mid/Late Spring, 4 - 6 = Early/Mid/Late Summer, 7 - 9 = Early/Mid/Late Autumn, 10 - 12 = Early/Mid/Late Winter", new Integer[]{Integer.valueOf(0), Integer.valueOf(12)});
         this.addSyncedValue(SeasonsOption.PROGRESS_SEASON_WHILE_OFFLINE, Boolean.valueOf(true), "Time Settings", "If the season should progress on a server with no players online", new Boolean[0]);
         this.generateSnowAndIce = this.config.getBoolean("Generate Snow and Ice", "Weather Settings", true, "Generate snow and ice during the Winter season");
         this.changeWeatherFrequency = this.config.getBoolean("Change Weather Frequency", "Weather Settings", true, "Change the frequency of rain/snow/storms based on the season");
         this.changeGrassColour = this.config.getBoolean("Change Grass Colour Seasonally", "Aesthetic Settings", true, "Change the grass colour based on the current season");
         this.changeFoliageColour = this.config.getBoolean("Change Foliage Colour Seasonally", "Aesthetic Settings", true, "Change the foliage colour based on the current season");
         this.changeBirchColour = this.config.getBoolean("Change Birch Colour Seasonally", "Aesthetic Settings", true, "Change the birch colour based on the current season");
         this.whitelistedDimensions = this.config.getStringList("Whitelisted Dimensions", "Dimension Settings", new String[]{"0"}, "Seasons will only apply to dimensons listed here");
      } catch (Exception var5) {
         SereneSeasons.logger.error("Serene Seasons has encountered a problem loading seasons.cfg", var5);
      } finally {
         if (this.config.hasChanged()) {
            this.config.save();
         }

      }

   }

   public static boolean isDimensionWhitelisted(int dimension) {
      for(String dimensions : ModConfig.seasons.whitelistedDimensions) {
         if (dimension == Integer.valueOf(dimensions).intValue()) {
            return true;
         }
      }

      return false;
   }
}
