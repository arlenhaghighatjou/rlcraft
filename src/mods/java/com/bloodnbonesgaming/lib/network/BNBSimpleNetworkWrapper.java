package com.bloodnbonesgaming.lib.network;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import java.util.EnumMap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.FMLOutboundHandler.OutboundTarget;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleIndexedCodec;
import net.minecraftforge.fml.relauncher.Side;

public class BNBSimpleNetworkWrapper {
   private final EnumMap<Side, FMLEmbeddedChannel> channels;
   private final SimpleIndexedCodec packetCodec = new SimpleIndexedCodec();
   private final BNBSimpleChannelHandler channelHandler;

   public BNBSimpleNetworkWrapper(String channelName, Side side) {
      this.channels = NetworkRegistry.INSTANCE.newChannel(channelName, new ChannelHandler[]{this.packetCodec});
      this.channelHandler = new BNBSimpleChannelHandler(side);
   }

   public <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<REQ> requestMessageType, int discriminator, Side side) {
      this.packetCodec.addDiscriminator(discriminator, requestMessageType);
      FMLEmbeddedChannel channel = (FMLEmbeddedChannel)this.channels.get(side);
      String type = channel.findChannelHandlerNameForType(SimpleIndexedCodec.class);
      channel.pipeline().addAfter(type, "BNBLib" + discriminator, this.channelHandler);
   }

   public Packet getPacketFrom(IMessage message) {
      return ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).generatePacketFrom(message);
   }

   public void sendToAll(IMessage message) {
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.ALL);
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
   }

   public void sendTo(IMessage message, EntityPlayerMP player) {
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.PLAYER);
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
   }

   public void sendToAllAround(IMessage message, TargetPoint point) {
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.ALLAROUNDPOINT);
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
   }

   public void sendToDimension(IMessage message, int dimensionId) {
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.DIMENSION);
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(Integer.valueOf(dimensionId));
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
   }

   public void sendToServer(IMessage message) {
      ((FMLEmbeddedChannel)this.channels.get(Side.CLIENT)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.TOSERVER);
      ((FMLEmbeddedChannel)this.channels.get(Side.CLIENT)).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
   }
}
