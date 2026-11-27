package slimeknights.mantle;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import slimeknights.mantle.common.CommonProxy;

@Mod(
   modid = "mantle",
   name = "Mantle",
   version = "1.12-1.3.3.55",
   dependencies = "required-after:forge@[14.21.1.2387,)",
   acceptedMinecraftVersions = "[1.12,1.13)"
)
public class Mantle {
   public static final String modId = "mantle";
   public static final String modName = "Mantle";
   public static final String modVersion = "1.12-1.3.3.55";
   public static final Logger logger = LogManager.getLogger("Mantle");
   public static Mantle instance;
   public static CommonProxy proxy;

   public Mantle() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new slimeknights.mantle.client.ClientProxy() : new slimeknights.mantle.common.CommonProxy();
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
