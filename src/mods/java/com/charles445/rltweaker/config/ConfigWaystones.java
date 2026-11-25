package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

public class ConfigWaystones {
   @Comment({"Master switch for this mod compatibility"})
   @Name("ENABLED")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean enabled = true;
   @Comment({"Remove biome name from new Waystones in villages"})
   @Name("Remove Waystone Biome Name")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean removeWaystoneBiome = false;

   public ConfigWaystones() {
   }
}
