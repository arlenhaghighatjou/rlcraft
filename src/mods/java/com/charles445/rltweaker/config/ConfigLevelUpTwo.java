package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

public class ConfigLevelUpTwo {
   @Comment({"Master switch for this mod compatibility"})
   @Name("ENABLED")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean enabled = true;
   @Comment({"Whether pets can use skills"})
   @Name("Pets Use Skills")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("false")
   public boolean petsUseSkills = true;
   @Comment({"Overhauls the stealth mechanic"})
   @Name("Stealth Overhaul")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean stealthOverhaul = false;
   @Comment({"Should stealth overhaul apply to Lycanites Mobs. Only does anything if Stealth Overhaul is enabled"})
   @Name("Stealth Overhaul Lycanites")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean stealthOverhaulLycanites = true;
   @Comment({"Base distance mobs can see you at while sneaking for stealth calculations. Only applies if stealth level is over zero."})
   @Name("Stealth Overhaul Base Distance")
   @RLConfig.ImprovementsOnly("16.0")
   @RLConfig.RLCraftTwoEightTwo("16.0")
   @RLConfig.RLCraftTwoNine("16.0")
   public double stealthOverhaulBaseDistance = 16.0D;
   @Comment({"How much closer in blocks mobs need to be to see a sneaking player per stealth level."})
   @Name("Stealth Overhaul Distance Per Level")
   @RLConfig.ImprovementsOnly("0.8")
   @RLConfig.RLCraftTwoEightTwo("0.8")
   @RLConfig.RLCraftTwoNine("0.8")
   public double stealthOverhaulDistancePerLevel = 0.8D;

   public ConfigLevelUpTwo() {
   }
}
