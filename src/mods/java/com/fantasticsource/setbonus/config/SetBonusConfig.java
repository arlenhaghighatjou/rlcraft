package com.fantasticsource.setbonus.config;

import com.fantasticsource.setbonus.config.client.ClientConfig;
import com.fantasticsource.setbonus.config.server.ServerConfig;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;

@Config(
   modid = "setbonus",
   name = "setbonus/1.12.2.009+"
)
public class SetBonusConfig {
   @Name("Client Settings")
   @LangKey("setbonus.config.clientSettings")
   public static ClientConfig clientSettings = new ClientConfig();
   @Name("Server Settings")
   @LangKey("setbonus.config.serverSettings")
   public static ServerConfig serverSettings = new ServerConfig();

   public SetBonusConfig() {
   }
}
