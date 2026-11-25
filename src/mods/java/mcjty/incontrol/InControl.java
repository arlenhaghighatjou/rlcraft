package mcjty.incontrol;

import mcjty.incontrol.commands.CmdDebug;
import mcjty.incontrol.commands.CmdKillMobs;
import mcjty.incontrol.commands.CmdLoadLoot;
import mcjty.incontrol.commands.CmdLoadPotentialSpawn;
import mcjty.incontrol.commands.CmdLoadSpawn;
import mcjty.incontrol.commands.CmdLoadSummonAid;
import mcjty.incontrol.commands.CmdReload;
import mcjty.incontrol.commands.CmdShowMobs;
import mcjty.incontrol.rules.EntityModCache;
import mcjty.incontrol.rules.RulesManager;
import mcjty.incontrol.setup.IProxy;
import mcjty.incontrol.setup.ModSetup;
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
   modid = "incontrol",
   name = "InControl",
   dependencies = "after:forge@[13.19.0.2176,)",
   version = "3.9.18",
   acceptedMinecraftVersions = "[1.12,1.13)",
   acceptableRemoteVersions = "*"
)
public class InControl {
   public static final String MODID = "incontrol";
   public static final String MODNAME = "InControl";
   public static final String VERSION = "3.9.18";
   public static final String MIN_FORGE11_VER = "13.19.0.2176";
   @SidedProxy(
      clientSide = "mcjty.incontrol.setup.ClientProxy",
      serverSide = "mcjty.incontrol.setup.ServerProxy"
   )
   public static IProxy proxy;
   public static ModSetup setup = new ModSetup();
   @Instance
   public static InControl instance;
   public EntityModCache modCache = new EntityModCache();

   public InControl() {
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
      event.registerServerCommand(new CmdLoadSpawn());
      event.registerServerCommand(new CmdLoadPotentialSpawn());
      event.registerServerCommand(new CmdLoadSummonAid());
      event.registerServerCommand(new CmdLoadLoot());
      event.registerServerCommand(new CmdShowMobs());
      event.registerServerCommand(new CmdKillMobs());
   }

   @EventHandler
   public void serverStopped(FMLServerStoppedEvent event) {
      StructureCache.CACHE.clean();
   }
}
