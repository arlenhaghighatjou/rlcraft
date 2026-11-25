package com.charles445.rltweaker.config;

import com.charles445.rltweaker.config.annotation.RLConfig;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

public class ConfigGooglyEyesClient {
   @Comment({"Master switch for this mod compatibility"})
   @Name("ENABLED")
   @RequiresMcRestart
   @RLConfig.ImprovementsOnly("true")
   @RLConfig.RLCraftTwoEightTwo("true")
   @RLConfig.RLCraftTwoNine("true")
   public boolean enabled = true;
   @Comment({"Entity blacklist for googly eyes that uses the registry name instead"})
   @Name("Entity Blacklist")
   @RLConfig.ImprovementsOnly("examplemod:mob")
   @RLConfig.RLCraftTwoEightTwo("examplemod:mob")
   @RLConfig.RLCraftTwoNine("examplemod:mob")
   public String[] entityBlacklist = new String[]{"examplemod:mob"};

   public ConfigGooglyEyesClient() {
   }
}
