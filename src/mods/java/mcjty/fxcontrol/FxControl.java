package mcjty.fxcontrol;

import mcjty.fxcontrol.commands.CmdDebug;
import mcjty.fxcontrol.commands.CmdDumpBlockNBT;
import mcjty.fxcontrol.commands.CmdDumpItemNBT;
import mcjty.fxcontrol.commands.CmdReload;
import mcjty.fxcontrol.setup.IProxy;
import mcjty.fxcontrol.setup.ModSetup;
import mcjty.tools.cache.StructureCache;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

@Mod(
   modid = "fxcontrol",
   name = "FxControl",
   dependencies = "after:forge@[13.19.0.2176,)",
   version = "0.1.14",
   acceptedMinecraftVersions = "[1.12,1.13)",
   acceptableRemoteVersions = "*"
)
public class FxControl {
   public static final String MODID = "fxcontrol";
   public static final String MODNAME = "FxControl";
   public static final String VERSION = "0.1.14";
   public static final String MIN_FORGE11_VER = "13.19.0.2176";
   @SidedProxy(
      clientSide = "mcjty.fxcontrol.setup.ClientProxy",
      serverSide = "mcjty.fxcontrol.setup.ServerProxy"
   )
   public static IProxy proxy;
   public static ModSetup setup = new ModSetup();
   @Instance
   public static FxControl instance;

   public FxControl() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      setup.preInit(event);
      proxy.preInit(event);
   }

   @EventHandler
   public void init(FMLInitializationEvent e) {
      setup.init(e);
      proxy.init(e);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent e) {
      setup.postInit(e);
      proxy.postInit(e);
   }

   @EventHandler
   public void onLoadComplete(FMLLoadCompleteEvent e) {
      RulesManager.readRules();
   }

   @EventHandler
   public void serverLoad(FMLServerStartingEvent event) {
      event.registerServerCommand(new CmdReload());
      event.registerServerCommand(new CmdDebug());
      event.registerServerCommand(new CmdDumpItemNBT());
      event.registerServerCommand(new CmdDumpBlockNBT());
   }

   @EventHandler
   public void serverStopped(FMLServerStoppedEvent event) {
      StructureCache.CACHE.clean();
   }
}
