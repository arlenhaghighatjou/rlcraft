package com.tmtravlr.qualitytools.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SToCMessage implements IMessage {
   private byte[] data;

   public SToCMessage() {
      this(new byte[]{0});
   }

   public SToCMessage(ByteBuf dataToSet) {
      this(dataToSet.array());
   }

   public SToCMessage(byte[] dataToSet) {
      if (dataToSet.length > 2097136) {
         throw new IllegalArgumentException("Payload may not be larger than 2097136 (0x1ffff0) bytes");
      } else {
         this.data = dataToSet;
      }
   }

   public void toBytes(ByteBuf buffer) {
      if (this.data.length > 2097136) {
         throw new IllegalArgumentException("Payload may not be larger than 2097136 (0x1ffff0) bytes");
      } else {
         buffer.writeInt(this.data.length);
         buffer.writeBytes(this.data);
      }
   }

   public void fromBytes(ByteBuf buffer) {
      this.data = new byte[buffer.readInt()];
      buffer.readBytes(this.data);
   }

   public byte[] getData() {
      return this.data;
   }
}
