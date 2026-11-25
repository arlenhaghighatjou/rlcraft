package vazkii.arl;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import vazkii.arl.util.ClientTicker;
import vazkii.arl.util.DropInHandler;
import vazkii.arl.util.ModelHandler;
import vazkii.arl.util.ProxyRegistry;

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
   public static AutoRegLib instance;
   public static CommonProxy proxy;

   public AutoRegLib() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            proxy = new ClientProxy();
            ClientTicker.register(MinecraftForge.EVENT_BUS);
            DropInHandler.register(MinecraftForge.EVENT_BUS);
            ModelHandler.register(MinecraftForge.EVENT_BUS);
         } else {
            proxy = new CommonProxy();
         }

         ProxyRegistry.register(MinecraftForge.EVENT_BUS);
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      proxy.preInit(event);
   }

   public void init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit(event);
   }
}
