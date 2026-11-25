package com.teamacronymcoders.base.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
   private SimpleNetworkWrapper networkWrapper;
   private int id = -1;

   public PacketHandler(String modid) {
      this.networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(modid);
   }

   public <I extends IMessage, O extends IMessage> void registerPacket(Class<? extends IMessageHandler<I, O>> messageHandler, Class<I> requestMessageType, Side side) {
      this.networkWrapper.registerMessage(messageHandler, requestMessageType, ++this.id, side);
   }

   public <I extends IMessage, O extends IMessage> void registerPacket(IMessageHandler<I, O> messageHandler, Class<I> messageClass, Side side) {
      this.networkWrapper.registerMessage(messageHandler, messageClass, ++this.id, side);
   }

   public void sendToAllAround(IMessage message, Entity entity) {
      TargetPoint targetPoint = new TargetPoint(entity.dimension, entity.posX, entity.posY, entity.posZ, 64.0D);
      this.sendToAllAround(message, targetPoint);
   }

   public void sendToAllAround(IMessage message, BlockPos blockPos, int dimensionId) {
      TargetPoint targetPoint = new TargetPoint(dimensionId, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), 64.0D);
      this.sendToAllAround(message, targetPoint);
   }

   public void sendToPlayer(IMessage message, EntityPlayerMP entityPlayer) {
      this.networkWrapper.sendTo(message, entityPlayer);
   }

   public void sendToAllAround(IMessage message, TargetPoint targetPoint) {
      this.networkWrapper.sendToAllAround(message, targetPoint);
   }

   public void sendToServer(IMessage message) {
      this.networkWrapper.sendToServer(message);
   }

   public void sendToDimension(IMessage message, int dimensionId) {
      this.networkWrapper.sendToDimension(message, dimensionId);
   }
}
