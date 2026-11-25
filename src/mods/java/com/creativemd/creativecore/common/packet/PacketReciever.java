package com.creativemd.creativecore.common.packet;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketReciever implements IMessageHandler<CreativeMessageHandler, IMessage> {
   public static HashMap<PacketReciever.PacketKey, PacketReciever.PacketValue> splittedPackets = new HashMap();
   public static ArrayList<CreativeSplittedMessageHandler> packetsToSend = new ArrayList();
   @SideOnly(Side.CLIENT)
   public static HashMap<PacketReciever.PacketKey, PacketReciever.PacketValue> clientSplittedPackets;
   @SideOnly(Side.CLIENT)
   public static ArrayList<CreativeSplittedMessageHandler> clientPacketsToSend;

   public PacketReciever() {
   }

   @SideOnly(Side.CLIENT)
   public static void initClient() {
      clientSplittedPackets = new HashMap();
      clientPacketsToSend = new ArrayList();
   }

   public static void refreshQueue(boolean isServer) {
      if (!isServer) {
         refreshQueueClient();
      } else {
         if (splittedPackets.isEmpty()) {
            return;
         }

         Iterator<Entry<PacketReciever.PacketKey, PacketReciever.PacketValue>> iterator = splittedPackets.entrySet().iterator();

         while(iterator.hasNext()) {
            Entry<PacketReciever.PacketKey, PacketReciever.PacketValue> entry = (Entry)iterator.next();
            if (((PacketReciever.PacketValue)entry.getValue()).isExpired()) {
               System.out.println("Packet parts expired " + (((PacketReciever.PacketValue)entry.getValue()).received + 1) + "/" + ((PacketReciever.PacketValue)entry.getValue()).amount);
               iterator.remove();
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public static void refreshQueueClient() {
      if (!clientSplittedPackets.isEmpty()) {
         Iterator<Entry<PacketReciever.PacketKey, PacketReciever.PacketValue>> iterator = clientSplittedPackets.entrySet().iterator();

         while(iterator.hasNext()) {
            Entry<PacketReciever.PacketKey, PacketReciever.PacketValue> entry = (Entry)iterator.next();
            if (((PacketReciever.PacketValue)entry.getValue()).isExpired()) {
               System.out.println("Packet parts expired " + (((PacketReciever.PacketValue)entry.getValue()).received + 1) + "/" + ((PacketReciever.PacketValue)entry.getValue()).amount);
               iterator.remove();
            }
         }

      }
   }

   @SideOnly(Side.CLIENT)
   public void executeClient(final IMessage message) {
      if (message instanceof CreativeMessageHandler) {
         final CreativeMessageHandler cm = (CreativeMessageHandler)message;
         if (!cm.isLast) {
            PacketReciever.PacketKey key = new PacketReciever.PacketKey(CreativeCorePacket.getId(cm.packet), cm.uuid);
            if (clientSplittedPackets.containsKey(key)) {
               System.out.println("Something went wrong! Received another packet of the same type with the same uuid id! " + key);
            } else {
               clientSplittedPackets.put(key, new PacketReciever.PacketValue(cm.content, cm.uuid, cm.packet, cm.amount));
            }
         } else if (cm.packet != null) {
            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
               public void run() {
                  ((CreativeMessageHandler)message).packet.executeClient(Minecraft.getMinecraft().player);
                  if (cm.content != null) {
                     cm.content.release();
                  }

               }
            });
         }
      }

   }

   public CreativeMessageHandler onMessage(final CreativeMessageHandler message, final MessageContext ctx) {
      if (ctx.side.isClient()) {
         this.executeClient(message);
      } else if (message instanceof CreativeMessageHandler) {
         if (!message.isLast) {
            PacketReciever.PacketKey key = new PacketReciever.PacketKey(CreativeCorePacket.getId(message.packet), message.uuid);
            if (splittedPackets.containsKey(key)) {
               System.out.println("Something went wrong! Received another packet of the same type with the same uuid id! " + key);
            } else {
               splittedPackets.put(key, new PacketReciever.PacketValue(message.content, message.uuid, message.packet, message.amount));
            }
         } else if (message.packet != null) {
            ctx.getServerHandler().player.getServer().addScheduledTask(new Runnable() {
               public void run() {
                  message.packet.executeServer(ctx.getServerHandler().player);
                  if (message.content != null) {
                     message.content.release();
                  }

               }
            });
         }
      }

      return null;
   }

   static {
      if (FMLCommonHandler.instance().getSide().isClient()) {
         initClient();
      }

   }

   public static class PacketKey {
      public final int packetId;
      public final UUID uuid;

      public PacketKey(int packetId, UUID uuid) {
         this.packetId = packetId;
         this.uuid = uuid;
      }

      public int hashCode() {
         return this.uuid.hashCode();
      }

      public boolean equals(Object object) {
         if (!(object instanceof PacketReciever.PacketKey)) {
            return false;
         } else {
            return ((PacketReciever.PacketKey)object).packetId == this.packetId && ((PacketReciever.PacketKey)object).uuid.equals(this.uuid);
         }
      }

      public String toString() {
         return "[id=" + this.packetId + ",uuid=" + this.uuid + "]";
      }
   }

   public static class PacketValue {
      public static long timeToWait = 60000L;
      public final ByteBuf buf;
      public final UUID uuid;
      public final CreativeCorePacket packet;
      public final int amount;
      public int received;
      public long lastReceivedPart = System.currentTimeMillis();

      public PacketValue(ByteBuf buf, UUID uuid, CreativeCorePacket packet, int amount) {
         this.buf = buf;
         this.packet = packet;
         this.amount = amount;
         this.uuid = uuid;
      }

      public boolean isComplete() {
         return this.received == this.amount - 1;
      }

      public boolean isExpired() {
         return System.currentTimeMillis() - this.lastReceivedPart > timeToWait;
      }

      public void receivePacket(ByteBuf toRead, int index, int length) throws IllegalAccessException {
         ++this.received;
         if (this.received >= this.amount) {
            throw new IllegalAccessException("This packet received more parts than it should! packetID=" + CreativeCorePacket.getId(this.packet) + ", uuid=" + this.uuid);
         } else {
            this.buf.writeBytes(toRead, index, length);
            this.lastReceivedPart = System.nanoTime();
            toRead.release();
         }
      }
   }
}
