package com.fantasticsource.mctools.event;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.datastructures.Pair;
import java.util.HashMap;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GametypeChangedEvent extends Event {
   public static final HashMap<Pair<EaglercraftUUID, Boolean>, GameType> PLAYER_GAMETYPES = new HashMap();
   public final EntityPlayer player;
   public final GameType oldGameType;
   public final GameType newGameType;

   public GametypeChangedEvent(EntityPlayer player, GameType oldGameType, GameType newGameType) {
      this.player = player;
      this.oldGameType = oldGameType;
      this.newGameType = newGameType;
   }

   @SubscribeEvent
   public static void playerLogoffServer(PlayerLoggedOutEvent event) {
      GameType oldGameType = (GameType)PLAYER_GAMETYPES.remove(new Pair(event.player.getPersistentID(), event.player instanceof EntityPlayerMP));
      if (oldGameType != null) {
         MinecraftForge.EVENT_BUS.post(new GametypeChangedEvent(event.player, oldGameType, (GameType)null));
      }

   }

   public static void serverStopped(FMLServerStoppedEvent event) {
      PLAYER_GAMETYPES.clear();
   }

   @SubscribeEvent
   public static void playerTick(PlayerTickEvent event) {
      if (event.phase != Phase.END) {
         EntityPlayer player = event.player;
         Pair<EaglercraftUUID, Boolean> pair = new Pair<EaglercraftUUID, Boolean>(player.getPersistentID(), player instanceof EntityPlayerMP);
         GameType oldGameType = (GameType)PLAYER_GAMETYPES.get(pair);
         GameType newGameType = MCTools.getGameType(player);
         if (oldGameType != newGameType) {
            PLAYER_GAMETYPES.put(pair, newGameType);
            MinecraftForge.EVENT_BUS.post(new GametypeChangedEvent(player, oldGameType, newGameType));
         }

      }
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void playerLogoffClient(ClientDisconnectionFromServerEvent event) {
      EntityPlayer player = Minecraft.getMinecraft().player;
      GameType oldGameType = (GameType)PLAYER_GAMETYPES.remove(new Pair(player.getPersistentID(), false));
      if (oldGameType != null) {
         MinecraftForge.EVENT_BUS.post(new GametypeChangedEvent(player, oldGameType, (GameType)null));
      }

   }

   static {
      MinecraftForge.EVENT_BUS.register(GametypeChangedEvent.class);
   }
}
