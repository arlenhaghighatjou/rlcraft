package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageGUIRequest implements IMessage {
   public byte guiID;

   public MessageGUIRequest() {
   }

   public MessageGUIRequest(byte guiID) {
      this.guiID = guiID;
   }

   public static void onMessage(MessageGUIRequest message, MessageContext ctx, EntityPlayer player) {
      ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
      playerExt.requestGUI(message.guiID);
   }

   public void fromBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);
      this.guiID = packet.readByte();
   }

   public void toBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);
      packet.writeByte(this.guiID);
   }
}
