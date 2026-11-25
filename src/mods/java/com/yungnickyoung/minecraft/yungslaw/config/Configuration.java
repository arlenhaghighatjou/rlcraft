package com.yungnickyoung.minecraft.yungslaw.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

@Config(
   modid = "yungslaw",
   name = "YungsLaw-1_12_2"
)
public class Configuration {
   @Name("Whitelisted Dimension IDs")
   @Comment({"List of ID's of dimensions that will use YUNG's Law. Ignored if Global Whitelisting is enabled."})
   @RequiresWorldRestart
   public static int[] whitelistedDimensionIDs = new int[]{0};
   @Name("Enable Global Whitelist")
   @Comment({"Automatically enables YUNG's Law in every possible dimension."})
   @RequiresWorldRestart
   public static boolean enableGlobalWhitelist = false;
   @Name("Replacement Settings")
   @Comment({"Settings for underground block replacement."})
   @RequiresWorldRestart
   public static ConfigReplacement replacementSettings = new ConfigReplacement();
   @Name("Mod Compatibility Settings")
   @Comment({"Support for modded ores when using Ore Deletion Mode."})
   @RequiresWorldRestart
   public static ConfigModCompat modCompat = new ConfigModCompat();

   public Configuration() {
   }
}
