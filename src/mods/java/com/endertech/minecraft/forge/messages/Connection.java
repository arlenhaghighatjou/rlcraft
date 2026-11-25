package com.endertech.minecraft.forge.messages;

import com.endertech.minecraft.forge.world.ChunkLoc;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class Connection {
   private final SimpleNetworkWrapper channel;

   public Connection(SimpleNetworkWrapper channel) {
      this.channel = channel;
   }

   public void sendToPlayer(IMessage message, EntityPlayerMP player) {
      this.getChannel().sendTo(message, player);
   }

   public void sendToServer(IMessage message) {
      this.getChannel().sendToServer(message);
   }

   public void sendToAll(IMessage message) {
      this.getChannel().sendToAll(message);
   }

   public void sendToDimension(IMessage message, int dimensionId) {
      this.getChannel().sendToDimension(message, dimensionId);
   }

   public void sendToAllAround(IMessage message, World world, BlockPos pos, int radius) {
      TargetPoint point = new TargetPoint(world.provider.getDimension(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)radius);
      this.getChannel().sendToAllAround(message, point);
   }

   public void sendToAllInChunk(IMessage message, ChunkLoc location) {
      MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

      for(EntityPlayerMP player : server.getPlayerList().getPlayers()) {
         if (location.contains(player)) {
            this.sendToPlayer(message, player);
         }
      }

   }

   protected SimpleNetworkWrapper getChannel() {
      return this.channel;
   }
}
