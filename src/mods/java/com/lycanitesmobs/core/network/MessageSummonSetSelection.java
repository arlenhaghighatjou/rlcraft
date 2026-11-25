package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageSummonSetSelection implements IMessage {
   public byte summonSetID;

   public MessageSummonSetSelection() {
   }

   public MessageSummonSetSelection(ExtendedPlayer playerExt) {
      this.summonSetID = (byte)playerExt.selectedSummonSet;
   }

   public static void onMessage(MessageSummonSetSelection message, MessageContext ctx, EntityPlayer player) {
      if (ctx.side == Side.SERVER) {
         ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
         playerExt.setSelectedSummonSet(message.summonSetID);
      } else {
         ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
         if (playerExt == null) {
            return;
         }

         playerExt.setSelectedSummonSet(message.summonSetID);
      }

   }

   public void fromBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);
      this.summonSetID = packet.readByte();
   }

   public void toBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);
      packet.writeByte(this.summonSetID);
   }
}
