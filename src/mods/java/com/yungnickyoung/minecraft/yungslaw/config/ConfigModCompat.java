package com.yungnickyoung.minecraft.yungslaw.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

public class ConfigModCompat {
   @Name("Quark")
   @Comment({"If enabled, Quark ores will be deleted when using Ore Deletion Mode."})
   @RequiresWorldRestart
   public boolean quark = true;
   @Name("Applied Energistics 2")
   @Comment({"If enabled, AE2 ores will be deleted when using Ore Deletion Mode."})
   @RequiresWorldRestart
   public boolean ae2 = true;

   public ConfigModCompat() {
   }
}
