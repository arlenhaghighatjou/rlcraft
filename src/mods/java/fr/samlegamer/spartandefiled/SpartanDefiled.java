package fr.samlegamer.spartandefiled;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import fr.samlegamer.spartandefiled.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "spartandefiled",
   name = "Spartan Defiled",
   version = "1.0",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class SpartanDefiled {
   public static final String MODID = "spartandefiled";
   public static final String NAME = "Spartan Defiled";
   public static final String VERSION = "1.0";
   public static final String CLIENT_PROXY = "fr.samlegamer.spartandefiled.proxy.ClientProxy";
   public static final String SERVER_PROXY = "fr.samlegamer.spartandefiled.proxy.ServerProxy";
   public static SpartanDefiled instance;
   public static final Logger LOGGER = LogManager.getLogger("spartandefiled");
   public static CommonProxy proxy;

   public SpartanDefiled() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new fr.samlegamer.spartandefiled.proxy.ClientProxy() : new fr.samlegamer.spartandefiled.proxy.ServerProxy();
         fr.samlegamer.spartandefiled.items.SDItemsRegistry.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit();
   }

   public void init(FMLInitializationEvent event) {
      proxy.init();
   }

   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit();
   }
}
