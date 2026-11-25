package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePlayerControl implements IMessage {
   public byte controlStates;

   public MessagePlayerControl() {
   }

   public MessagePlayerControl(byte controlStates) {
      this.controlStates = controlStates;
   }

   public static void onMessage(MessagePlayerControl message, MessageContext ctx, EntityPlayer player) {
      ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
      playerExt.updateControlStates(message.controlStates);
   }

   public void fromBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);
      this.controlStates = packet.readByte();
   }

   public void toBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);
      packet.writeByte(this.controlStates);
   }
}
