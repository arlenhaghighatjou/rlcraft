package jaredbgreat.dldungeons;

import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import jaredbgreat.dldungeons.builder.RegisteredBlock;
import jaredbgreat.dldungeons.commands.CmdDimID;
import jaredbgreat.dldungeons.commands.CmdForceInstallThemes;
import jaredbgreat.dldungeons.commands.CmdInstallThemes;
import jaredbgreat.dldungeons.commands.CmdReload;
import jaredbgreat.dldungeons.commands.CmdSpawn;
import jaredbgreat.dldungeons.themes.ThemeReader;
import jaredbgreat.dldungeons.themes.ThemeType;
import jaredbgreat.dldungeons.util.debug.DLDProfile;
import jaredbgreat.dldungeons.util.debug.DoNothing;
import jaredbgreat.dldungeons.util.debug.IProfiler;
import jaredbgreat.dldungeons.util.debug.Logging;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(
   modid = "dldungeonsjbg",
   name = "Doomlike Dungeons",
   version = "1.14.12",
   acceptableRemoteVersions = "*",
   acceptedMinecraftVersions = "[1.12]"
)
public class DoomlikeDungeons {
   private static GenerationHandler dungeonPlacer;
   public static IProfiler profiler;
   public static DoomlikeDungeons instance;

   public DoomlikeDungeons() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      } else if (event instanceof FMLServerStartingEvent) {
         this.serverLoad((FMLServerStartingEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      Logging.logInfo("Doomlike Dungeons is in preInit, should now load config.");
      ConfigHandler.configDir = ConfigHandler.findConfigDir(event.getModConfigurationDirectory());
      ConfigHandler.init();
      Logging.logInfo("Config should now be loaded.");
      if (ConfigHandler.profile) {
         profiler = new DLDProfile();
      } else {
         profiler = new DoNothing();
      }

   }

   public void init(FMLInitializationEvent event) {
      if (ConfigHandler.naturalSpawn) {
         dungeonPlacer = new GenerationHandler();
      }

      RegisteredBlock.add("minecraft:air");
   }

   public void postInit(FMLPostInitializationEvent event) {
      ConfigHandler.generateLists();
      ThemeReader.readThemes();
      ThemeType.SyncMobLists();
   }

   public void serverLoad(FMLServerStartingEvent event) {
      event.registerServerCommand(new CmdSpawn());
      event.registerServerCommand(new CmdReload());
      event.registerServerCommand(new CmdDimID());
      if (ConfigHandler.installCmd) {
         event.registerServerCommand(new CmdInstallThemes());
         event.registerServerCommand(new CmdForceInstallThemes());
      }

   }
}
