package com.fantasticsource.setbonus;

import com.fantasticsource.mctools.ClientTickTimer;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.ServerTickTimer;
import com.fantasticsource.setbonus.client.BonusScreen;
import com.fantasticsource.setbonus.client.ClientBonus;
import com.fantasticsource.setbonus.client.ClientData;
import com.fantasticsource.setbonus.client.TooltipRenderer;
import com.fantasticsource.setbonus.common.Commands;
import com.fantasticsource.setbonus.common.Network;
import com.fantasticsource.setbonus.config.ConfigHandler;
import com.fantasticsource.setbonus.server.ServerBonus;
import com.fantasticsource.setbonus.server.ServerData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.PostConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(
   modid = "setbonus",
   name = "Set Bonus",
   version = "1.12.2.020",
   dependencies = "required-after:fantasticlib@[1.12.2.030,)",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class SetBonus {
   public static final String MODID = "setbonus";
   public static final String NAME = "Set Bonus";
   public static final String VERSION = "1.12.2.020";
   public static final String CONFIG_VERSION = "1.12.2.009";

   public SetBonus() {
   }

   @EventHandler
   public static void preInit(FMLPreInitializationEvent event) {
      Network.init();
      MinecraftForge.EVENT_BUS.register(SetBonus.class);
      MinecraftForge.EVENT_BUS.register(ServerTickTimer.class);
      if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
         MinecraftForge.EVENT_BUS.register(ClientTickTimer.class);
         MinecraftForge.EVENT_BUS.register(TooltipRenderer.class);
         MinecraftForge.EVENT_BUS.register(BonusScreen.class);
      }

   }

   @EventHandler
   public static void serverStarting(FMLServerStartingEvent event) {
      event.registerServerCommand(new Commands());
      ServerData.update();
   }

   @EventHandler
   public static void serverStop(FMLServerStoppingEvent event) {
      ServerBonus.dropAll();
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public static void saveConfig(OnConfigChangedEvent event) {
      if (event.getModID().equals("setbonus")) {
         ConfigManager.sync("setbonus", Type.INSTANCE);
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public static void calcConfigs(PostConfigChangedEvent event) {
      if (event.isWorldRunning()) {
         if (MCTools.hosting()) {
            ServerData.update();
            EntityPlayer localPlayer = Minecraft.getMinecraft().player;
            World world = localPlayer.world;

            for(EntityPlayer player : world.playerEntities) {
               if (player != localPlayer) {
                  Network.updateConfig((EntityPlayerMP)player);
               }
            }
         }
      } else {
         ServerData.update();
      }

   }

   @SubscribeEvent
   public static void playerTick(PlayerTickEvent event) {
      if (event.phase == Phase.START) {
         if (event.side == Side.SERVER) {
            ServerBonus.updateBonuses((EntityPlayerMP)event.player);
         } else {
            EntityPlayer player = event.player;
            if (Minecraft.getMinecraft().player == player) {
               ClientBonus.updateBonuses(player);
            }
         }
      }

   }

   @SubscribeEvent
   public static void playerConnect(EntityJoinWorldEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof EntityPlayerMP) {
         EntityPlayerMP player = (EntityPlayerMP)entity;
         ServerBonus.loadDiscoveries(player);
         Network.updateConfig(player);
      }

   }

   @SubscribeEvent
   public static void playerDisconnect(PlayerLoggedOutEvent event) {
      ServerBonus.clearMem(event.player);
   }

   @SubscribeEvent
   public static void disconnectFromServer(ClientDisconnectionFromServerEvent event) {
      ClientData.clear();
   }

   static {
      ConfigHandler.init();
   }
}
