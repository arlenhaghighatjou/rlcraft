package com.lycanitesmobs.core.network;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.ExtendedPlayer;
import com.lycanitesmobs.core.info.Beastiary;
import com.lycanitesmobs.core.info.CreatureKnowledge;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageBeastiary implements IMessage {
   public int entryAmount = 0;
   public String[] creatureNames;
   public int[] ranks;
   public int[] experience;

   public MessageBeastiary() {
   }

   public MessageBeastiary(Beastiary beastiary) {
      this.entryAmount = Math.min(201, beastiary.creatureKnowledgeList.size());
      if (this.entryAmount > 0) {
         this.creatureNames = new String[this.entryAmount];
         this.ranks = new int[this.entryAmount];
         this.experience = new int[this.entryAmount];
         int i = 0;

         for(CreatureKnowledge creatureKnowledge : beastiary.creatureKnowledgeList.values()) {
            this.creatureNames[i] = creatureKnowledge.creatureName;
            this.ranks[i] = creatureKnowledge.rank;
            this.experience[i] = creatureKnowledge.experience;
            ++i;
         }
      }

   }

   public static void onMessage(MessageBeastiary message, MessageContext ctx, EntityPlayer player) {
      LycanitesMobs.logDebug("Packets", "Received Beastiary packet from server.");
      ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
      if (playerExt == null) {
         LycanitesMobs.logDebug("Packets", "Unable to find extended player for client player: " + player);
      } else {
         LycanitesMobs.logDebug("Packets", "Beastiary packet is valid.");
         playerExt.getBeastiary().creatureKnowledgeList.clear();

         for(int i = 0; i < message.entryAmount; ++i) {
            String creatureName = message.creatureNames[i];
            int rank = message.ranks[i];
            int experience = message.experience[i];
            CreatureKnowledge creatureKnowledge = new CreatureKnowledge(playerExt.getBeastiary(), creatureName, rank, experience);
            playerExt.getBeastiary().creatureKnowledgeList.put(creatureKnowledge.creatureName, creatureKnowledge);
         }

         LycanitesMobs.logDebug("Packets", "Added " + message.entryAmount + " entries from the Beastairy Packet.");
      }
   }

   public void fromBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);
      this.entryAmount = Math.min(200, packet.readInt());
      if (this.entryAmount == 200) {
         LycanitesMobs.logWarning("", "Received 200 or more creature entries, something went wrong with the Beastiary packet! Additional entries will be skipped to prevent OOM!");
      }

      if (this.entryAmount > 0) {
         this.creatureNames = new String[this.entryAmount];
         this.ranks = new int[this.entryAmount];
         this.experience = new int[this.entryAmount];

         for(int i = 0; i < this.entryAmount; ++i) {
            this.creatureNames[i] = packet.readString(32767);
            this.ranks[i] = packet.readInt();
            this.experience[i] = packet.readInt();
         }
      }

   }

   public void toBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);
      packet.writeInt(this.entryAmount);
      if (this.entryAmount > 0) {
         for(int i = 0; i < this.entryAmount; ++i) {
            packet.writeString(this.creatureNames[i]);
            packet.writeInt(this.ranks[i]);
            packet.writeInt(this.experience[i]);
         }
      }

   }
}
