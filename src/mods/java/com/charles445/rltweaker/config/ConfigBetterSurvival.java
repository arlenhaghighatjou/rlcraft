package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

public class ConfigBetterSurvival {
   @Comment({"Master switch for this mod compatibility"})
   @Name("ENABLED")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean enabled = true;
   @Comment({"Enables blacklist for blocks with the tunneling enchantment"})
   @Name("Tunneling Blacklist Enabled")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean tunnelingBlacklistEnabled = false;
   @Comment({"Whether the tunneling blacklist is a whitelist. Acting as a whitelist may cause buggy behavior. Use with caution."})
   @Name("Tunneling Blacklist Is Whitelist")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("false")
   public boolean tunnelingBlacklistIsWhitelist = false;
   @Comment({"Blocks that are blacklisted from interacting with the tunneling enchantment"})
   @Name("Tunneling Blacklist")
   @RLConfig.ImprovementsOnly("minecraft:chest")
   @RLConfig.RLCraftTwoEightTwo("minecraft:chest")
   @RLConfig.RLCraftTwoNine("minecraft:chest")
   public String[] tunnelingBlacklist = new String[]{"minecraft:chest"};
   @Comment({"Whether tunneling should fail if the center block break event fails. By default, Better Survival has this false"})
   @Name("Tunneling Cancelable")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean tunnelingCancelable = true;
   @Comment({"Whether tunneling can break tile entities"})
   @Name("Tunneling Breaks Tile Entities")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("false")
   public boolean tunnelingBreaksTileEntities = false;
   @Comment({"Speed multiplier for the Range enchantment. Default is 2"})
   @Name("Range Speed Multiplier")
   @RLConfig.ImprovementsOnly("2.0")
   @RLConfig.RLCraftTwoEightTwo("2.0")
   @RLConfig.RLCraftTwoNine("2.0")
   public double rangeSpeedMultiplier = 2.0D;
   @Comment({"Whether blindness affects mobs at all"})
   @Name("Mob Blindness")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean mobBlindness = true;
   @Comment({"Replaces the blindness and combo handler entirely for performance"})
   @Name("Replace Combo Handler")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean replaceComboHandler = true;
   @Comment({"Replaces the enchantment handler entirely for performance"})
   @Name("Replace Enchantment Handler")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean replaceEnchantmentHandler = true;
   @Comment({"How strong the reduced follow range for mobs is with blindness"})
   @Name("Mob Blindness Percentage")
   @RLConfig.ImprovementsOnly("80")
   @RLConfig.RLCraftTwoEightTwo("80")
   @RLConfig.RLCraftTwoNine("80")
   @RangeDouble(
      min = 0.0D,
      max = 100.0D
   )
   public double mobBlindnessPercentage = 80.0D;
   @Comment({"Blacklist for mob blindness"})
   @Name("Mob Blindness Blacklist")
   @RLConfig.ImprovementsOnly("examplemod:mob")
   @RLConfig.RLCraftTwoEightTwo("examplemod:mob")
   @RLConfig.RLCraftTwoNine("examplemod:mob")
   public String[] mobBlindnessBlacklist = new String[]{"examplemod:mob"};

   public ConfigBetterSurvival() {
   }
}
