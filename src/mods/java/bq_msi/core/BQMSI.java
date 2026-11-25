package bq_msi.core;

import bq_msi.commands.ClearMultiblockCacheCommand;
import bq_msi.commands.GetMultiblockCacheCommand;
import bq_msi.commands.GetStructureNameCommand;
import bq_msi.core.proxies.CommonProxy;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

@Mod(
   modid = "bq_msi",
   version = "1.0.9",
   name = "BQ_Multiblock_Structure_Integration",
   dependencies = "after:betterquesting;after:reccomplex;after:gamestages"
)
public class BQMSI {
   public static final String MODID = "bq_msi";
   public static final String VERSION = "1.0.9";
   public static final String NAME = "BQ_Multiblock_Structure_Integration";
   public static final String PROXY = "bq_msi.core.proxies";
   public static final String CHANNEL = "BQMSI";
   @Instance("bq_msi")
   public static BQMSI instance;
   @SidedProxy(
      clientSide = "bq_msi.core.proxies.ClientProxy",
      serverSide = "bq_msi.core.proxies.CommonProxy"
   )
   public static CommonProxy proxy;
   public SimpleNetworkWrapper network;
   public static Logger logger;

   public BQMSI() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
      this.network = NetworkRegistry.INSTANCE.newSimpleChannel("BQMSI");
      proxy.registerHandlers();
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      if (Loader.isModLoaded("betterquesting")) {
         proxy.registerExpansion();
      }

   }

   @EventHandler
   public void serverLoad(FMLServerStartingEvent event) {
      if (Loader.isModLoaded("reccomplex")) {
         event.registerServerCommand(new GetStructureNameCommand());
      }

      event.registerServerCommand(new GetMultiblockCacheCommand());
      event.registerServerCommand(new ClearMultiblockCacheCommand());
   }
}
