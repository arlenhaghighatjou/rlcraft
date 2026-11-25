package com.fantasticsource.fantasticlib;

import com.fantasticsource.fantasticlib.api.INBTCap;
import com.fantasticsource.fantasticlib.config.FantasticConfig;
import com.fantasticsource.mctools.DataFiles;
import com.fantasticsource.mctools.EntityRenderFixer;
import com.fantasticsource.mctools.ImprovedRayTracing;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Network;
import com.fantasticsource.mctools.PlayerData;
import com.fantasticsource.mctools.Render;
import com.fantasticsource.mctools.TooltipAlterer;
import com.fantasticsource.mctools.TooltipFixer;
import com.fantasticsource.mctools.betterattributes.BetterAttributeMod;
import com.fantasticsource.mctools.data.CModpackDataHandler;
import com.fantasticsource.mctools.data.CWorldDataHandler;
import com.fantasticsource.mctools.event.GametypeChangedEvent;
import com.fantasticsource.mctools.event.InventoryChangedEvent;
import com.fantasticsource.mctools.gui.screen.TestGUI;
import com.fantasticsource.mctools.nbtcap.NBTCap;
import com.fantasticsource.mctools.nbtcap.NBTCapStorage;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.datastructures.ColorImmutable;
import java.util.concurrent.Callable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.PostConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
   modid = "fantasticlib",
   name = "Fantastic Lib",
   version = "1.12.2.047",
   acceptableRemoteVersions = "*",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class FantasticLib {
   public static final String MODID = "fantasticlib";
   public static final String DOMAIN = "flib";
   public static final String NAME = "Fantastic Lib";
   public static final String VERSION = "1.12.2.047";
   public static long serverStartTime = -1L;
   public static boolean isClient = false;
   public static final boolean DEV_ENV = ReflectionTool.getField(ItemStack.class, "stackSize") != null;

   public FantasticLib() {
      MinecraftForge.EVENT_BUS.register(FantasticLib.class);
      MinecraftForge.EVENT_BUS.register(NBTCap.class);
      MinecraftForge.EVENT_BUS.register(BetterAttributeMod.class);
      Network.init();
      if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
         isClient = true;
         if (FantasticConfig.entityRenderFixer) {
            MinecraftForge.EVENT_BUS.register(EntityRenderFixer.class);
         }

         MinecraftForge.EVENT_BUS.register(TooltipFixer.class);
         MinecraftForge.EVENT_BUS.register(TooltipAlterer.class);
         if (DEV_ENV) {
            MinecraftForge.EVENT_BUS.register(TestGUI.class);
         }
      }

      MinecraftForge.EVENT_BUS.register(PlayerData.class);
   }

   @SubscribeEvent
   public static void saveConfig(OnConfigChangedEvent event) {
      if (event.getModID().equals("fantasticlib")) {
         ConfigManager.sync("fantasticlib", Type.INSTANCE);
      }

   }

   @SubscribeEvent
   public static void syncConfig(PostConfigChangedEvent event) {
      if (event.getModID().equals("fantasticlib")) {
         ImprovedRayTracing.reloadConfigs();
      }

   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      PlayerData.load();
      CapabilityManager.INSTANCE.register(INBTCap.class, new NBTCapStorage(), () -> {
         return null;
      });
      if (event.getSide() == Side.CLIENT) {
         Render.init();
      }

   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      CModpackDataHandler.load(event);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      Compat.betterportals = Loader.isModLoaded("betterportals");
      Compat.smoothfont = Loader.isModLoaded("smoothfont");
      Compat.baubles = Loader.isModLoaded("baubles");
      Compat.tiamatinventory = Loader.isModLoaded("tiamatinventory");
      ImprovedRayTracing.reloadConfigs();
      DataFiles.output();
   }

   @EventHandler
   public static void serverAboutToStart(FMLServerAboutToStartEvent event) {
      MCTools.serverStart(event);
   }

   @EventHandler
   public static void serverStarting(FMLServerStartingEvent event) {
      CWorldDataHandler.load(event);
      event.registerServerCommand(new Commands());
      event.registerServerCommand(new CmdGive());
   }

   @EventHandler
   public static void serverStarted(FMLServerStartedEvent event) {
      serverStartTime = System.nanoTime();
   }

   @EventHandler
   public static void serverStopped(FMLServerStoppedEvent event) {
      serverStartTime = -1L;
      MCTools.serverStop(event);
      CWorldDataHandler.clear(event);
      GametypeChangedEvent.PLAYER_GAMETYPES.clear();
   }

   @SubscribeEvent
   public static void inventorySyncFix(InventoryChangedEvent event) {
      if (FantasticConfig.inventoryDesyncFixer && event.getEntity() instanceof EntityPlayerMP) {
         EntityPlayerMP player = (EntityPlayerMP)event.getEntity();
         if (!player.isCreative()) {
            player.sendAllContents(player.inventoryContainer, player.inventoryContainer.getInventory());
            player.inventoryContainer.detectAndSendChanges();
            if (player.openContainer != null && player.openContainer != player.inventoryContainer) {
               player.sendAllContents(player.openContainer, player.openContainer.getInventory());
               player.openContainer.detectAndSendChanges();
            }
         }
      }

   }

   static {
      ColorImmutable.init();
   }
}
