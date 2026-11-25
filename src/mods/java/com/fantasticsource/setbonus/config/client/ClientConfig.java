package com.fantasticsource.setbonus.config.client;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;

public class ClientConfig {
   @Name("1. Enable Tooltips")
   @LangKey("setbonus.config.enableTooltips")
   @Comment({"Whether or not to show set bonus information in discovered set item tooltips"})
   public boolean enableTooltips = true;

   public ClientConfig() {
   }
}
