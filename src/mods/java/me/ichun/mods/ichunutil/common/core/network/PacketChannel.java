package me.ichun.mods.ichunutil.common.core.network;

import com.google.common.base.Throwables;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.util.ReportedException;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.FMLOutboundHandler.OutboundTarget;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class PacketChannel {
   private EnumMap<Side, FMLEmbeddedChannel> channels;
   private PacketChannel.AbstractPacketCodec packetCodec;
   private static Class<?> defaultChannelPipeline;
   private static Method generateName;

   private String generateName(ChannelPipeline pipeline, ChannelHandler handler) {
      try {
         return (String)generateName.invoke(defaultChannelPipeline.cast(pipeline), handler);
      } catch (Exception var4) {
         FMLLog.log(Level.FATAL, var4, "It appears we somehow have a not-standard pipeline. Huh", new Object[0]);
         throw Throwables.propagate(var4);
      }
   }

   public PacketChannel(String modId, Class<? extends AbstractPacket>... packetTypes) {
      try {
         defaultChannelPipeline = Class.forName("io.netty.channel.DefaultChannelPipeline");
         generateName = defaultChannelPipeline.getDeclaredMethod("generateName", ChannelHandler.class);
         generateName.setAccessible(true);
      } catch (Exception var8) {
         FMLLog.log(Level.FATAL, var8, "What? Netty isn't installed, what magic is this?", new Object[0]);
         throw Throwables.propagate(var8);
      }

      if (modId.length() > 20) {
         throw new ReportedException(new CrashReport("Mod " + modId + " has a channel name that's too long!", new Throwable()));
      } else if (packetTypes.length == 0) {
         throw new ReportedException(new CrashReport("Mod " + modId + " is not registering any packets with its channel handlers.", new Throwable()));
      } else {
         this.packetCodec = new PacketChannel.AbstractPacketCodec();
         this.channels = NetworkRegistry.INSTANCE.newChannel(modId, new ChannelHandler[]{this.packetCodec});
         ArrayList<Class<? extends AbstractPacket>> list = new ArrayList();

         for(Class<? extends AbstractPacket> packetType : packetTypes) {
            if (!list.contains(packetType)) {
               list.add(packetType);
            } else {
               iChunUtil.LOGGER.warn("Channel " + modId + " is reregistering packet types!");
            }
         }

         for(int i = 0; i < list.size(); ++i) {
            this.registerPacket(modId, i, (Class)list.get(i));
         }

      }
   }

   public void registerPacket(String modId, int i, Class<? extends AbstractPacket> packetClass) {
      this.packetCodec.addDiscriminator(i, packetClass);

      try {
         Side side = ((AbstractPacket)packetClass.newInstance()).receivingSide();
         if (side == null) {
            FMLEmbeddedChannel channelServer = (FMLEmbeddedChannel)this.channels.get(Side.SERVER);
            String typeServer = channelServer.findChannelHandlerNameForType(PacketChannel.AbstractPacketCodec.class);
            PacketHandlerWrapper handlerServer = new PacketHandlerWrapper(Side.SERVER, packetClass);
            channelServer.pipeline().addAfter(typeServer, this.generateName(channelServer.pipeline(), handlerServer), handlerServer);
            FMLEmbeddedChannel channelClient = (FMLEmbeddedChannel)this.channels.get(Side.CLIENT);
            String typeClient = channelClient.findChannelHandlerNameForType(PacketChannel.AbstractPacketCodec.class);
            PacketHandlerWrapper handlerClient = new PacketHandlerWrapper(Side.CLIENT, packetClass);
            channelClient.pipeline().addAfter(typeClient, this.generateName(channelClient.pipeline(), handlerClient), handlerClient);
         } else {
            FMLEmbeddedChannel channel = (FMLEmbeddedChannel)this.channels.get(side);
            String type = channel.findChannelHandlerNameForType(PacketChannel.AbstractPacketCodec.class);
            PacketHandlerWrapper handler = new PacketHandlerWrapper(side, packetClass);
            channel.pipeline().addAfter(type, this.generateName(channel.pipeline(), handler), handler);
         }

      } catch (Exception var11) {
         iChunUtil.LOGGER.warn("Could not create packet for class " + packetClass.getName() + " for channel " + modId + ". Is there a default constructor in that class?");
         throw Throwables.propagate(var11);
      }
   }

   public Packet<?> getPacketFrom(AbstractPacket message) {
      return ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).generatePacketFrom(message);
   }

   public void sendToAllExcept(AbstractPacket packet, EntityPlayer player) {
      for(int i = 0; i < FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().size(); ++i) {
         EntityPlayerMP player1 = (EntityPlayerMP)FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().get(i);
         if (!player.getName().equalsIgnoreCase(player1.getName())) {
            this.sendTo(packet, player1);
         }
      }

   }

   public void sendToAll(AbstractPacket message) {
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.ALL);
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
   }

   public void sendTo(AbstractPacket message, EntityPlayer player) {
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.PLAYER);
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
   }

   public void sendToAllAround(AbstractPacket message, TargetPoint point) {
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.ALLAROUNDPOINT);
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
   }

   public void sendToDimension(AbstractPacket message, int dimensionId) {
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.DIMENSION);
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(Integer.valueOf(dimensionId));
      ((FMLEmbeddedChannel)this.channels.get(Side.SERVER)).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
   }

   public void sendToServer(AbstractPacket message) {
      ((FMLEmbeddedChannel)this.channels.get(Side.CLIENT)).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.TOSERVER);
      ((FMLEmbeddedChannel)this.channels.get(Side.CLIENT)).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
   }

   public class AbstractPacketCodec extends FMLIndexedMessageToMessageCodec<AbstractPacket> {
      public AbstractPacketCodec() {
      }

      public void encodeInto(ChannelHandlerContext ctx, AbstractPacket msg, ByteBuf target) throws Exception {
         msg.writeTo(target);
      }

      public void decodeInto(ChannelHandlerContext ctx, ByteBuf source, AbstractPacket msg) {
         msg.readFrom(source);
      }
   }
}
