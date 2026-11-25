package codersafterdark.reskillable;

import codersafterdark.reskillable.api.ReskillableAPI;
import codersafterdark.reskillable.base.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "reskillable",
   name = "Reskillable",
   version = "1.12.2-1.13.0",
   guiFactory = "codersafterdark.reskillable.client.gui.GuiFactory",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class Reskillable {
   @SidedProxy(
      serverSide = "codersafterdark.reskillable.base.CommonProxy",
      clientSide = "codersafterdark.reskillable.client.base.ClientProxy"
   )
   public static CommonProxy proxy;
   public static Logger logger;

   public Reskillable() {
      ReskillableAPI.setInstance(new ReskillableAPI(new ReskillableModAccess()));
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
      proxy.preInit(event);
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit(event);
   }

   @EventHandler
   public void serverStarting(FMLServerStartingEvent event) {
      proxy.serverStarting(event);
   }
}
