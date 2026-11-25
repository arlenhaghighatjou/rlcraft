package com.yungnickyoung.minecraft.bettermineshafts.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

public class ModCompatConfig {
   @Name("Use Rustic Lanterns")
   @Comment({"If Rustic is installed, mineshafts will occasionally have Rustic lanterns hanging from the ceiling."})
   @RequiresWorldRestart
   public boolean rusticLanternsEnabled = true;
   @Name("Use Charm Lanterns")
   @Comment({"If Charm is installed, mineshafts will occasionally have Charm lanterns hanging from the ceiling."})
   @RequiresWorldRestart
   public boolean charmLanternsEnabled = true;
   @Name("Use Varied Commodities Candles")
   @Comment({"If Varied Commodities is installed, mineshafts will occasionally have Varied Commodities candles on the walls."})
   @RequiresWorldRestart
   public boolean variedcommoditiesCandlesEnabled = true;

   public ModCompatConfig() {
   }
}
