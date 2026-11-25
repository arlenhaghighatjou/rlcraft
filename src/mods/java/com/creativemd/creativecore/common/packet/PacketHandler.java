package com.creativemd.creativecore.common.packet;

import com.creativemd.creativecore.CreativeCore;
import com.creativemd.creativecore.common.world.IOrientatedWorld;
import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketHandler {
   private static ArrayList<CreativeSplittedMessageHandler> queuedMessages = new ArrayList();

   public PacketHandler() {
   }

   public static void addQueueMessage(CreativeSplittedMessageHandler message) {
      queuedMessages.add(message);
   }

   private static void sendQueuedMessage(EntityPlayer player) {
      for(int i = 0; i < queuedMessages.size(); ++i) {
         sendMessage(((CreativeSplittedMessageHandler)queuedMessages.get(i)).type, player, (IMessage)queuedMessages.get(i));
      }

      queuedMessages.clear();
   }

   public static void sendMessage(CreativeMessageHandler.MessageType type, EntityPlayer player, IMessage message) {
      switch(type) {
      case ToAllPlayer:
         CreativeCore.network.sendToAll(message);
         break;
      case ToPlayer:
         CreativeCore.network.sendTo(message, (EntityPlayerMP)player);
         break;
      case ToServer:
         CreativeCore.network.sendToServer(message);
      }

   }

   public static void sendPacketToAllPlayers(CreativeCorePacket packet) {
      CreativeCore.network.sendToAll(new CreativeMessageHandler(packet, CreativeMessageHandler.MessageType.ToAllPlayer, (EntityPlayer)null));
      sendQueuedMessage((EntityPlayer)null);
   }

   public static void sendPacketToServer(CreativeCorePacket packet) {
      CreativeCore.network.sendToServer(new CreativeMessageHandler(packet, CreativeMessageHandler.MessageType.ToServer, (EntityPlayer)null));
      sendQueuedMessage((EntityPlayer)null);
   }

   public static void sendPacketsToAllPlayers(List<CreativeCorePacket> packets) {
      for(int i = 0; i < packets.size(); ++i) {
         sendPacketToAllPlayers((CreativeCorePacket)packets.get(i));
      }

   }

   public static void sendPacketToNearPlayers(World world, CreativeCorePacket packet, final int distance, final BlockPos pos) {
      for(EntityPlayerMP entityplayermp : world.getPlayers(EntityPlayerMP.class, new Predicate<EntityPlayerMP>() {
         public boolean apply(@Nullable EntityPlayerMP p_apply_1_) {
            return p_apply_1_.getDistanceSq(pos) < Math.pow((double)distance, 2.0D);
         }
      })) {
         sendPacketToPlayer(packet, entityplayermp);
      }

   }

   public static void sendPacketToPlayer(CreativeCorePacket packet, EntityPlayerMP player) {
      CreativeCore.network.sendTo(new CreativeMessageHandler(packet, CreativeMessageHandler.MessageType.ToPlayer, player), player);
      sendQueuedMessage(player);
   }

   public static void sendPacketToPlayers(CreativeCorePacket packet, Iterable<? extends EntityPlayer> players) {
      for(EntityPlayer player : players) {
         sendPacketToPlayer(packet, (EntityPlayerMP)player);
      }

   }

   public static void sendPacketToPlayers(CreativeCorePacket packet, Iterable<? extends EntityPlayer> players, @Nullable Predicate<EntityPlayer> predicate) {
      for(EntityPlayer player : players) {
         if (predicate == null || predicate.apply(player)) {
            sendPacketToPlayer(packet, (EntityPlayerMP)player);
         }
      }

   }

   public static void sendPacketToTrackingPlayers(CreativeCorePacket packet, World world, BlockPos pos, @Nullable Predicate<EntityPlayer> predicate) {
      if (world instanceof IOrientatedWorld) {
         sendPacketToTrackingPlayers(packet, ((IOrientatedWorld)world).getTopEntity(), predicate);
      } else {
         try {
            PlayerChunkMapEntry entry = ((WorldServer)world).getPlayerChunkMap().getEntry(pos.getX() >> 4, pos.getZ() >> 4);
            if (entry != null) {
               sendPacketToPlayers(packet, entry.getWatchingPlayers(), predicate);
            }
         } catch (NullPointerException var5) {
            var5.printStackTrace();
         }
      }

   }

   public static void sendPacketToTrackingPlayers(CreativeCorePacket packet, Entity entity, @Nullable Predicate<EntityPlayer> predicate) {
      World world = entity.world;
      if (world instanceof IOrientatedWorld) {
         sendPacketToTrackingPlayers(packet, ((IOrientatedWorld)world).getTopEntity(), predicate);
      } else {
         for(EntityPlayer player : ((WorldServer)world).getEntityTracker().getTrackingPlayers(entity)) {
            if (predicate == null || predicate.apply(player)) {
               sendPacketToPlayer(packet, (EntityPlayerMP)player);
            }
         }
      }

   }

   public static void sendPacketToTrackingPlayers(CreativeCorePacket packet, EntityPlayerMP player) {
      for(EntityPlayer entityPlayer : player.getServerWorld().getEntityTracker().getTrackingPlayers(player)) {
         sendPacketToPlayer(packet, (EntityPlayerMP)entityPlayer);
      }

   }
}
