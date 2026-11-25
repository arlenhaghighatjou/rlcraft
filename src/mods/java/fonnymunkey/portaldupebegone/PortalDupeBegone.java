package fonnymunkey.portaldupebegone;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "portaldupebegone",
   name = "PortalDupeBegone",
   version = "1.0.0",
   acceptableRemoteVersions = "*"
)
public class PortalDupeBegone {
   public static final String MOD_ID = "portaldupebegone";
   public static final String MOD_NAME = "PortalDupeBegone";
   public static final String VERSION = "1.0.0";
   @Instance("portaldupebegone")
   public static PortalDupeBegone INSTANCE;
   public static Logger logger;

   public PortalDupeBegone() {
   }

   @net.minecraftforge.fml.common.Mod.EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
   }
}
