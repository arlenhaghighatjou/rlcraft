package kenkron.antiqueatlasoverlay;

import net.minecraftforge.common.config.ConfigEntry;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;

@Config(
   modid = "antiqueatlasoverlay",
   name = "antiqueatlas/atlas_overlay",
   category = ""
)
public class AAOConfig {
   @Comment({"These settings change the location and size."})
   public static AAOConfig.Position position = new AAOConfig.Position();
   @Comment({"These settings change what the map shows, or how it is shown."})
   public static AAOConfig.Appearance appearance = new AAOConfig.Appearance();

   public AAOConfig() {
   }

   public static void register(EventBus bus) {
      bus.register(OnConfigChangedEvent.class, AAOConfig::onConfigChanged);
   }

   public static void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equals("antiqueatlasoverlay")) {
         ConfigManager.sync("antiqueatlasoverlay", Type.INSTANCE);
      }

   }

   public static class Appearance {
      @Comment({"The size (in GUI pixels) of a map's tile.", "Note that this will change with Minecraft's GUI scale configuration.", "When using a small gui scale, the map may look better with a TILE_SIZE of 16 or more."})
      @RangeInt(
         min = 1
      )
      public int tileSize = 8;
      @Comment({"The size (in GUI pixels) of a marker on the map.", "Note that this will change with Minecraft's GUI scale configuration."})
      @RangeInt(
         min = 0
      )
      public int markerSize = 16;
      @Comment({"The width (in GUI pixels) of the player's icon."})
      @RangeInt(
         min = 0
      )
      public int playerIconWidth = 7;
      @Comment({"The height (in GUI pixels) of the player's icon."})
      @RangeInt(
         min = 0
      )
      public int playerIconHeight = 8;
      @Comment({"The width of the map border on the left and right sides of the minimap tiles.", "Represented as a fraction of the image width.", "Below a certain threshold, this border will be overtaken by the map border graphic."})
      @RangeDouble(
         min = 0.0D,
         max = 0.5D
      )
      public float borderX = 0.0F;
      @Comment({"The width of the map border on the top and bottom sides of the minimap tiles.", "Represented as a fraction of the image width.", "Below a certain threshold, this border will be overtaken by the map border graphic.\n"})
      @RangeDouble(
         min = 0.0D,
         max = 0.5D
      )
      public float borderY = 0.0F;
      @Comment({"If true, the minimap will show the map of an atlas only while it is held.", "If false, the minimap will show the map of the first atlas in the hotbar."})
      public boolean requiresHold = true;
      @Comment({"Set true to enable minimap"})
      public boolean enabled = false;

      public Appearance() {
      }
   }

   public static class Position {
      @Comment({"If true, the map position's x axis will align 0 to the right", "of the screen, increasing towards the left."})
      public boolean alignRight = true;
      @Comment({"If true, the map position's y axis will align 0 to the bottom", "of the screen, increasing towards the top."})
      public boolean alignBottom = false;
      @Comment({"Map's minimum position along the x axis in GUI pixels.", "Note that this will change with Minecraft's GUI scale configuration."})
      @RangeInt
      public int xPosition = 2;
      @Comment({"Map's minimum position along the y axis in GUI pixels.", "Note that this will change with Minecraft's GUI scale configuration."})
      @RangeInt
      public int yPosition = 2;
      @Comment({"Map's width in GUI pixels.", "Note that this will change with Minecraft's GUI scale configuration."})
      @RangeInt(
         min = 0
      )
      public int width = 155;
      @Comment({"Map's height in GUI pixels.", "Note that this will change with Minecraft's GUI scale configuration."})
      @RangeInt(
         min = 0
      )
      public int height = 109;

      public Position() {
      }
   }

   public static void register() {
      ConfigManager.register("antiqueatlasoverlay", "antiqueatlas/atlas_overlay", "", null,
         ConfigEntry.category("position",
            ConfigEntry.value("alignRight", boolean.class, () -> AAOConfig.position.alignRight, v -> AAOConfig.position.alignRight = (Boolean)v).comment("If true, the map position's x axis will align 0 to the right", "of the screen, increasing towards the left."),
            ConfigEntry.value("alignBottom", boolean.class, () -> AAOConfig.position.alignBottom, v -> AAOConfig.position.alignBottom = (Boolean)v).comment("If true, the map position's y axis will align 0 to the bottom", "of the screen, increasing towards the top."),
            ConfigEntry.value("xPosition", int.class, () -> AAOConfig.position.xPosition, v -> AAOConfig.position.xPosition = (Integer)v).comment("Map's minimum position along the x axis in GUI pixels.", "Note that this will change with Minecraft's GUI scale configuration.").range(Integer.MIN_VALUE, Integer.MAX_VALUE),
            ConfigEntry.value("yPosition", int.class, () -> AAOConfig.position.yPosition, v -> AAOConfig.position.yPosition = (Integer)v).comment("Map's minimum position along the y axis in GUI pixels.", "Note that this will change with Minecraft's GUI scale configuration.").range(Integer.MIN_VALUE, Integer.MAX_VALUE),
            ConfigEntry.value("width", int.class, () -> AAOConfig.position.width, v -> AAOConfig.position.width = (Integer)v).comment("Map's width in GUI pixels.", "Note that this will change with Minecraft's GUI scale configuration.").range(0, Integer.MAX_VALUE),
            ConfigEntry.value("height", int.class, () -> AAOConfig.position.height, v -> AAOConfig.position.height = (Integer)v).comment("Map's height in GUI pixels.", "Note that this will change with Minecraft's GUI scale configuration.").range(0, Integer.MAX_VALUE)).comment("These settings change the location and size."),
         ConfigEntry.category("appearance",
            ConfigEntry.value("tileSize", int.class, () -> AAOConfig.appearance.tileSize, v -> AAOConfig.appearance.tileSize = (Integer)v).comment("The size (in GUI pixels) of a map's tile.", "Note that this will change with Minecraft's GUI scale configuration.", "When using a small gui scale, the map may look better with a TILE_SIZE of 16 or more.").range(1, Integer.MAX_VALUE),
            ConfigEntry.value("markerSize", int.class, () -> AAOConfig.appearance.markerSize, v -> AAOConfig.appearance.markerSize = (Integer)v).comment("The size (in GUI pixels) of a marker on the map.", "Note that this will change with Minecraft's GUI scale configuration.").range(0, Integer.MAX_VALUE),
            ConfigEntry.value("playerIconWidth", int.class, () -> AAOConfig.appearance.playerIconWidth, v -> AAOConfig.appearance.playerIconWidth = (Integer)v).comment("The width (in GUI pixels) of the player's icon.").range(0, Integer.MAX_VALUE),
            ConfigEntry.value("playerIconHeight", int.class, () -> AAOConfig.appearance.playerIconHeight, v -> AAOConfig.appearance.playerIconHeight = (Integer)v).comment("The height (in GUI pixels) of the player's icon.").range(0, Integer.MAX_VALUE),
            ConfigEntry.value("borderX", float.class, () -> AAOConfig.appearance.borderX, v -> AAOConfig.appearance.borderX = (Float)v).comment("The width of the map border on the left and right sides of the minimap tiles.", "Represented as a fraction of the image width.", "Below a certain threshold, this border will be overtaken by the map border graphic.").range(0.0, 0.5),
            ConfigEntry.value("borderY", float.class, () -> AAOConfig.appearance.borderY, v -> AAOConfig.appearance.borderY = (Float)v).comment("The width of the map border on the top and bottom sides of the minimap tiles.", "Represented as a fraction of the image width.", "Below a certain threshold, this border will be overtaken by the map border graphic.\n").range(0.0, 0.5),
            ConfigEntry.value("requiresHold", boolean.class, () -> AAOConfig.appearance.requiresHold, v -> AAOConfig.appearance.requiresHold = (Boolean)v).comment("If true, the minimap will show the map of an atlas only while it is held.", "If false, the minimap will show the map of the first atlas in the hotbar."),
            ConfigEntry.value("enabled", boolean.class, () -> AAOConfig.appearance.enabled, v -> AAOConfig.appearance.enabled = (Boolean)v).comment("Set true to enable minimap")).comment("These settings change what the map shows, or how it is shown."));
   }
}
