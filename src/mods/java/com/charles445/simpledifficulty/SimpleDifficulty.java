package com.charles445.simpledifficulty;

import com.charles445.simpledifficulty.command.CommandSimpleDifficulty;
import com.charles445.simpledifficulty.debug.DebugVerifier;
import com.charles445.simpledifficulty.network.PacketHandler;
import com.charles445.simpledifficulty.proxy.IProxy;
import java.io.File;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "simpledifficulty",
   name = "SimpleDifficulty",
   version = "0.3.9",
   acceptedMinecraftVersions = "[1.12, 1.13)",
   dependencies = "required-after:forge@[14.23.5.2859,);",
   updateJSON = "https://raw.githubusercontent.com/Charles445/SimpleDifficulty/master/modupdatechecker.json"
)
public class SimpleDifficulty {
   public static final String MODID = "simpledifficulty";
   public static final String NAME = "SimpleDifficulty";
   public static final String VERSION = "0.3.9";
   @Instance("simpledifficulty")
   public static SimpleDifficulty instance;
   public static Logger logger = LogManager.getLogger("SimpleDifficulty");
   @SidedProxy(
      clientSide = "com.charles445.simpledifficulty.proxy.ClientProxy",
      serverSide = "com.charles445.simpledifficulty.proxy.ServerProxy"
   )
   public static IProxy proxy;
   public static File jsonDirectory;

   public SimpleDifficulty() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      jsonDirectory = new File(event.getModConfigurationDirectory(), "simpledifficulty");
      PacketHandler.init();
      proxy.preInit();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      proxy.init();
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      proxy.postInit();
   }

   @EventHandler
   public void loadComplete(FMLLoadCompleteEvent event) {
      DebugVerifier verifier = new DebugVerifier();
      verifier.verify();
   }

   @EventHandler
   public void serverStarting(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandSimpleDifficulty());
   }

   static {
      FluidRegistry.enableUniversalBucket();
   }
}
