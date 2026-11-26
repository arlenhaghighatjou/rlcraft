package hunternif.mc.atlas;

import net.minecraftforge.common.config.ConfigEntry;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;

@Config(
   modid = "antiqueatlas",
   name = "antiqueatlas/antiqueatlas",
   category = ""
)
public class SettingsConfig {
   @Comment({"These settings will affect how the mod behaves in certain situations and the players' overall gameplay, but generally won't affect performance."})
   public static SettingsConfig.Gameplay gameplay = new SettingsConfig.Gameplay();
   @Comment({"These setting will affect the look and feel of the Atlas' interface."})
   public static SettingsConfig.Interface userInterface = new SettingsConfig.Interface();
   @Comment({"These settings affect the algorithms for scanning the world, drawing the map etc. Changing them may improve the game's overall stability and performance at the cost of Atlas' functionality."})
   public static SettingsConfig.Performance performance = new SettingsConfig.Performance();

   public SettingsConfig() {
   }

   public static void register(EventBus bus) {
      bus.register(OnConfigChangedEvent.class, SettingsConfig::onConfigChanged);
   }

   public static void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equals("antiqueatlas")) {
         ConfigManager.sync("antiqueatlas", Type.INSTANCE);
      }

   }

   public static class Gameplay {
      @Comment({"Whether to remember last open browsing position and zoom level for each dimension in every atlas.", "If disabled, all dimensions and all atlases will be \"synchronized\" at the same coordinates and zoom level, and map will \"follow\" player by default."})
      public boolean doSaveBrowsingPos = true;
      @Comment({"Whether to add local marker for the spot where the player died."})
      public boolean autoDeathMarker = true;
      @Comment({"Whether to add local markers for NPC villages."})
      public boolean autoVillageMarkers = true;
      @Comment({"Whether to add local markers for Nether Portals."})
      public boolean autoNetherPortalMarkers = true;
      @Comment({"Player will need to craft atlas item to use atlas."})
      @RequiresMcRestart
      public boolean itemNeeded = true;

      public Gameplay() {
      }
   }

   public static class Interface {
      public boolean doScaleMarkers = false;
      @Comment({"Default zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferrably a power of 2."})
      @RangeDouble(
         min = 0.001953125D,
         max = 16.0D
      )
      public double defaultScale = 0.5D;
      @Comment({"Minimum zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferrably a power of 2. Smaller values may decrease performance!"})
      @RangeDouble(
         min = 0.001953125D,
         max = 16.0D
      )
      public double minScale = 0.03125D;
      @Comment({"Maximum zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferrably a power of 2."})
      @RangeDouble(
         min = 0.001953125D,
         max = 16.0D
      )
      public double maxScale = 4.0D;
      @Comment({"If false (by default), then mousewheel up is zoom in, mousewheel down is zoom out.", "If true, then the direction is reversed."})
      public boolean doReverseWheelZoom = false;

      public Interface() {
      }
   }

   public static class Performance {
      @Comment({"The radius of the area around the player which is scanned by the Atlas at regular intervals.", "Note that this will not force faraway chunks to load, unless force_chunk_loading is enabled.", "Lower value gives better performance."})
      public int scanRadius = 11;
      @Comment({"Force loading of chunks within scan radius even if it exceeds regular chunk loading distance.", "Enabling this may SEVERELY decrease performance!"})
      public boolean forceChunkLoading = false;
      @Comment({"Time in seconds between two scans of the area.", "Higher value gives better performance."})
      public float newScanInterval = 1.0F;
      @Comment({"Whether to rescan chunks in the area that have been previously mapped. This is useful in case of changes in coastline (including small ponds of water and lava), or if land disappears completely (for sky worlds).", "Disable for better performance."})
      public boolean doRescan = true;
      @Comment({"The number of area scans between full rescans.", "Higher value gives better performance."})
      @RangeInt(
         min = 1,
         max = 1000
      )
      public int rescanRate = 4;
      @Comment({"The maximum number of markers a particular atlas can hold."})
      @RangeInt(
         min = 0,
         max = Integer.MAX_VALUE
      )
      public int markerLimit = 10000;
      @Comment({"Whether to perform additional scanning to locate small ponds of water or lava.", "Disable for better performance."})
      public boolean doScanPonds = true;
      @Comment({"Whether to perform additional scanning to locate ravines.", "Disable for better performance."})
      public boolean doScanRavines = true;
      @Comment({"If true, map render time will be output."})
      public boolean debugRender = false;
      @Comment({"Maximum size of image for export. Larger atlases will not be exported to prevent crashes."})
      @RangeInt(
         min = 32
      )
      public int exportSizeLimit = 40000;

      public Performance() {
      }
   }

   public static void register() {
      ConfigManager.register("antiqueatlas", "antiqueatlas/antiqueatlas", "", null,
         ConfigEntry.category("gameplay",
            ConfigEntry.value("doSaveBrowsingPos", boolean.class, () -> SettingsConfig.gameplay.doSaveBrowsingPos, v -> SettingsConfig.gameplay.doSaveBrowsingPos = (Boolean)v).comment("Whether to remember last open browsing position and zoom level for each dimension in every atlas.", "If disabled, all dimensions and all atlases will be \"synchronized\" at the same coordinates and zoom level, and map will \"follow\" player by default."),
            ConfigEntry.value("autoDeathMarker", boolean.class, () -> SettingsConfig.gameplay.autoDeathMarker, v -> SettingsConfig.gameplay.autoDeathMarker = (Boolean)v).comment("Whether to add local marker for the spot where the player died."),
            ConfigEntry.value("autoVillageMarkers", boolean.class, () -> SettingsConfig.gameplay.autoVillageMarkers, v -> SettingsConfig.gameplay.autoVillageMarkers = (Boolean)v).comment("Whether to add local markers for NPC villages."),
            ConfigEntry.value("autoNetherPortalMarkers", boolean.class, () -> SettingsConfig.gameplay.autoNetherPortalMarkers, v -> SettingsConfig.gameplay.autoNetherPortalMarkers = (Boolean)v).comment("Whether to add local markers for Nether Portals."),
            ConfigEntry.value("itemNeeded", boolean.class, () -> SettingsConfig.gameplay.itemNeeded, v -> SettingsConfig.gameplay.itemNeeded = (Boolean)v).comment("Player will need to craft atlas item to use atlas.").requiresMcRestart()).comment("These settings will affect how the mod behaves in certain situations and the players' overall gameplay, but generally won't affect performance."),
         ConfigEntry.category("userInterface",
            ConfigEntry.value("doScaleMarkers", boolean.class, () -> SettingsConfig.userInterface.doScaleMarkers, v -> SettingsConfig.userInterface.doScaleMarkers = (Boolean)v),
            ConfigEntry.value("defaultScale", double.class, () -> SettingsConfig.userInterface.defaultScale, v -> SettingsConfig.userInterface.defaultScale = (Double)v).comment("Default zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferrably a power of 2.").range(0.001953125, 16.0),
            ConfigEntry.value("minScale", double.class, () -> SettingsConfig.userInterface.minScale, v -> SettingsConfig.userInterface.minScale = (Double)v).comment("Minimum zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferrably a power of 2. Smaller values may decrease performance!").range(0.001953125, 16.0),
            ConfigEntry.value("maxScale", double.class, () -> SettingsConfig.userInterface.maxScale, v -> SettingsConfig.userInterface.maxScale = (Double)v).comment("Maximum zoom level. The number corresponds to the size of a block on the map relative to the size of a GUI pixel. Preferrably a power of 2.").range(0.001953125, 16.0),
            ConfigEntry.value("doReverseWheelZoom", boolean.class, () -> SettingsConfig.userInterface.doReverseWheelZoom, v -> SettingsConfig.userInterface.doReverseWheelZoom = (Boolean)v).comment("If false (by default), then mousewheel up is zoom in, mousewheel down is zoom out.", "If true, then the direction is reversed.")).comment("These setting will affect the look and feel of the Atlas' interface."),
         ConfigEntry.category("performance",
            ConfigEntry.value("scanRadius", int.class, () -> SettingsConfig.performance.scanRadius, v -> SettingsConfig.performance.scanRadius = (Integer)v).comment("The radius of the area around the player which is scanned by the Atlas at regular intervals.", "Note that this will not force faraway chunks to load, unless force_chunk_loading is enabled.", "Lower value gives better performance."),
            ConfigEntry.value("forceChunkLoading", boolean.class, () -> SettingsConfig.performance.forceChunkLoading, v -> SettingsConfig.performance.forceChunkLoading = (Boolean)v).comment("Force loading of chunks within scan radius even if it exceeds regular chunk loading distance.", "Enabling this may SEVERELY decrease performance!"),
            ConfigEntry.value("newScanInterval", float.class, () -> SettingsConfig.performance.newScanInterval, v -> SettingsConfig.performance.newScanInterval = (Float)v).comment("Time in seconds between two scans of the area.", "Higher value gives better performance."),
            ConfigEntry.value("doRescan", boolean.class, () -> SettingsConfig.performance.doRescan, v -> SettingsConfig.performance.doRescan = (Boolean)v).comment("Whether to rescan chunks in the area that have been previously mapped. This is useful in case of changes in coastline (including small ponds of water and lava), or if land disappears completely (for sky worlds).", "Disable for better performance."),
            ConfigEntry.value("rescanRate", int.class, () -> SettingsConfig.performance.rescanRate, v -> SettingsConfig.performance.rescanRate = (Integer)v).comment("The number of area scans between full rescans.", "Higher value gives better performance.").range(1, 1000),
            ConfigEntry.value("markerLimit", int.class, () -> SettingsConfig.performance.markerLimit, v -> SettingsConfig.performance.markerLimit = (Integer)v).comment("The maximum number of markers a particular atlas can hold.").range(0, Integer.MAX_VALUE),
            ConfigEntry.value("doScanPonds", boolean.class, () -> SettingsConfig.performance.doScanPonds, v -> SettingsConfig.performance.doScanPonds = (Boolean)v).comment("Whether to perform additional scanning to locate small ponds of water or lava.", "Disable for better performance."),
            ConfigEntry.value("doScanRavines", boolean.class, () -> SettingsConfig.performance.doScanRavines, v -> SettingsConfig.performance.doScanRavines = (Boolean)v).comment("Whether to perform additional scanning to locate ravines.", "Disable for better performance."),
            ConfigEntry.value("debugRender", boolean.class, () -> SettingsConfig.performance.debugRender, v -> SettingsConfig.performance.debugRender = (Boolean)v).comment("If true, map render time will be output."),
            ConfigEntry.value("exportSizeLimit", int.class, () -> SettingsConfig.performance.exportSizeLimit, v -> SettingsConfig.performance.exportSizeLimit = (Integer)v).comment("Maximum size of image for export. Larger atlases will not be exported to prevent crashes.").range(32, Integer.MAX_VALUE)).comment("These settings affect the algorithms for scanning the world, drawing the map etc. Changing them may improve the game's overall stability and performance at the cost of Atlas' functionality."));
   }
}
