package slimeknights.mantle;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
   @Instance("mantle")
   public static Mantle instance;
   @SidedProxy(
      clientSide = "slimeknights.mantle.client.ClientProxy",
      serverSide = "slimeknights.mantle.common.CommonProxy"
   )
   public static CommonProxy proxy;

   public Mantle() {
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
