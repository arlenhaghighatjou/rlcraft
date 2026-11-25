package bettercombat.mod.util;

import bettercombat.mod.capability.CapabilityOffhandCooldown;
import bettercombat.mod.handler.EventHandlers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "bettercombatmod",
   name = "RLCombat",
   version = "2.0.8",
   guiFactory = "bettercombat.mod.client.gui.GUIFactory",
   acceptedMinecraftVersions = "[1.12.2]",
   dependencies = "required-after:reachfix;after:qualitytools;after:iceandfire"
)
public class BetterCombatMod {
   @SidedProxy(
      modId = "bettercombatmod",
      clientSide = "bettercombat.mod.client.ClientProxy",
      serverSide = "bettercombat.mod.util.CommonProxy"
   )
   public static CommonProxy proxy;
   @Instance("bettercombatmod")
   public static BetterCombatMod modInstance;
   public static Logger LOG = LogManager.getLogger("bettercombatmod");

   public BetterCombatMod() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      MinecraftForge.EVENT_BUS.register(EventHandlers.INSTANCE);
      proxy.preInit(event);
      ConfigurationHandler.init(event.getSuggestedConfigurationFile());
      MinecraftForge.EVENT_BUS.register(new ConfigurationHandler());
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      CapabilityOffhandCooldown.register();
      ConfigurationHandler.createInstLists();
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
   }
}
