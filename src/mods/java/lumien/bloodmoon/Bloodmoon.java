package lumien.bloodmoon;

import lumien.bloodmoon.config.BloodmoonConfig;
import lumien.bloodmoon.proxy.CommonProxy;
import lumien.bloodmoon.server.CommandBloodmoon;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
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
   @Instance("Bloodmoon")
   public static Bloodmoon instance;
   @SidedProxy(
      clientSide = "lumien.bloodmoon.proxy.ClientProxy",
      serverSide = "lumien.bloodmoon.proxy.CommonProxy"
   )
   public static CommonProxy proxy;
   public static BloodmoonConfig config;

   public Bloodmoon() {
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

   @EventHandler
   public void serverStarting(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandBloodmoon());
   }
}
