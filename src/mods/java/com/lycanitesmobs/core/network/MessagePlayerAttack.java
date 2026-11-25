package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.entity.ExtendedPlayer;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessagePlayerAttack implements IMessage {
   public int attackEntityID = 0;

   public MessagePlayerAttack() {
   }

   public MessagePlayerAttack(Entity attackEntity) {
      if (attackEntity != null) {
         this.attackEntityID = attackEntity.getEntityId();
      }

   }

   public static void onMessage(MessagePlayerAttack message, MessageContext ctx, EntityPlayer player) {
      ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
      if (message.attackEntityID != 0) {
         playerExt.meleeAttack(player.getEntityWorld().getEntityByID(message.attackEntityID));
      }

   }

   public void fromBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);
      this.attackEntityID = packet.readInt();
   }

   public void toBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);
      packet.writeInt(this.attackEntityID);
   }
}
