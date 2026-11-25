package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

public class ConfigRuins {
   @Comment({"Master switch for this mod compatibility"})
   @Name("ENABLED")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean enabled = true;
   @Comment({"Clean up the Ruins Chunk Logger automatically"})
   @Name("Chunk Logger Cleanup")
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean cleanupChunkLogger = true;
   @Comment({"Threshold to clean up the Ruins Chunk Logger, in chunks"})
   @Name("Chunk Logger Threshold")
   @RangeInt(
      min = 0
   )
   @RLConfig.ImprovementsOnly("20000")
   @RLConfig.RLCraftTwoEightTwo("20000")
   @RLConfig.RLCraftTwoNine("20000")
   public int chunkThreshold = 20000;
   @Comment({"Removes the RUINSTRIGGER tag for custom ruins structures as it is very resource intensive and no custom ruins seem to use it."})
   @Name("Remove RUINSTRIGGER Functionality")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean removeRUINSTRIGGERFunctionality = true;

   public ConfigRuins() {
   }
}
