package hunternif.mc.atlas;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(
   modid = "antiqueatlas",
   name = "antiqueatlas/antiqueatlas",
   category = ""
)
@EventBusSubscriber(
   modid = "antiqueatlas"
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

   @SubscribeEvent
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
}
