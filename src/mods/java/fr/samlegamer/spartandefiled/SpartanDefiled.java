package fr.samlegamer.spartandefiled;

import fr.samlegamer.spartandefiled.proxy.CommonProxy;
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
   @Instance("spartandefiled")
   public static SpartanDefiled instance;
   public static final Logger LOGGER = LogManager.getLogger("spartandefiled");
   @SidedProxy(
      clientSide = "fr.samlegamer.spartandefiled.proxy.ClientProxy",
      serverSide = "fr.samlegamer.spartandefiled.proxy.ServerProxy"
   )
   public static CommonProxy proxy;

   public SpartanDefiled() {
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
