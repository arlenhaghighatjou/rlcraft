package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

public class ConfigSRParasites {
   @Comment({"Master switch for this mod compatibility"})
   @Name("ENABLED")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean enabled = true;
   @Comment({"Whether parasites should prevent players from sleeping"})
   @Name("Parasites Sleep Prevention")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("false")
   public boolean parasitesSleepPrevention = true;
   @Comment({"Forcefully remove parasites from certain dimensions"})
   @Name("Parasites Dimension Blacklist Enabled")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean parasitesDimensionBlacklistEnabled = false;
   @Comment({"Dimension blacklist for parasites"})
   @Name("Parasites Dimension Blacklist")
   @RLConfig.ImprovementsOnly("")
   @RLConfig.RLCraftTwoEightTwo("")
   @RLConfig.RLCraftTwoNine("0|1|-1")
   public int[] parasitesDimensionBlacklist = new int[0];
   @Comment({"Attempt to fix biomass crash"})
   @Name("Parasites Biomass Crash Fix")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean parasitesBiomassCrashFix = true;

   public ConfigSRParasites() {
   }
}
