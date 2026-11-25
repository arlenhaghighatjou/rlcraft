package com.creativemd.creativecore.common.packet;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CreativeSplittedMessageHandler implements IMessage {
   public boolean isLast;
   public int packetId;
   public EaglercraftUUID uuid;
   public ByteBuf buffer;
   public int index;
   public int length;
   public CreativeMessageHandler.MessageType type;

   public CreativeSplittedMessageHandler() {
   }

   public CreativeSplittedMessageHandler(boolean isLast, int packetId, EaglercraftUUID uuid, ByteBuf buffer, int index, int length) {
      this.isLast = isLast;
      this.buffer = buffer;
      this.uuid = uuid;
      this.index = index;
      this.length = length;
      this.packetId = packetId;
   }

   public void fromBytes(ByteBuf buf) {
      this.isLast = buf.readBoolean();
      this.packetId = buf.readInt();
      this.uuid = EaglercraftUUID.fromString(CreativeCorePacket.readString(buf));
      this.buffer = ByteBufAllocator.DEFAULT.directBuffer();
      this.length = buf.readInt();
      byte[] data = new byte[this.length];
      buf.readBytes(data);
      this.buffer.writeBytes(data);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeBoolean(this.isLast);
      buf.writeInt(this.packetId);
      CreativeCorePacket.writeString(buf, this.uuid.toString());
      buf.writeInt(this.length);
      buf.writeBytes(this.buffer, this.index, this.length);
   }
}
