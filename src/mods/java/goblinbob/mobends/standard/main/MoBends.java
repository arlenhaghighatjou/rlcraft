package goblinbob.mobends.standard.main;

import goblinbob.mobends.core.Core;
import goblinbob.mobends.core.addon.Addons;
import goblinbob.mobends.core.animation.keyframe.AnimationLoader;
import goblinbob.mobends.core.bender.EntityBenderRegistry;
import goblinbob.mobends.core.data.EntityDatabase;
import goblinbob.mobends.core.pack.PackDataProvider;
import goblinbob.mobends.core.util.GsonResources;
import java.util.logging.Logger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
   modid = "mobends"
)
public class MoBends {
   @SidedProxy(
      serverSide = "goblinbob.mobends.standard.main.CommonProxy",
      clientSide = "goblinbob.mobends.standard.client.ClientProxy"
   )
   public static CommonProxy proxy;
   @Instance("mobends")
   public static MoBends instance;
   public static final Logger LOG = Logger.getLogger("mobends");

   public MoBends() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      proxy.createCore();
      Core.getInstance().preInit(event);
      proxy.preInit();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      Core.getInstance().init(event);
      proxy.init();
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      Core.getInstance().postInit(event);
      proxy.postInit();
   }

   public static void refreshSystems() {
      AnimationLoader.clearCache();
      GsonResources.clearCache();
      PackDataProvider.INSTANCE.clearCache();
      EntityDatabase.instance.refresh();
      EntityBenderRegistry.instance.refreshMutators();
      Addons.onRefresh();
      Core.getInstance().refreshModules();
   }
}
