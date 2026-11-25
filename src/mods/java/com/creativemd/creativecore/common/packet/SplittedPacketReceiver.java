package com.creativemd.creativecore.common.packet;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SplittedPacketReceiver implements IMessageHandler<CreativeSplittedMessageHandler, IMessage> {
   public SplittedPacketReceiver() {
   }

   @SideOnly(Side.CLIENT)
   public void executeClient(IMessage message) {
      if (message instanceof CreativeSplittedMessageHandler) {
         CreativeSplittedMessageHandler cm = (CreativeSplittedMessageHandler)message;
         PacketReciever.PacketKey key = new PacketReciever.PacketKey(cm.packetId, cm.uuid);
         final PacketReciever.PacketValue value = (PacketReciever.PacketValue)PacketReciever.clientSplittedPackets.get(key);
         if (value == null) {
            System.out.println("Something went wrong! Either a packet got lost or the receiving time has expired. " + key);
            return;
         }

         try {
            value.receivePacket(cm.buffer, 0, cm.length);
         } catch (IllegalAccessException var6) {
            var6.printStackTrace();
         }

         if (cm.isLast) {
            if (value != null && value.isComplete()) {
               value.packet.readBytes(value.buf);
               Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                  public void run() {
                     value.packet.executeClient(Minecraft.getMinecraft().player);
                     value.buf.release();
                  }
               });
               PacketReciever.clientSplittedPackets.remove(key);
            } else {
               System.out.println("Something went wrong! Either a packet got lost or the receiving time has expired. " + key);
            }
         }

         PacketReciever.refreshQueue(false);
      }

   }

   public CreativeMessageHandler onMessage(CreativeSplittedMessageHandler message, final MessageContext ctx) {
      if (ctx.side.isClient()) {
         this.executeClient(message);
      } else if (message instanceof CreativeSplittedMessageHandler) {
         CreativeSplittedMessageHandler cm = message;
         PacketReciever.PacketKey key = new PacketReciever.PacketKey(message.packetId, message.uuid);
         final PacketReciever.PacketValue value = (PacketReciever.PacketValue)PacketReciever.splittedPackets.get(key);

         try {
            if (value == null) {
               System.out.println("Something went wrong! Either a packet got lost or the receiving time has expired. " + key);
               return null;
            }

            value.receivePacket(cm.buffer, 0, cm.length);
            if (cm.isLast) {
               if (value != null && value.isComplete()) {
                  value.packet.readBytes(value.buf);
                  ctx.getServerHandler().player.getServer().addScheduledTask(new Runnable() {
                     public void run() {
                        value.packet.executeServer(ctx.getServerHandler().player);
                        value.buf.release();
                     }
                  });
                  PacketReciever.splittedPackets.remove(key);
               } else {
                  System.out.println("Something went wrong! Either a packet got lost or the receiving time has expired. " + key);
               }
            }
         } catch (IllegalAccessException var7) {
            var7.printStackTrace();
         }

         PacketReciever.refreshQueue(true);
      }

      return null;
   }
}
