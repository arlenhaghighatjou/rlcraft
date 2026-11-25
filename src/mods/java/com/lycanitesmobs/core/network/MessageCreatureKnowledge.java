package com.lycanitesmobs.core.network;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageCreatureKnowledge implements IMessage {
   public String creatureName;
   public int rank;
   public int experience;

   public MessageCreatureKnowledge() {
   }

   public MessageCreatureKnowledge(CreatureKnowledge creatureKnowledge) {
      this.creatureName = creatureKnowledge.creatureName;
      this.rank = creatureKnowledge.rank;
      this.experience = creatureKnowledge.experience;
   }

   public static void onMessage(MessageCreatureKnowledge message, MessageContext ctx, EntityPlayer player) {
      ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
      if (playerExt != null) {
         playerExt.beastiary.addCreatureKnowledge(new CreatureKnowledge(playerExt.beastiary, message.creatureName, message.rank, message.experience), false);
      }
   }

   public void fromBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);

      try {
         this.creatureName = packet.readString(256);
         this.rank = packet.readInt();
         this.experience = packet.readInt();
      } catch (Exception var4) {
         LycanitesMobs.logWarning("", "There was a problem decoding the packet: " + packet + ".");
         var4.printStackTrace();
      }

   }

   public void toBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);
      packet.writeString(this.creatureName);
      packet.writeInt(this.rank);
      packet.writeInt(this.experience);
   }
}
