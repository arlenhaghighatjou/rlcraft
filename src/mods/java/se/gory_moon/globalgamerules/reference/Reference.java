package se.gory_moon.globalgamerules.reference;

import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class Reference {
   public static final String MODID = "globalgamerules";
   public static final String NAME = "Global GameRules";
   public static final String VERSION = "2.2";
   public static final String PROXY_CLIENT = "se.gory_moon.globalgamerules.proxy.ClientProxy";
   public static final String PROXY_SERVER = "se.gory_moon.globalgamerules.proxy.ServerProxy";
   public static final String GUI_FACTORY = "se.gory_moon.globalgamerules.config.GGRGuiFactory";
   public static Logger logger = LogManager.getLogger("Global GameRules".replace(" ", ""));

   public Reference() {
   }
}
