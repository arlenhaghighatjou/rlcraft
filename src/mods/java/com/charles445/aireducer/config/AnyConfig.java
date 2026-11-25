package com.charles445.aireducer.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;

public class AnyConfig {
   @Comment({"Enables tweaking task delay with the JSON config"})
   @Name("Task Delay Tweaks")
   public boolean taskDelayTweaks = true;

   public AnyConfig() {
   }
}
