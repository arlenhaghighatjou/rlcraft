package com.alcatrazescapee.notreepunching;

import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.ConfigEntry;
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

   public static void register() {
      ConfigManager.register("notreepunching", "", "", null,
         ConfigEntry.category("GENERAL",
            ConfigEntry.value("alwaysBreakable", String[].class, () -> ModConfig.GENERAL.alwaysBreakable, v -> ModConfig.GENERAL.alwaysBreakable = (String[])v).comment("Blocks that are always breakable. Use the format modid:registryname or modid:registryname:metadata", "Note that this will not make blocks drop if they wouldn't normally drop when broken with fists."),
            ConfigEntry.value("ceramicBucketValidFluids", String[].class, () -> ModConfig.GENERAL.ceramicBucketValidFluids, v -> ModConfig.GENERAL.ceramicBucketValidFluids = (String[])v).comment("Fluids that are able to be picked up by the ceramic bucket"),
            ConfigEntry.value("replaceLogRecipes", boolean.class, () -> ModConfig.GENERAL.replaceLogRecipes, v -> ModConfig.GENERAL.replaceLogRecipes = (Boolean)v).comment("If true, NTP will add its own log->plank and plank->stick recipes and remove old versions", "If false, NTP will still add its own recipes, but it will not remove any other vanilla or modded recipes").requiresMcRestart(),
            ConfigEntry.value("replaceVanillaRecipes", boolean.class, () -> ModConfig.GENERAL.replaceVanillaRecipes, v -> ModConfig.GENERAL.replaceVanillaRecipes = (Boolean)v).comment("If true, NTP will remove recipes for vanilla wooden + stone tools. It will also attempt to hide these items from the creative inventory.", "If false, NTP will not add or remove any vanilla recipes").requiresMcRestart(),
            ConfigEntry.value("enableAdvancedRecipeReplacement", boolean.class, () -> ModConfig.GENERAL.enableAdvancedRecipeReplacement, v -> ModConfig.GENERAL.enableAdvancedRecipeReplacement = (Boolean)v).comment("If true, this will allow NTP to search aggressively for recipes and try and replace log -> wood recipes. (Makes NTPs wood chopping / saw recipes more automatic.) For pack makers: this WILL run after craft tweaker, and it WILL override any recipes craft tweaker adds.").requiresMcRestart(),
            ConfigEntry.value("enableBreakingChanges", boolean.class, () -> ModConfig.GENERAL.enableBreakingChanges, v -> ModConfig.GENERAL.enableBreakingChanges = (Boolean)v).comment("If false, this will disable all changes to breaking speed + block drops."),
            ConfigEntry.value("enableStoneDropChanges", boolean.class, () -> ModConfig.GENERAL.enableStoneDropChanges, v -> ModConfig.GENERAL.enableStoneDropChanges = (Boolean)v).comment("If false, this will disable all changes to Stone / Cobblestone drops."),
            ConfigEntry.value("enableRandomStoneWorldGenVariants", boolean.class, () -> ModConfig.GENERAL.enableRandomStoneWorldGenVariants, v -> ModConfig.GENERAL.enableRandomStoneWorldGenVariants = (Boolean)v).comment("If false, this will disable stone variants (andesite, diorite, and granite) from appearing, except if the respective stone is found during world gen. (It will only disable random occurrences, not ones based on the underground material.)"),
            ConfigEntry.value("looseRocksGeneration", boolean.class, () -> ModConfig.GENERAL.looseRocksGeneration, v -> ModConfig.GENERAL.looseRocksGeneration = (Boolean)v).comment("If false, this will disable NTP world gen (surface rocks)."),
            ConfigEntry.value("allowTorchesFromFirePits", boolean.class, () -> ModConfig.GENERAL.allowTorchesFromFirePits, v -> ModConfig.GENERAL.allowTorchesFromFirePits = (Boolean)v).comment("If false, this will disable the ability to get torches from fire pits."),
            ConfigEntry.value("looseRocksFrequency", int.class, () -> ModConfig.GENERAL.looseRocksFrequency, v -> ModConfig.GENERAL.looseRocksFrequency = (Integer)v).comment("Frequency of loose rocks in the world").range(1, 1000)),
         ConfigEntry.category("BALANCE",
            ConfigEntry.value("flintKnappingChance", double.class, () -> ModConfig.BALANCE.flintKnappingChance, v -> ModConfig.BALANCE.flintKnappingChance = (Double)v).comment("Chance for a flint knapping to occur. Set to zero to disable flint knapping.").range(0.0, 1.0),
            ConfigEntry.value("flintKnappingSuccessChance", double.class, () -> ModConfig.BALANCE.flintKnappingSuccessChance, v -> ModConfig.BALANCE.flintKnappingSuccessChance = (Double)v).comment("Chance for a successful flint knapping").range(0.0, 1.0),
            ConfigEntry.value("logChoppingChance", double.class, () -> ModConfig.BALANCE.logChoppingChance, v -> ModConfig.BALANCE.logChoppingChance = (Double)v).comment("Chance for a log chopping to occur").range(0.0, 1.0),
            ConfigEntry.value("firePitCookTime", int.class, () -> ModConfig.BALANCE.firePitCookTime, v -> ModConfig.BALANCE.firePitCookTime = (Integer)v).comment("Fire pit cook time (in ticks). Furnace is 200 ticks").range(20, Integer.MAX_VALUE),
            ConfigEntry.value("firePitFuelMultiplier", int.class, () -> ModConfig.BALANCE.firePitFuelMultiplier, v -> ModConfig.BALANCE.firePitFuelMultiplier = (Integer)v).comment("Fire pit fuel efficiency multiplier as compared to the furnace").range(1, Integer.MAX_VALUE),
            ConfigEntry.value("firePitFuelMaxAmount", int.class, () -> ModConfig.BALANCE.firePitFuelMaxAmount, v -> ModConfig.BALANCE.firePitFuelMaxAmount = (Integer)v).comment("The maximum burn amount required for a fire pit fuel. (in ticks, coal = 1600)").range(20, Integer.MAX_VALUE),
            ConfigEntry.value("fireStarterFireStartChance", double.class, () -> ModConfig.BALANCE.fireStarterFireStartChance, v -> ModConfig.BALANCE.fireStarterFireStartChance = (Double)v).comment("The chance that a fire starter will start a fire").range(0.0, 1.0),
            ConfigEntry.value("leavesStickDropChance", double.class, () -> ModConfig.BALANCE.leavesStickDropChance, v -> ModConfig.BALANCE.leavesStickDropChance = (Double)v).comment("The chance for leaves to drop sticks when broken").range(0.0, 1.0),
            ConfigEntry.value("tallGrassDropPlantFiberChance", double.class, () -> ModConfig.BALANCE.tallGrassDropPlantFiberChance, v -> ModConfig.BALANCE.tallGrassDropPlantFiberChance = (Double)v).comment("The chance for tall grass to drop plant fiber when broken with a knife").range(0.0, 1.0),
            ConfigEntry.value("canPickUpRocks", boolean.class, () -> ModConfig.BALANCE.canPickUpRocks, v -> ModConfig.BALANCE.canPickUpRocks = (Boolean)v).comment("Can the player pick up rocks by right clicking? (vs. having to mine them)")),
         ConfigEntry.category("TOOLS",
            ConfigEntry.value("miningLevelFlint", int.class, () -> ModConfig.TOOLS.miningLevelFlint, v -> ModConfig.TOOLS.miningLevelFlint = (Integer)v).comment("Mining level of flint tools. 0 = Wood, 1 = Stone, 2 = Iron, 3 = Diamond").range(0, 4).requiresMcRestart(),
            ConfigEntry.value("miningLevelTin", int.class, () -> ModConfig.TOOLS.miningLevelTin, v -> ModConfig.TOOLS.miningLevelTin = (Integer)v).comment("Mining level of tin tools. 0 = Wood, 1 = Stone, 2 = Iron, 3 = Diamond").range(0, 4).requiresMcRestart(),
            ConfigEntry.value("miningLevelCopper", int.class, () -> ModConfig.TOOLS.miningLevelCopper, v -> ModConfig.TOOLS.miningLevelCopper = (Integer)v).comment("Mining level of copper tools. 0 = Wood, 1 = Stone, 2 = Iron, 3 = Diamond").range(0, 4).requiresMcRestart(),
            ConfigEntry.value("miningLevelBronze", int.class, () -> ModConfig.TOOLS.miningLevelBronze, v -> ModConfig.TOOLS.miningLevelBronze = (Integer)v).comment("Mining level of bronze tools. 0 = Wood, 1 = Stone, 2 = Iron, 3 = Diamond").range(0, 4).requiresMcRestart(),
            ConfigEntry.value("miningLevelSteel", int.class, () -> ModConfig.TOOLS.miningLevelSteel, v -> ModConfig.TOOLS.miningLevelSteel = (Integer)v).comment("Mining level of steel tools. 0 = Wood, 1 = Stone, 2 = Iron, 3 = Diamond").range(0, 4).requiresMcRestart(),
            ConfigEntry.value("enableCopperTools", boolean.class, () -> ModConfig.TOOLS.enableCopperTools, v -> ModConfig.TOOLS.enableCopperTools = (Boolean)v).comment("Enable copper versions of NTP tools (knife, mattock, saw)").requiresMcRestart(),
            ConfigEntry.value("enableTinTools", boolean.class, () -> ModConfig.TOOLS.enableTinTools, v -> ModConfig.TOOLS.enableTinTools = (Boolean)v).comment("Enable tin versions of NTP tools (knife, mattock, saw)").requiresMcRestart(),
            ConfigEntry.value("enableBronzeTools", boolean.class, () -> ModConfig.TOOLS.enableBronzeTools, v -> ModConfig.TOOLS.enableBronzeTools = (Boolean)v).comment("Enable bronze versions of NTP tools (knife, mattock, saw)").requiresMcRestart(),
            ConfigEntry.value("enableSteelTools", boolean.class, () -> ModConfig.TOOLS.enableSteelTools, v -> ModConfig.TOOLS.enableSteelTools = (Boolean)v).comment("Enable steel versions of NTP tools (knife, mattock, saw)").requiresMcRestart()),
         ConfigEntry.category("COMPAT",
            ConfigEntry.value("enableRusticCompat", boolean.class, () -> ModConfig.COMPAT.enableRusticCompat, v -> ModConfig.COMPAT.enableRusticCompat = (Boolean)v).comment("Enable compatibility features from Rustic (slate cobblestone + loose rock)").requiresMcRestart(),
            ConfigEntry.value("enableQuarkCompat", boolean.class, () -> ModConfig.COMPAT.enableQuarkCompat, v -> ModConfig.COMPAT.enableQuarkCompat = (Boolean)v).comment("Enable compatibility features from Quark (marble + limestone cobblestone and loose rock)").requiresMcRestart(),
            ConfigEntry.value("enableChiselCompat", boolean.class, () -> ModConfig.COMPAT.enableChiselCompat, v -> ModConfig.COMPAT.enableChiselCompat = (Boolean)v).comment("Enable compatability features from Chisel (marble + limestone + basalt loose rocks)")));
   }
}
