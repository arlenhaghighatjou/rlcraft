package ivorius.ivtoolkit.network;

import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.FMLOutboundHandler.OutboundTarget;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class IvNetworkHelperServer {
   public IvNetworkHelperServer() {
   }

   public static <UTileEntity extends TileEntity & PartialUpdateHandler> void sendTileEntityUpdatePacket(UTileEntity tileEntity, String context, SimpleNetworkWrapper network, EntityPlayer player, Object... params) {
      if (!(player instanceof EntityPlayerMP)) {
         throw new UnsupportedOperationException();
      } else {
         network.sendTo(PacketTileEntityData.packetEntityData(tileEntity, context, params), (EntityPlayerMP)player);
      }
   }

   public static <UTileEntity extends TileEntity & PartialUpdateHandler> void sendTileEntityUpdatePacket(UTileEntity tileEntity, String context, SimpleNetworkWrapper network, Object... params) {
      sendToPlayersWatchingChunk(tileEntity.getWorld(), tileEntity.getPos().getX() / 16, tileEntity.getPos().getZ() / 16, network, PacketTileEntityData.packetEntityData(tileEntity, context, params));
   }

   public static void sendToPlayersWatchingChunk(World world, int chunkX, int chunkZ, SimpleNetworkWrapper network, IMessage message) {
      for(EntityPlayerMP playerMP : getPlayersWatchingChunk(world, chunkX, chunkZ)) {
         network.sendTo(message, playerMP);
      }

   }

   public static void sendToPlayersWatchingChunk(World world, int chunkX, int chunkZ, Channel channel, Object message) {
      for(EntityPlayerMP playerMP : getPlayersWatchingChunk(world, chunkX, chunkZ)) {
         sendToPlayer(channel, playerMP, message);
      }

   }

   public static void sendToPlayersWatchingChunk(World world, int chunkX, int chunkZ, Packet packet) {
      for(EntityPlayerMP playerMP : getPlayersWatchingChunk(world, chunkX, chunkZ)) {
         playerMP.connection.sendPacket(packet);
      }

   }

   public static void sendToPlayer(Channel channel, EntityPlayerMP playerMP, Object message) {
      channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.PLAYER);
      channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(playerMP);
      channel.writeAndFlush(message);
   }

   public static List<EntityPlayerMP> getPlayersWatchingChunk(World world, int chunkX, int chunkZ) {
      if (!world.isRemote && world instanceof WorldServer) {
         ArrayList<EntityPlayerMP> playersWatching = new ArrayList();
         WorldServer server = (WorldServer)world;
         PlayerChunkMap playerManager = server.getPlayerChunkMap();
         List<EntityPlayer> players = server.playerEntities;
         List<EntityPlayerMP> mpplayers = (List)players.stream().filter((p) -> {
            return p instanceof EntityPlayerMP;
         }).collect(Collectors.toList());
         playersWatching.addAll((Collection)mpplayers.stream().filter((player) -> {
            return playerManager.isPlayerWatchingChunk(player, chunkX, chunkZ);
         }).collect(Collectors.toList()));
         return playersWatching;
      } else {
         return Collections.emptyList();
      }
   }

   public static void sendEEPUpdatePacketToPlayer(Entity entity, String capabilityKey, EnumFacing facing, String context, SimpleNetworkWrapper network, EntityPlayer player, Object... params) {
      if (!(player instanceof EntityPlayerMP)) {
         throw new UnsupportedOperationException();
      } else {
         network.sendTo(PacketEntityCapabilityData.packetEntityData(entity, capabilityKey, facing, context, params), (EntityPlayerMP)player);
      }
   }

   public static void sendEEPUpdatePacket(Entity entity, String capabilityKey, EnumFacing facing, String context, SimpleNetworkWrapper network, Object... params) {
      if (entity.world.isRemote) {
         throw new UnsupportedOperationException();
      } else {
         for(EntityPlayer player : ((WorldServer)entity.world).getEntityTracker().getTrackingPlayers(entity)) {
            sendEEPUpdatePacketToPlayer(entity, capabilityKey, facing, context, network, player, params);
         }

         if (entity instanceof EntityPlayerMP) {
            sendEEPUpdatePacketToPlayer(entity, capabilityKey, facing, context, network, (EntityPlayer)entity, params);
         }

      }
   }
}
