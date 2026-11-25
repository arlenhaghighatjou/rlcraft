package com.yungnickyoung.minecraft.yungslaw.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

public class ConfigReplacementMode {
   @Name("Hard Block")
   @Comment({"The block that will replace the underground in areas far from Safe Blocks. Defaults to obsidian if value provided is invalid."})
   @RequiresWorldRestart
   public String hardBlock = "minecraft:obsidian";

   public ConfigReplacementMode() {
   }
}
