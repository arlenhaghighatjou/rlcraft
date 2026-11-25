package com.alcatrazescapee.notreepunching;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

@Config(
   modid = "notreepunching",
   category = ""
)
public final class ModConfig {
   public static final ModConfig.GeneralConfig GENERAL = new ModConfig.GeneralConfig();
   public static final ModConfig.BalanceConfig BALANCE = new ModConfig.BalanceConfig();
   public static final ModConfig.ToolsConfig TOOLS = new ModConfig.ToolsConfig();
   public static final ModConfig.CompatConfig COMPAT = new ModConfig.CompatConfig();

   public ModConfig() {
   }

   public static class BalanceConfig {
      @Comment({"Chance for a flint knapping to occur. Set to zero to disable flint knapping."})
      @RangeDouble(
         min = 0.0D,
         max = 1.0D
      )
      public double flintKnappingChance;
      @Comment({"Chance for a successful flint knapping"})
      @RangeDouble(
         min = 0.0D,
         max = 1.0D
      )
      public double flintKnappingSuccessChance;
      @Comment({"Chance for a log chopping to occur"})
      @RangeDouble(
         min = 0.0D,
         max = 1.0D
      )
      public double logChoppingChance;
      @Comment({"Fire pit cook time (in ticks). Furnace is 200 ticks"})
      @RangeInt(
         min = 20
      )
      public int firePitCookTime;
      @Comment({"Fire pit fuel efficiency multiplier as compared to the furnace"})
      @RangeInt(
         min = 1
      )
      public int firePitFuelMultiplier;
      @Comment({"The maximum burn amount required for a fire pit fuel. (in ticks, coal = 1600)"})
      @RangeInt(
         min = 20
      )
      public int firePitFuelMaxAmount;
      @Comment({"The chance that a fire starter will start a fire"})
      @RangeDouble(
         min = 0.0D,
         max = 1.0D
      )
      public double fireStarterFireStartChance;
      @Comment({"The chance for leaves to drop sticks when broken"})
      @RangeDouble(
         min = 0.0D,
         max = 1.0D
      )
      public double leavesStickDropChance;
      @Comment({"The chance for tall grass to drop plant fiber when broken with a knife"})
      @RangeDouble(
         min = 0.0D,
         max = 1.0D
      )
      public double tallGrassDropPlantFiberChance;
      @Comment({"Can the player pick up rocks by right clicking? (vs. having to mine them)"})
      public boolean canPickUpRocks;

      private BalanceConfig() {
         this.flintKnappingChance = 0.6D;
         this.flintKnappingSuccessChance = 0.7D;
         this.logChoppingChance = 0.6D;
         this.firePitCookTime = 400;
         this.firePitFuelMultiplier = 5;
         this.firePitFuelMaxAmount = 800;
         this.fireStarterFireStartChance = 0.5D;
         this.leavesStickDropChance = 0.2D;
         this.tallGrassDropPlantFiberChance = 0.4D;
         this.canPickUpRocks = true;
      }
   }

   public static class CompatConfig {
      @Comment({"Enable compatibility features from Rustic (slate cobblestone + loose rock)"})
      @RequiresMcRestart
      public boolean enableRusticCompat;
      @Comment({"Enable compatibility features from Quark (marble + limestone cobblestone and loose rock)"})
      @RequiresMcRestart
      public boolean enableQuarkCompat;
      @Comment({"Enable compatability features from Chisel (marble + limestone + basalt loose rocks)"})
      public boolean enableChiselCompat;

      private CompatConfig() {
         this.enableRusticCompat = true;
         this.enableQuarkCompat = true;
         this.enableChiselCompat = true;
      }
   }

   public static class GeneralConfig {
      @Comment({"Blocks that are always breakable. Use the format modid:registryname or modid:registryname:metadata", "Note that this will not make blocks drop if they wouldn't normally drop when broken with fists."})
      public String[] alwaysBreakable;
      @Comment({"Fluids that are able to be picked up by the ceramic bucket"})
      public String[] ceramicBucketValidFluids;
      @RequiresMcRestart
      @Comment({"If true, NTP will add its own log->plank and plank->stick recipes and remove old versions", "If false, NTP will still add its own recipes, but it will not remove any other vanilla or modded recipes"})
      public boolean replaceLogRecipes;
      @RequiresMcRestart
      @Comment({"If true, NTP will remove recipes for vanilla wooden + stone tools. It will also attempt to hide these items from the creative inventory.", "If false, NTP will not add or remove any vanilla recipes"})
      public boolean replaceVanillaRecipes;
      @RequiresMcRestart
      @Comment({"If true, this will allow NTP to search aggressively for recipes and try and replace log -> wood recipes. (Makes NTPs wood chopping / saw recipes more automatic.) For pack makers: this WILL run after craft tweaker, and it WILL override any recipes craft tweaker adds."})
      public boolean enableAdvancedRecipeReplacement;
      @Comment({"If false, this will disable all changes to breaking speed + block drops."})
      public boolean enableBreakingChanges;
      @Comment({"If false, this will disable all changes to Stone / Cobblestone drops."})
      public boolean enableStoneDropChanges;
      @Comment({"If false, this will disable stone variants (andesite, diorite, and granite) from appearing, except if the respective stone is found during world gen. (It will only disable random occurrences, not ones based on the underground material.)"})
      public boolean enableRandomStoneWorldGenVariants;
      @Comment({"If false, this will disable NTP world gen (surface rocks)."})
      public boolean looseRocksGeneration;
      @Comment({"If false, this will disable the ability to get torches from fire pits."})
      public boolean allowTorchesFromFirePits;
      @Comment({"Frequency of loose rocks in the world"})
      @RangeInt(
         min = 1,
         max = 1000
      )
      public int looseRocksFrequency;

      private GeneralConfig() {
         this.alwaysBreakable = new String[]{"minecraft:leaves", "minecraft:gravel", "minecraft:sand", "minecraft:dirt", "minecraft:grass"};
         this.ceramicBucketValidFluids = new String[]{"water"};
         this.replaceLogRecipes = true;
         this.replaceVanillaRecipes = true;
         this.enableAdvancedRecipeReplacement = true;
         this.enableBreakingChanges = true;
         this.enableStoneDropChanges = true;
         this.enableRandomStoneWorldGenVariants = false;
         this.looseRocksGeneration = true;
         this.allowTorchesFromFirePits = true;
         this.looseRocksFrequency = 10;
      }
   }

   public static class ToolsConfig {
      @Comment({"Mining level of flint tools. 0 = Wood, 1 = Stone, 2 = Iron, 3 = Diamond"})
      @RangeInt(
         min = 0,
         max = 4
      )
      @RequiresMcRestart
      public int miningLevelFlint;
      @Comment({"Mining level of tin tools. 0 = Wood, 1 = Stone, 2 = Iron, 3 = Diamond"})
      @RangeInt(
         min = 0,
         max = 4
      )
      @RequiresMcRestart
      public int miningLevelTin;
      @Comment({"Mining level of copper tools. 0 = Wood, 1 = Stone, 2 = Iron, 3 = Diamond"})
      @RangeInt(
         min = 0,
         max = 4
      )
      @RequiresMcRestart
      public int miningLevelCopper;
      @Comment({"Mining level of bronze tools. 0 = Wood, 1 = Stone, 2 = Iron, 3 = Diamond"})
      @RangeInt(
         min = 0,
         max = 4
      )
      @RequiresMcRestart
      public int miningLevelBronze;
      @Comment({"Mining level of steel tools. 0 = Wood, 1 = Stone, 2 = Iron, 3 = Diamond"})
      @RangeInt(
         min = 0,
         max = 4
      )
      @RequiresMcRestart
      public int miningLevelSteel;
      @Comment({"Enable copper versions of NTP tools (knife, mattock, saw)"})
      @RequiresMcRestart
      public boolean enableCopperTools;
      @Comment({"Enable tin versions of NTP tools (knife, mattock, saw)"})
      @RequiresMcRestart
      public boolean enableTinTools;
      @Comment({"Enable bronze versions of NTP tools (knife, mattock, saw)"})
      @RequiresMcRestart
      public boolean enableBronzeTools;
      @Comment({"Enable steel versions of NTP tools (knife, mattock, saw)"})
      @RequiresMcRestart
      public boolean enableSteelTools;

      private ToolsConfig() {
         this.miningLevelFlint = 1;
         this.miningLevelTin = 0;
         this.miningLevelCopper = 1;
         this.miningLevelBronze = 2;
         this.miningLevelSteel = 3;
         this.enableCopperTools = true;
         this.enableTinTools = true;
         this.enableBronzeTools = true;
         this.enableSteelTools = true;
      }
   }
}
