package dttraverse;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import dttraverse.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "dttraverse",
   name = "Dynamic Trees Traverse",
   version = "1.12.2-2.1",
   dependencies = "required-after:dynamictrees@[1.12.2-0.9.18,);required-after:traverse",
   updateJSON = "https://github.com/Harleyoc1/DynamicTreesVersionInfo/blob/master/Add-ons/Traverse.json?raw=true",
   acceptedMinecraftVersions = "[1.12.2]"
)
public final class DynamicTreesTraverse {
   public static final String MOD_ID = "dttraverse";
   public static final String NAME = "Dynamic Trees Traverse";
   public static final String VERSION = "1.12.2-2.1";
   public static final String DEPENDENCIES = "required-after:dynamictrees@[1.12.2-0.9.18,);required-after:traverse";
   public static DynamicTreesTraverse instance;
   public static final Logger logger = LogManager.getLogger("dttraverse");
   public static CommonProxy proxy;

   public DynamicTreesTraverse() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new dttraverse.proxy.ClientProxy() : new dttraverse.proxy.CommonProxy();
         dttraverse.ModContent.register(MinecraftForge.EVENT_BUS);
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
