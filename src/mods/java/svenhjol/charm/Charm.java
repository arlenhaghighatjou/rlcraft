package svenhjol.charm;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import svenhjol.charm.automation.CharmAutomation;
import svenhjol.charm.base.CharmLoader;
import svenhjol.charm.base.CommonProxy;
import svenhjol.charm.brewing.CharmBrewing;
import svenhjol.charm.crafting.CharmCrafting;
import svenhjol.charm.enchanting.CharmEnchanting;
import svenhjol.charm.smithing.CharmSmithing;
import svenhjol.charm.tweaks.CharmTweaks;
import svenhjol.charm.world.CharmWorld;
import svenhjol.meson.Feature;
import svenhjol.meson.Meson;
import svenhjol.meson.Module;

@Mod(
   modid = "charm",
   version = "1.4",
   name = "Charm",
   dependencies = "after:jei@[4.14.3,);after:redstoneplusplus;after:quark",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class Charm {
   public static final String MOD_NAME = "Charm";
   public static final String MOD_ID = "charm";
   public static final String MOD_VERSION = "1.4";
   public static final String PROXY_COMMON = "svenhjol.charm.base.CommonProxy";
   public static final String PROXY_CLIENT = "svenhjol.charm.base.ClientProxy";
   public static final String DEPENDENCIES = "after:jei@[4.14.3,);after:redstoneplusplus;after:quark";
   @Instance("charm")
   public static Charm instance;
   @SidedProxy(
      serverSide = "svenhjol.charm.base.CommonProxy",
      clientSide = "svenhjol.charm.base.ClientProxy"
   )
   public static CommonProxy proxy;

   public Charm() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      Meson.init();
      CharmLoader.INSTANCE.registerModLoader("charm").setup(new CharmAutomation(), new CharmBrewing(), new CharmCrafting(), new CharmEnchanting(), new CharmSmithing(), new CharmTweaks(), new CharmWorld());
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
      proxy.serverStarting(event);
   }

   public static boolean hasFeature(Class<? extends Feature> feature) {
      return CharmLoader.INSTANCE.enabledFeatures.contains(feature);
   }

   public static boolean hasModule(Class<? extends Module> module) {
      return CharmLoader.INSTANCE.enabledModules.contains(module);
   }
}
