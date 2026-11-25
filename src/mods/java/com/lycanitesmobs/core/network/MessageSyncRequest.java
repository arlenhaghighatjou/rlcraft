package com.lycanitesmobs.core.network;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSyncRequest implements IMessage {
   public MessageSyncRequest() {
   }

   public static void onMessage(MessageSyncRequest message, MessageContext ctx, EntityPlayer player) {
      ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
      playerExt.needsFullSync = true;
      LycanitesMobs.logDebug("Packets", "Requested full player sync.");
   }

   public void fromBytes(ByteBuf buf) {
   }

   public void toBytes(ByteBuf buf) {
   }
}
