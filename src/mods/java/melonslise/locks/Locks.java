package melonslise.locks;

import melonslise.locks.common.command.CommandLocksDebug;
import melonslise.locks.common.config.LocksConfig;
import melonslise.locks.common.init.LocksCapabilities;
import melonslise.locks.common.init.LocksNetworks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "locks",
   name = "Locks",
   version = "3.0.0",
   acceptedMinecraftVersions = "1.12.2"
)
public final class Locks {
   public static final String ID = "locks";
   public static final String NAME = "Locks";
   public static final String VERSION = "3.0.0";
   public static final String GAMEVERSIONS = "1.12.2";
   @Instance("locks")
   public static Locks instance = null;
   public static boolean debug = false;
   public static Logger logger = LogManager.getLogger("Locks");

   public Locks() {
   }

   @EventHandler
   public void onInitialization(FMLInitializationEvent event) {
      LocksCapabilities.register();
      LocksNetworks.register();
      LocksConfig.init();
   }

   @EventHandler
   public void onServerStarting(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandLocksDebug());
   }
}
