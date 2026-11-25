package ivorius.reccomplex;

import ivorius.ivtoolkit.tools.MCRegistry;
import ivorius.ivtoolkit.tools.MCRegistryDefault;
import ivorius.reccomplex..shadow.mcopts.translation.ServerTranslations;
import ivorius.reccomplex.commands.RCCommands;
import ivorius.reccomplex.events.handlers.RCForgeEventHandler;
import ivorius.reccomplex.events.handlers.RCRecurrentComplexEventHandler;
import ivorius.reccomplex.events.handlers.RCTerrainGenEventHandler;
import ivorius.reccomplex.files.loading.FileLoader;
import ivorius.reccomplex.files.loading.LeveledRegistry;
import ivorius.reccomplex.files.loading.ResourceDirectory;
import ivorius.reccomplex.files.saving.FileSaver;
import ivorius.reccomplex.gui.RCGuiHandler;
import ivorius.reccomplex.gui.container.IvGuiRegistry;
import ivorius.reccomplex.utils.FMLMissingRemapper;
import ivorius.reccomplex.utils.FMLRemapper;
import ivorius.reccomplex.utils.FMLRemapperConvenience;
import ivorius.reccomplex.utils.MCRegistryRemapping;
import ivorius.reccomplex.world.gen.feature.structure.registry.MCRegistrySpecial;
import ivorius.reccomplex.world.gen.feature.structure.schematics.SchematicLoader;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.NetworkModHolder;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "reccomplex",
   version = "1.4.8.4",
   name = "Recurrent Complex",
   guiFactory = "ivorius.reccomplex.gui.RCConfigGuiFactory",
   dependencies = "required-after:ivtoolkit"
)
public class RecurrentComplex {
   public static final String NAME = "Recurrent Complex";
   public static final String MOD_ID = "reccomplex";
   public static final String VERSION = "1.4.8.4";
   public static final boolean USE_JSON_FOR_NBT = true;
   public static final boolean USE_ZIP_FOR_STRUCTURE_FILES = true;
   public static final boolean PARTIALLY_SPAWN_NATURAL_STRUCTURES = true;
   @Instance("reccomplex")
   public static RecurrentComplex instance;
   @SidedProxy(
      clientSide = "ivorius.reccomplex.client.ClientProxy",
      serverSide = "ivorius.reccomplex.server.ServerProxy"
   )
   public static RCProxy proxy;
   public static String filePathTextures = "textures/mod/";
   public static String textureBase = "reccomplex:";
   public static Logger logger;
   public static Configuration config;
   public static FileLoader loader;
   public static FileSaver saver;
   public static MCRegistry mcRegistry;
   public static FMLRemapper remapper;
   public static FMLRemapperConvenience cremapper;
   public static MCRegistrySpecial specialRegistry;
   public static FMLMissingRemapper missingRemapper;
   public static RCForgeEventHandler forgeEventHandler;
   public static RCTerrainGenEventHandler terrainEventHandler;
   public static RCRecurrentComplexEventHandler recurrentComplexEventHandler;
   public static SimpleNetworkWrapper network;
   public static RCGuiHandler guiHandler;
   public static RCCommunicationHandler communicationHandler;
   public static ServerTranslations translations = new ServerTranslations() {
      public boolean translateServerSide() {
         return RCConfig.isLightweightMode();
      }
   };

   public RecurrentComplex() {
   }

   public static boolean isLite() {
      return RCConfig.isLightweightMode();
   }

   public static boolean checkPerms(EntityPlayer player) {
      boolean b = canHandleSaving(player);
      if (!b) {
         translations.get("reccomplex.save.permission");
      }

      return !b;
   }

   public static boolean canHandleSaving(EntityPlayer player) {
      return player.canUseCommand(2, "setblock");
   }

   @NetworkCheckHandler
   public boolean checkNetwork(Map<String, String> mods, Side side) {
      return isLite() || mods.containsKey("reccomplex");
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      logger = event.getModLog();
      config = new Configuration(event.getSuggestedConfigurationFile());
      config.load();
      RCConfig.loadConfig((String)null);
      config.save();
      ((NetworkModHolder)NetworkRegistry.INSTANCE.registry().get(Loader.instance().getIndexedModList().get("reccomplex"))).testVanillaAcceptance();
      logger.trace(isLite() ? "Entering lightweight mode!" : "Entering default mode!");
      loader = new FileLoader();
      saver = new FileSaver();
      remapper = new FMLRemapper();
      specialRegistry = new MCRegistrySpecial(mcRegistry = new MCRegistryRemapping(new MCRegistryDefault(), remapper), remapper);
      cremapper = new FMLRemapperConvenience("reccomplex", specialRegistry, remapper);
      missingRemapper = new FMLMissingRemapper(new MCRegistryDefault(), remapper);
      forgeEventHandler = new RCForgeEventHandler();
      forgeEventHandler.register();
      terrainEventHandler = new RCTerrainGenEventHandler();
      terrainEventHandler.register();
      recurrentComplexEventHandler = new RCRecurrentComplexEventHandler();
      recurrentComplexEventHandler.register();
      guiHandler = new RCGuiHandler();
      IvGuiRegistry.INSTANCE.register("reccomplex", guiHandler);
      communicationHandler = new RCCommunicationHandler(logger, "reccomplex", instance);
      RCRegistryHandler.preInit(event, this);
      proxy.preInit(event);
      proxy.registerRenderers();
   }

   @EventHandler
   public void load(FMLInitializationEvent event) {
      network = NetworkRegistry.INSTANCE.newSimpleChannel("reccomplex");
      RCRegistryHandler.load(event, this);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      ResourceDirectory.tryReload(loader, LeveledRegistry.Level.MODDED);
      ResourceDirectory.tryReload(loader, LeveledRegistry.Level.CUSTOM);
      SchematicLoader.initializeFolder();
   }

   @EventHandler
   public void onServerStart(FMLServerStartingEvent event) {
      RCCommands.onServerStart(event);
   }

   @EventHandler
   public void onServerStarted(FMLServerStartedEvent event) {
      ResourceDirectory.tryReload(loader, LeveledRegistry.Level.SERVER);
   }
}
