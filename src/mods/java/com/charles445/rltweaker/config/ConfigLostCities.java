package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

public class ConfigLostCities {
   @Comment({"Master switch for this mod compatibility"})
   @Name("ENABLED")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean enabled = true;
   @Comment({"Whether the chunk generation cache should be cleaned regularly to avoid memory leaks."})
   @Name("GenerationCacheCleanupEnabled")
   @RLConfig.ImprovementsOnly("false")
   @RLConfig.RLCraftTwoEightTwo("false")
   @RLConfig.RLCraftTwoNine("true")
   public boolean generationCacheCleanupEnabled = false;
   @Comment({"How many entries the caches are allowed to get before they are cleared. Default is recommended."})
   @Name("GenerationCacheMaxCount")
   @RangeInt(
      min = 0
   )
   @RLConfig.ImprovementsOnly("200")
   @RLConfig.RLCraftTwoEightTwo("200")
   @RLConfig.RLCraftTwoNine("200")
   public int generationCacheMaxCount = 200;

   public ConfigLostCities() {
   }
}
