package com.oblivioussp.spartanhudbaubles.util;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.oblivioussp.spartanhudbaubles.client.gui.AlignmentHelper;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.util.Arrays;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;

public class Config {
   private static final String VERSION = Integer.toString(1);
   private static Configuration config;
   private static final String[] VALID_ALIGNMENT_VALUES = AlignmentHelper.Alignment.getValidConfigValues();
   private static final String[] VALID_SKIN_VALUES = SkinType.getValidConfigValues();
   public static boolean disableHUD = false;
   public static SkinType skinType;
   public static boolean showBaublesIcon = true;
   public static int tooltipBackgroundColour = -267386864;
   public static int tooltipFrameTopColour = 1347420415;
   public static int tooltipFrameBottomColour = 1344798847;
   public static int minimalTopColour = 2105376;
   public static int minimalBottomColour = -1071636448;
   public static AlignmentHelper.Alignment hudAlignment;
   public static int hudOffsetX;
   public static int hudOffsetY;
   public static boolean hudRotateVertical = false;

   public Config() {
   }

   public static void init(VFile2 configPath) {
      VFile2 configFile = new VFile2(configPath, "spartanhudbaubles".toLowerCase() + ".cfg");
      if (config == null) {
         config = new Configuration(configFile, VERSION);
         load();
      }

   }

   public void register(EventBus bus) {
      bus.register(OnConfigChangedEvent.class, this::onConfigChanged);
   }

   public void onConfigChanged(OnConfigChangedEvent ev) {
      if (ev.getModID().equalsIgnoreCase("spartanhudbaubles")) {
         load();
      }

   }

   private static void load() {
      Configuration var10002 = config;
      disableHUD = config.getBoolean("disable_hud", "client", false, "Set to true to disable the Baubles HUD Element (which essentially disables the entire mod)", "config.spartanhudbaubles:client.disable_hud");
      var10002 = config;
      showBaublesIcon = config.getBoolean("show_baubles_icon", "client", true, "Enables display of the Baubles icon next to the Baubles HUD Element. This won't show if the skin type is set to 'None'", "config.spartanhudbaubles:client.show_baubles_icon");
      var10002 = config;
      Config.ColourValue tooltipBGColour = Config.ColourValue.fromConfig(config, "tooltip_background_colour", "client", -267386864, "Background colour for the tooltip skin variant");
      tooltipBackgroundColour = tooltipBGColour.toInteger();
      var10002 = config;
      Config.ColourValue tooltipFrTopColour = Config.ColourValue.fromConfig(config, "tooltip_frame_top_colour", "client", 1347420415, "Frame top colour for the tooltip skin variant");
      tooltipFrameTopColour = tooltipFrTopColour.toInteger();
      var10002 = config;
      Config.ColourValue tooltipFrBtmColour = Config.ColourValue.fromConfig(config, "tooltip_frame_bottom_colour", "client", 1344798847, "Frame bottom colour for the tooltip skin variant");
      tooltipFrameBottomColour = tooltipFrBtmColour.toInteger();
      var10002 = config;
      Config.ColourValue minTopColour = Config.ColourValue.fromConfig(config, "minimal_top_colour", "client", 2105376, "Top colour for the minimal skin variant");
      minimalTopColour = minTopColour.toInteger();
      var10002 = config;
      Config.ColourValue minBtmColour = Config.ColourValue.fromConfig(config, "minimal_bottom_colour", "client", -1071636448, "Bottom colour for the minimal skin variant");
      minimalBottomColour = minBtmColour.toInteger();
      var10002 = config;
      String skinTypeString = config.getString("skin_type", "client", SkinType.HOTBAR_STYLE.toString(), "Skin variant to display behind the Baubles in the HUD Element", VALID_SKIN_VALUES, "config.spartanhudbaubles:client.skin_type");
      skinType = SkinType.valueOf(skinTypeString);
      var10002 = config;
      String hudAlignmentString = config.getString("alignment", "client", AlignmentHelper.Alignment.BOTTOM_RIGHT.toString(), "Alignment of the Baubles HUD Element", VALID_ALIGNMENT_VALUES, "config.spartanhudbaubles:client.alignment");
      hudAlignment = AlignmentHelper.Alignment.valueOf(hudAlignmentString);
      var10002 = config;
      hudOffsetX = config.getInt("offset_x", "client", 0, -1000, 1000, "X offset of the Baubles HUD Element. Can be used to adjust the X position", "config.spartanhudbaubles:client.offset_x");
      var10002 = config;
      hudOffsetY = config.getInt("offset_y", "client", 0, -1000, 1000, "Y offset of the Baubles HUD Element. Can be used to adjust the Y position", "config.spartanhudbaubles:client.offset_y");
      var10002 = config;
      hudRotateVertical = config.getBoolean("rotate_vertical", "client", false, "Set to true to rotate the Baubles HUD Element vertically or false for horizontally", "config.spartanhudbaubles:client.rotate_vertical");
      if (config.hasChanged()) {
         config.save();
      }

   }

   public static Configuration getConfig() {
      return config;
   }

   public static class ColourValue {
      private byte r;
      private byte g;
      private byte b;
      private byte a;

      public static Config.ColourValue fromConfig(Configuration config, String name, String category, int defaultValue, String comment) {
         ConfigCategory cfgCategory = config.getCategory(category + "." + name);
         cfgCategory.setComment(comment);
         cfgCategory.setLanguageKey("config.spartanhudbaubles:client." + name);
         String[] order = new String[]{"red", "green", "blue", "alpha"};
         cfgCategory.setPropertyOrder(Arrays.asList(order));
         byte alpha = (byte)config.getInt("alpha", cfgCategory.getQualifiedName(), defaultValue >> 24 & 255, 0, 255, "");
         byte red = (byte)config.getInt("red", cfgCategory.getQualifiedName(), defaultValue >> 16 & 255, 0, 255, "");
         byte green = (byte)config.getInt("green", cfgCategory.getQualifiedName(), defaultValue >> 8 & 255, 0, 255, "");
         byte blue = (byte)config.getInt("blue", cfgCategory.getQualifiedName(), defaultValue & 255, 0, 255, "");
         return new Config.ColourValue(red, green, blue, alpha);
      }

      public ColourValue(byte r, byte g, byte b, byte a) {
         this.r = r;
         this.g = g;
         this.b = b;
         this.a = a;
      }

      public int toInteger() {
         int value = ((this.a & 255) << 24) + ((this.r & 255) << 16) + ((this.g & 255) << 8) + (this.b & 255) & -1;
         return value;
      }
   }
}
