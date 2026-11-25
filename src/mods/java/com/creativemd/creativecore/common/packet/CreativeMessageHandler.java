package com.creativemd.creativecore.common.packet;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public class CreativeMessageHandler implements IMessage {
   public boolean isLast = true;
   public EaglercraftUUID uuid;
   public CreativeCorePacket packet = null;
   public CreativeMessageHandler.MessageType type;
   public EntityPlayer player;
   public int amount;
   public ByteBuf content;

   public CreativeMessageHandler() {
   }

   public CreativeMessageHandler(CreativeCorePacket packet, CreativeMessageHandler.MessageType type, EntityPlayer player) {
      this.packet = packet;
      this.type = type;
      this.player = player;
   }

   public void fromBytes(ByteBuf buf) {
      this.isLast = buf.readBoolean();
      int id = buf.readInt();
      Class PacketClass = CreativeCorePacket.getClass(id);
      this.packet = null;

      try {
         this.packet = (CreativeCorePacket)PacketClass.getConstructor().newInstance();
      } catch (Exception var6) {
         System.out.println("Invalid packet id=" + id);
      }

      if (this.isLast) {
         if (this.packet != null) {
            this.packet.readBytes(buf);
         }
      } else {
         this.amount = buf.readInt();
         this.uuid = EaglercraftUUID.fromString(CreativeCorePacket.readString(buf));
         int length = buf.readInt();
         this.content = ByteBufAllocator.DEFAULT.directBuffer();
         byte[] data = new byte[length];
         buf.readBytes(data);
         this.content.writeBytes(data);
      }

   }

   public void toBytes(ByteBuf buf) {
      this.content = ByteBufAllocator.DEFAULT.directBuffer();
      this.packet.writeBytes(this.content);
      int packetSize = 31767;
      if (this.type.getSide().isServer()) {
         packetSize = CreativeCorePacket.maxPacketSize;
      }

      if (packetSize > this.content.writerIndex()) {
         buf.writeBoolean(true);
         buf.writeInt(CreativeCorePacket.getId(this.packet));
         buf.writeBytes(this.content);
      } else {
         this.amount = (int)Math.ceil((double)this.content.writerIndex() / (double)packetSize);
         this.uuid = EaglercraftUUID.randomUUID();
         int id = CreativeCorePacket.getId(this.packet);

         for(int i = 0; i < this.amount; ++i) {
            int length = Math.min(packetSize, this.content.writerIndex() - i * packetSize);
            if (i == 0) {
               buf.writeBoolean(false);
               buf.writeInt(id);
               buf.writeInt(this.amount);
               CreativeCorePacket.writeString(buf, this.uuid.toString());
               buf.writeInt(length);
               buf.writeBytes(this.content, 0, length);
            } else {
               CreativeSplittedMessageHandler splitted = new CreativeSplittedMessageHandler(i == this.amount - 1, id, this.uuid, this.content, i * packetSize, length);
               splitted.type = this.type;
               PacketHandler.addQueueMessage(splitted);
            }
         }
      }

   }

   public static enum MessageType {
      ToServer {
         public Side getSide() {
            return Side.CLIENT;
         }
      },
      ToPlayer {
         public Side getSide() {
            return Side.SERVER;
         }
      },
      ToAllPlayer {
         public Side getSide() {
            return Side.SERVER;
         }
      };

      private MessageType() {
      }

      public abstract Side getSide();
   }
}
