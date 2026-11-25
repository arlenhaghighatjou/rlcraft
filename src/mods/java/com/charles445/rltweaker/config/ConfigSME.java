package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

public class ConfigSME {
   @Comment({"Master switch for this mod compatibility"})
   @Name("ENABLED")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean enabled = true;
   @Comment({"Advanced Mending fixes"})
   @Name("Manage Advanced Mending")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean manageAdvancedMending = true;
   @Comment({"Leaves in the double XP advanced mending bug. RLCraft vanilla is true"})
   @Name("Advanced Mending Double XP")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean advancedMendingDoubleXP = true;
   @Comment({"Arc Slash fixes"})
   @Name("Manage Arc Slash")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean manageArcSlash = true;
   @Comment({"Curse of Possession fixes"})
   @Name("Manage Curse of Possession")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean manageCurseOfPossession = true;
   @Comment({"How delayed curse of possession is. Vanilla is 1"})
   @Name("Curse of Possession Delay")
   @RangeInt(
      min = 1,
      max = 40
   )
   @RLConfig.ImprovementsOnly("20")
   @RLConfig.RLCraftTwoEightTwo("20")
   @RLConfig.RLCraftTwoNine("20")
   public int curseOfPossessionDelay = 20;
   @Comment({"Empowered Defence fixes"})
   @Name("Manage Empowered Defence")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean manageEmpoweredDefence = true;
   @Comment({"Evasion fixes"})
   @Name("Manage Evasion")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean manageEvasion = true;
   @Comment({"Freezing fixes"})
   @Name("Manage Freezing")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean manageFreezing = true;
   @Comment({"Parry fixes"})
   @Name("Manage Parry")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean manageParry = true;
   @Comment({"Unreasonable fixes"})
   @Name("Manage Unreasonable")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean manageUnreasonable = true;
   @Comment({"Upgraded Potentials fixes"})
   @Name("Manage Upgraded Potentials")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean manageUpgradedPotentials = true;

   public ConfigSME() {
   }
}
