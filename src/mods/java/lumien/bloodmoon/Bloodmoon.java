package lumien.bloodmoon;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import lumien.bloodmoon.config.BloodmoonConfig;
import lumien.bloodmoon.proxy.CommonProxy;
import lumien.bloodmoon.server.BloodmoonHandler;
import lumien.bloodmoon.server.CommandBloodmoon;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(
   modid = "bloodmoon",
   name = "Bloodmoon",
   version = "1.5.3",
   certificateFingerprint = "d72e0dd57935b3e9476212aea0c0df352dd76291",
   acceptedMinecraftVersions = "[1.12,1.13)"
)
public class Bloodmoon {
   public static Bloodmoon instance;
   public static CommonProxy proxy;
   public static BloodmoonConfig config;

   public Bloodmoon() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         proxy = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new lumien.bloodmoon.proxy.ClientProxy() : new lumien.bloodmoon.proxy.CommonProxy();
         lumien.bloodmoon.config.BloodmoonConfig.register();
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

   public void preInit(FMLPreInitializationEvent event) {
      MapStorage.storageProviders.put(BloodmoonHandler.class, BloodmoonHandler::new);
      proxy.preInit(event);
   }

   public void init(FMLInitializationEvent event) {
      proxy.init(event);
   }

   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit(event);
   }

   public void serverStarting(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandBloodmoon());
   }
}
