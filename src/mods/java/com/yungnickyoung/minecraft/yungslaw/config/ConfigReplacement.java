package com.yungnickyoung.minecraft.yungslaw.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

public class ConfigReplacement {
   @Name("Generation Distance")
   @Comment({"If Ore Deletion Mode is disabled, this is the minimum distance from Safe Blocks (e.g. air) at which your Hard Block will replace underground blocks.\nIf Ore Deletion Mode is enabled, this is the minimum distance from Safe Blocks at which ores will start to be deleted."})
   @RequiresWorldRestart
   public int genDistance = 3;
   @Name("Safe Blocks")
   @Comment({"Blocks that will not be replaced. Any blocks within the Generation Distance of a Safe Block will also not be replaced."})
   @RequiresWorldRestart
   public String[] safeBlocks = new String[]{"minecraft:air"};
   @Name("Untouchable Blocks")
   @Comment({"Like Safe Blocks, these blocks will not be replaced. Unlike Safe Blocks, no surrounding blocks will be protected from replacement."})
   @RequiresWorldRestart
   public String[] untouchableBlocks = new String[]{"minecraft:bedrock"};
   @Name("Max Altitude")
   @Comment({"Maximum altitude at which block replacement or ore deletion can occur."})
   @RequiresWorldRestart
   public int maxAltitude = 63;
   @Name("Mark Liquids As Safe")
   @Comment({"If true, liquids such as water and lava will be considered Safe Blocks, meaning blocks close to them won't be replaced or deleted.\nRecommended: true"})
   @RequiresWorldRestart
   public boolean enableLiquidSafety = true;
   @Name("Enable Ore Deletion Mode")
   @Comment({"If enabled, only ore blocks will be changed. The Hard Block will not be used - instead ores will be replaced with the biome's filler block (usually stone)."})
   @RequiresWorldRestart
   public boolean enableOreDeletion = false;
   @Name("Replacement Mode Settings")
   @Comment({"Settings for using Replacement Mode (default). These only do anything if Ore Deletion Mode is disabled."})
   @RequiresWorldRestart
   public ConfigReplacementMode replacementMode = new ConfigReplacementMode();
   @Name("Ore Deletion Mode Settings")
   @Comment({"Settings for using Ore Deletion Mode. These only do anything if Ore Deletion Mode is enabled."})
   @RequiresWorldRestart
   public ConfigOreMode oreMode = new ConfigOreMode();

   public ConfigReplacement() {
   }
}
