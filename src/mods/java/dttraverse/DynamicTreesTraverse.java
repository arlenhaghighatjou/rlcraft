package dttraverse;

import dttraverse.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
   @Instance
   public static DynamicTreesTraverse instance;
   public static final Logger logger = LogManager.getLogger("dttraverse");
   @SidedProxy(
      clientSide = "dttraverse.proxy.ClientProxy",
      serverSide = "dttraverse.proxy.CommonProxy"
   )
   public static CommonProxy proxy;

   public DynamicTreesTraverse() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.init();
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit();
   }
}
