package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

public class ConfigRecurrent {
   @Comment({"Master switch for this mod compatibility"})
   @Name("ENABLED")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean enabled = true;
   @Comment({"Allows for some recurrent complex event configuration"})
   @Name("Manage Recurrent Complex Events")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean manageRCForgeEventHandler = true;
   @Comment({"Generates structures one chunk at a time. Vanilla Recurrent Complex is true"})
   @Name("Generate Structures Partially")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("false")
   public boolean generatePartially = false;
   @Comment({"Cleans the reccomplex-structuredata.dat regularly. Vanilla Recurrent Complex is false"})
   @Name("Clean Structure Data")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean cleanStructureData = true;
   @Comment({"How many structures need to generate before the dat is cleaned"})
   @Name("Clean Structure Threshold")
   @RangeInt(
      min = 1
   )
   @RLConfig.ImprovementsOnly("100")
   @RLConfig.RLCraftTwoEightTwo("100")
   @RLConfig.RLCraftTwoNine("100")
   public int cleanStructureThreshold = 100;

   public ConfigRecurrent() {
   }
}
