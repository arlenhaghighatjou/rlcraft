package pl.asie.foamfix;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.asie.foamfix.api.FoamFixAPI;
import pl.asie.foamfix.common.FoamFixHelper;
import pl.asie.foamfix.common.PropertyValueDeduplicator;
import pl.asie.foamfix.common.WorldNuller;
import pl.asie.foamfix.ghostbuster.CommandGhostBuster;
import pl.asie.foamfix.ghostbuster.GhostBusterEventHandler;
import pl.asie.foamfix.shared.FoamFixShared;

@Mod(
   modid = "foamfix",
   name = "FoamFix",
   version = "@VERSION@",
   acceptableRemoteVersions = "*",
   acceptedMinecraftVersions = "[1.12.2,1.13)",
   dependencies = "required:forge@[14.23.3.2673,);",
   guiFactory = "pl.asie.foamfix.client.gui.FoamFixGuiFactory"
)
public class FoamFix {
   private static Item AIR;
   public static Map<Class<? extends TileEntity>, ResourceLocation> TILE_OVERRIDES = new HashMap();
   @SidedProxy(
      clientSide = "pl.asie.foamfix.ProxyClient",
      serverSide = "pl.asie.foamfix.ProxyCommon",
      modId = "foamfix"
   )
   public static ProxyCommon proxy;
   private static Logger logger;
   public static int stage;
   public static boolean shouldFasterAnimation = false;

   public FoamFix() {
   }

   public static Item getItemAir() {
      return AIR == null ? (AIR = Item.getItemFromBlock(Blocks.AIR)) : AIR;
   }

   public static Logger getLogger() {
      if (logger == null) {
         logger = LogManager.getLogger("foamfix");
      }

      return logger;
   }

   @EventHandler
   public void serverStarting(FMLServerStartingEvent event) {
      AIR = Item.getItemFromBlock(Blocks.AIR);
      if (FoamFixShared.config.gbEnableWrapper) {
         event.registerServerCommand(new CommandGhostBuster());
      }

   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      FoamFixAPI.HELPER = new FoamFixHelper();
      stage = 0;
      FoamFixShared.config.init(event.getSuggestedConfigurationFile(), false);
      MinecraftForge.EVENT_BUS.register(proxy);
      proxy.preInit();
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      stage = 1;
      if (FoamFixShared.config.geDeduplicate) {
         PropertyValueDeduplicator deduplicator = new PropertyValueDeduplicator();
         deduplicator.deduplicate();
         getLogger().info("Deduplicated " + deduplicator.successfuls + " property sets.");
      }

      if (FoamFixShared.config.gbEnableWrapper) {
         MinecraftForge.EVENT_BUS.register(new GhostBusterEventHandler());
      }

      if (FoamFixShared.config.gbNotifyNonUnloadedWorlds) {
         WorldNuller.init();
      }

      MinecraftForge.EVENT_BUS.register(proxy);
      proxy.init();
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      stage = 2;
      proxy.postInit();
      if (FoamFixShared.config.twDisableRedstoneLight) {
         Blocks.REDSTONE_TORCH.setLightLevel(0.0F);
         Blocks.POWERED_REPEATER.setLightLevel(0.0F);
         Blocks.POWERED_COMPARATOR.setLightLevel(0.0F);
      }

      MinecraftForge.EVENT_BUS.register(this);
   }

   @EventHandler
   public void serverStopping(FMLServerStoppingEvent event) {
   }

   @SubscribeEvent
   public void configChanged(OnConfigChangedEvent event) {
      if ("foamfix".equals(event.getModID())) {
         FoamFixShared.config.reload();
         if (FoamFixShared.config.resourceDirty) {
            proxy.refreshResources();
            FoamFixShared.config.resourceDirty = false;
         }
      }

   }
}
