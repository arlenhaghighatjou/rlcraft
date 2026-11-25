package fonnymunkey.portaldupebegone;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

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
   public static PortalDupeBegone INSTANCE;
   public static Logger logger;

   public PortalDupeBegone() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         INSTANCE = this;
         fonnymunkey.portaldupebegone.ModConfig.register();
         fonnymunkey.portaldupebegone.EventHandler.register(MinecraftForge.EVENT_BUS);
         fonnymunkey.portaldupebegone.ModConfig.EventHandler.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
   }
}
