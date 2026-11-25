package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePlayerStats implements IMessage {
   public int spirit;
   public int summonFocus;
   public int creatureStudyCooldown;

   public MessagePlayerStats() {
   }

   public MessagePlayerStats(ExtendedPlayer playerExt) {
      this.spirit = playerExt.spirit;
      this.summonFocus = playerExt.summonFocus;
      this.creatureStudyCooldown = playerExt.creatureStudyCooldown;
   }

   public static void onMessage(MessagePlayerStats message, MessageContext ctx, EntityPlayer player) {
      ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
      if (playerExt != null) {
         playerExt.spirit = message.spirit;
         playerExt.summonFocus = message.summonFocus;
         playerExt.creatureStudyCooldown = message.creatureStudyCooldown;
      }
   }

   public void fromBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);
      this.spirit = packet.readInt();
      this.summonFocus = packet.readInt();
      this.creatureStudyCooldown = packet.readInt();
   }

   public void toBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);
      packet.writeInt(this.spirit);
      packet.writeInt(this.summonFocus);
      packet.writeInt(this.creatureStudyCooldown);
   }
}
