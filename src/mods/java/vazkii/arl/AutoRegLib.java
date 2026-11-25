package vazkii.arl;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
   modid = "autoreglib",
   name = "AutoRegLib",
   version = "1.3-32",
   dependencies = "required-after:forge@[14.21.1.2387,)",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class AutoRegLib {
   public static final String MOD_ID = "autoreglib";
   public static final String MOD_NAME = "AutoRegLib";
   public static final String BUILD = "32";
   public static final String VERSION = "1.3-32";
   public static final String DEPENDENCIES = "required-after:forge@[14.21.1.2387,)";
   public static final String PROXY_COMMON = "vazkii.arl.CommonProxy";
   public static final String PROXY_CLIENT = "vazkii.arl.ClientProxy";
   @Instance("autoreglib")
   public static AutoRegLib instance;
   @SidedProxy(
      serverSide = "vazkii.arl.CommonProxy",
      clientSide = "vazkii.arl.ClientProxy"
   )
   public static CommonProxy proxy;

   public AutoRegLib() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
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
}
