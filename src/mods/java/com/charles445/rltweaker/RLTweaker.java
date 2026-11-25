package com.charles445.rltweaker;

import com.charles445.rltweaker.capability.ITweakerCapability;
import com.charles445.rltweaker.capability.TweakerCapability;
import com.charles445.rltweaker.capability.TweakerStorage;
import com.charles445.rltweaker.command.CommandAdvisor;
import com.charles445.rltweaker.command.CommandDebug;
import com.charles445.rltweaker.command.CommandErrorReport;
import com.charles445.rltweaker.command.CommandRLTweakerConfig;
import com.charles445.rltweaker.command.CommandReloadInvestigateAIConfig;
import com.charles445.rltweaker.config.JsonConfig;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.debug.DebugUtil;
import com.charles445.rltweaker.handler.AquacultureHandler;
import com.charles445.rltweaker.handler.BattleTowersHandler;
import com.charles445.rltweaker.handler.BaublesHandler;
import com.charles445.rltweaker.handler.BetterSurvivalHandler;
import com.charles445.rltweaker.handler.CarryOnHandler;
import com.charles445.rltweaker.handler.CharmHandler;
import com.charles445.rltweaker.handler.ClassyHatsHandler;
import com.charles445.rltweaker.handler.DynamicSurroundingsHandler;
import com.charles445.rltweaker.handler.GrapplemodHandler;
import com.charles445.rltweaker.handler.IceAndFireHandler;
import com.charles445.rltweaker.handler.InfernalMobsHandler;
import com.charles445.rltweaker.handler.InvestigateAIHandler;
import com.charles445.rltweaker.handler.LevelUpTwoHandler;
import com.charles445.rltweaker.handler.LostCitiesHandler;
import com.charles445.rltweaker.handler.LycanitesHandler;
import com.charles445.rltweaker.handler.MinecraftHandler;
import com.charles445.rltweaker.handler.MotionCheckHandler;
import com.charles445.rltweaker.handler.MultiMineHandler;
import com.charles445.rltweaker.handler.PotionCoreHandler;
import com.charles445.rltweaker.handler.QuarkHandler;
import com.charles445.rltweaker.handler.RecurrentHandler;
import com.charles445.rltweaker.handler.ReskillableHandler;
import com.charles445.rltweaker.handler.RoguelikeHandler;
import com.charles445.rltweaker.handler.RuinsHandler;
import com.charles445.rltweaker.handler.RusticHandler;
import com.charles445.rltweaker.handler.SMEHandler;
import com.charles445.rltweaker.handler.SRParasitesHandler;
import com.charles445.rltweaker.handler.SereneSeasonsHandler;
import com.charles445.rltweaker.handler.SpawnerControlHandler;
import com.charles445.rltweaker.handler.TANHandler;
import com.charles445.rltweaker.handler.WaystonesHandler;
import com.charles445.rltweaker.network.NetworkHandler;
import com.charles445.rltweaker.network.PacketHandler;
import com.charles445.rltweaker.proxy.CommonProxy;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ServerRunnable;
import com.charles445.rltweaker.util.VersionDelimiter;
import com.charles445.rltweaker.util.Watchdog;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
   modid = "rltweaker",
   name = "RLTweaker",
   version = "0.5.12",
   acceptedMinecraftVersions = "[1.12, 1.13)",
   acceptableRemoteVersions = "*",
   dependencies = "required-after:forge@[14.23.5.2859,);"
)
public class RLTweaker {
   public static final String MODID = "rltweaker";
   public static final String NAME = "RLTweaker";
   public static final String VERSION = "0.5.12";
   public static final VersionDelimiter VERSION_DELIMITER = VersionDelimiter.parse("0.5.12");
   @Instance("rltweaker")
   public static RLTweaker instance;
   @SidedProxy(
      clientSide = "com.charles445.rltweaker.proxy.ClientProxy",
      serverSide = "com.charles445.rltweaker.proxy.CommonProxy"
   )
   public static CommonProxy proxy;
   public static Logger logger = LogManager.getLogger("RLTweaker");
   public static Path jsonDirectory;
   public static Map<String, Object> handlers = new HashMap();
   public static Map<String, Object> clientHandlers = new HashMap();
   public static Map<String, ServerRunnable> serverRunnables = new HashMap();

   public RLTweaker() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      FMLCommonHandler.instance().registerCrashCallable(new ErrorUtil.CrashCallable());
      jsonDirectory = event.getModConfigurationDirectory().toPath().resolve("rltweaker");
      ModConfig.onConfigChanged();
      PacketHandler.init();
      CapabilityManager.INSTANCE.register(ITweakerCapability.class, new TweakerStorage(), TweakerCapability::new);
      handlers.put("minecraft", new MinecraftHandler());
      if (ModConfig.server.minecraft.investigateAi) {
         handlers.put("investigate_ai", new InvestigateAIHandler());
      }

      if (Loader.isModLoaded("reskillable") && ModConfig.server.reskillable.enabled) {
         handlers.put("reskillable", new ReskillableHandler());
      }

      if (Loader.isModLoaded("roguelike") && ModConfig.server.roguelike.enabled) {
         handlers.put("roguelike", new RoguelikeHandler());
      }

      if (Loader.isModLoaded("waystones") && ModConfig.server.waystones.enabled) {
         handlers.put("waystones", new WaystonesHandler());
      }

      proxy.preInit();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      if (Loader.isModLoaded("aquaculture") && ModConfig.server.aquaculture.enabled) {
         handlers.put("aquaculture", new AquacultureHandler());
      }

      if (Loader.isModLoaded("grapplemod") && ModConfig.server.grapplemod.enabled) {
         handlers.put("grapplemod", new GrapplemodHandler());
      }

      if (Loader.isModLoaded("iceandfire") && ModConfig.server.iceandfire.enabled) {
         handlers.put("iceandfire", new IceAndFireHandler());
      }

      if (Loader.isModLoaded("reccomplex") && ModConfig.server.recurrentcomplex.enabled) {
         handlers.put("reccomplex", new RecurrentHandler());
      }

      if (Loader.isModLoaded("toughasnails") && ModConfig.server.toughasnails.enabled) {
         handlers.put("toughasnails", new TANHandler());
      }

      if (Loader.isModLoaded("spawnercontrol") && ModConfig.server.spawnercontrol.enabled) {
         handlers.put("spawnercontrol", new SpawnerControlHandler());
      }

      proxy.init();
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      if (Loader.isModLoaded("lostcities") && ModConfig.server.lostcities.enabled) {
         handlers.put("lostcities", new LostCitiesHandler());
      }

      if (Loader.isModLoaded("charm") && ModConfig.server.charm.enabled) {
         handlers.put("charm", new CharmHandler());
      }

      if (Loader.isModLoaded("carryon") && ModConfig.server.carryon.enabled) {
         handlers.put("carryon", new CarryOnHandler());
      }

      if (Loader.isModLoaded("mujmajnkraftsbettersurvival") && ModConfig.server.bettersurvival.enabled) {
         handlers.put("mujmajnkraftsbettersurvival", new BetterSurvivalHandler());
      }

      if (Loader.isModLoaded("baubles") && ModConfig.server.baubles.enabled) {
         handlers.put("baubles", new BaublesHandler());
      }

      if (Loader.isModLoaded("ruins") && ModConfig.server.ruins.enabled) {
         handlers.put("ruins", new RuinsHandler());
      }

      JsonConfig.init();
      proxy.postInit();
   }

   @EventHandler
   public void loadComplete(FMLLoadCompleteEvent event) {
      if (Loader.isModLoaded("srparasites") && ModConfig.server.srparasites.enabled) {
         handlers.put("srparasites", new SRParasitesHandler());
      }

      if (Loader.isModLoaded("somanyenchantments") && ModConfig.server.somanyenchantments.enabled) {
         handlers.put("somanyenchantments", new SMEHandler());
      }

      if (Loader.isModLoaded("battletowers") && ModConfig.server.battletowers.enabled) {
         handlers.put("battletowers", new BattleTowersHandler());
      }

      if (Loader.isModLoaded("levelup2") && ModConfig.server.leveluptwo.enabled) {
         handlers.put("levelup2", new LevelUpTwoHandler());
      }

      if (Loader.isModLoaded("quark") && ModConfig.server.quark.enabled) {
         handlers.put("quark", new QuarkHandler());
      }

      if (Loader.isModLoaded("rustic") && ModConfig.server.rustic.enabled) {
         handlers.put("rustic", new RusticHandler());
      }

      if (Loader.isModLoaded("lycanitesmobs") && ModConfig.server.lycanitesmobs.enabled) {
         handlers.put("lycanitesmobs", new LycanitesHandler());
      }

      if (Loader.isModLoaded("classyhats") && ModConfig.server.classyhats.enabled) {
         handlers.put("classyhats", new ClassyHatsHandler());
      }

      if (Loader.isModLoaded("infernalmobs") && ModConfig.server.infernalmobs.enabled) {
         handlers.put("infernalmobs", new InfernalMobsHandler());
      }

      if (Loader.isModLoaded("multimine") && ModConfig.server.multimine.enabled) {
         handlers.put("multimine", new MultiMineHandler());
      }

      if (Loader.isModLoaded("dsurround") && ModConfig.server.dynamicsurroundings.enabled) {
         handlers.put("dsurround", new DynamicSurroundingsHandler());
      }

      if (Loader.isModLoaded("sereneseasons") && ModConfig.server.sereneseasons.enabled) {
         handlers.put("sereneseasons", new SereneSeasonsHandler());
      }

      if (Loader.isModLoaded("potioncore") && ModConfig.server.potioncore.enabled) {
         handlers.put("potioncore", new PotionCoreHandler());
      }

      proxy.loadComplete();
      handlers.put("MotionCheckHandler", new MotionCheckHandler());
      if (ModConfig.server.minecraft.watchdog) {
         Watchdog.init();
      }

      DebugUtil.loadCompleteDebugRoutine();
   }

   @EventHandler
   public void serverStarting(FMLServerStartingEvent event) {
      event.registerServerCommand(new CommandAdvisor());
      event.registerServerCommand(new CommandDebug());
      event.registerServerCommand(new CommandErrorReport());
      event.registerServerCommand(new CommandRLTweakerConfig());
      event.registerServerCommand(new CommandReloadInvestigateAIConfig());
      serverRunnables.values().forEach((runnable) -> {
         runnable.onServerStarting();
      });
   }

   @EventHandler
   public void serverStopping(FMLServerStoppingEvent event) {
      serverRunnables.values().forEach((runnable) -> {
         runnable.onServerStopping();
      });
   }

   @NetworkCheckHandler
   public boolean checkVersion(Map<String, String> values, Side side) {
      String version = (String)values.get("rltweaker");
      if (side == Side.SERVER) {
         NetworkHandler.serverHasVersioning = false;
         if (StringUtils.isEmpty(version)) {
            NetworkHandler.serverVersion = VersionDelimiter.UNKOWN;
         } else {
            VersionDelimiter servervd = VersionDelimiter.parse(version);
            NetworkHandler.serverVersion = servervd;
            if (servervd.isSameOrNewerVersion(0, 4)) {
               NetworkHandler.serverHasVersioning = true;
            }
         }

         logger.trace("Server Version: " + NetworkHandler.serverVersion);
         logger.trace("Local Version: " + VERSION_DELIMITER);
         logger.trace("Server Has Versioning: " + NetworkHandler.serverHasVersioning);
         return true;
      } else {
         if (StringUtils.isEmpty(version)) {
            logger.trace("Client Version: " + VersionDelimiter.UNKOWN);
         } else {
            logger.trace("Client Version: " + VersionDelimiter.parse(version));
         }

         logger.trace("Local Version: " + VERSION_DELIMITER);
         return true;
      }
   }
}
