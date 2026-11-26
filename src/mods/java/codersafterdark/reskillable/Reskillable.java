package codersafterdark.reskillable;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import codersafterdark.reskillable.api.ReskillableAPI;
import codersafterdark.reskillable.base.CommonProxy;
import net.minecraftforge.fml.common.Mod;
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
   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new codersafterdark.reskillable.client.base.ClientProxy() : new codersafterdark.reskillable.base.CommonProxy();
         codersafterdark.reskillable.ReskillableRegistryHandler.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         this.serverStarting((FMLServerStartingEvent)event);
      }

   }

   public static CommonProxy proxy;
   public static Logger logger;

   public Reskillable() {
      ReskillableAPI.setInstance(new ReskillableAPI(new ReskillableModAccess()));
   }

   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
      proxy.preInit(event);
   }

   public void init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit(event);
   }

   public void serverStarting(FMLServerStartingEvent event) {
      proxy.serverStarting(event);
   }
}
