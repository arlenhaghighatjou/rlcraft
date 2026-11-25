package com.fantasticsource.fantasticlib.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

@Config(
   modid = "fantasticlib"
)
public class FantasticConfig {
   @Name("Use Entity Render Fixer")
   @LangKey("fantasticlib.config.entityRenderFixer")
   @Comment({"If enabled, runs some code to reset certain openGL settings after each entity renders", "Only use this if you need it to fix a graphical issue"})
   @RequiresMcRestart
   public static boolean entityRenderFixer = false;
   @Name("Use Inventory Desync Fixer")
   @LangKey("fantasticlib.config.inventoryDesyncFixer")
   @Comment({"If enabled, runs some code to synchronize inventory to client when it changes server-side"})
   public static boolean inventoryDesyncFixer = false;
   @Name("Preview Render Mode Overrides")
   @LangKey("fantasticlib.config.previewRenderModeOverrides")
   @Comment({"Overrides for render modes when previewing AW skins on items", "Syntax is...", "Slotting, Channel@Mode, Channel@Mode, etc", "Valid entries for 'Slotting' can be found in config/fantasticlib/reference/slottings.txt", "Valid channels and modes depend on the AW skins on the server (ask server admin)", "Eg. if Tiamat Inventory is installed and a server has valid shoulder skins for it...", "Tiamat Shoulders, ShoulderL@On, ShoulderR@On, ShoulderLControl@On, ShoulderRControl@On"})
   public static String[] previewRenderModeOverrides = new String[0];
   @Name("Negative Attributes")
   @LangKey("fantasticlib.config.negativeAttributes")
   @Comment({"A list of attributes which are bad to have, for render color purposes"})
   public static String[] negativeAttributes = new String[0];
   @Name("Tooltip Scaling")
   @LangKey("fantasticlib.config.tooltipScaling")
   @Comment({"Multiplies tooltip render size by this amount"})
   public static double tooltipScaling = 1.0D;
   @Name("GUI Settings")
   @LangKey("fantasticlib.config.guiSettings")
   public static GUIConfig guiSettings = new GUIConfig();
   @Name("Raytrace Settings")
   @LangKey("fantasticlib.config.raytraceSettings")
   public static RaytraceConfig raytraceSettings = new RaytraceConfig();

   public FantasticConfig() {
   }
}
