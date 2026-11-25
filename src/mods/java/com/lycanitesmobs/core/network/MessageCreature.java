package com.lycanitesmobs.core.network;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.CreatureRelationshipEntry;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageCreature implements IMessage {
   int entityID;
   int playerReputation = 0;

   public MessageCreature() {
   }

   public MessageCreature(BaseCreatureEntity creatureEntity, int playerReputation) {
      this.entityID = creatureEntity.getEntityId();
      this.playerReputation = playerReputation;
   }

   public static void onMessage(MessageCreature message, MessageContext ctx, EntityPlayer player) {
      if (player != null) {
         World world = player.getEntityWorld();
         Entity entity = world.getEntityByID(message.entityID);
         if (entity instanceof BaseCreatureEntity) {
            BaseCreatureEntity creatureEntity = (BaseCreatureEntity)entity;
            CreatureRelationshipEntry relationshipEntry = creatureEntity.relationships.getOrCreateEntry(player);
            relationshipEntry.setReputation(message.playerReputation);
         }
      }
   }

   public void fromBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);

      try {
         this.entityID = packet.readInt();
         this.playerReputation = packet.readInt();
      } catch (Exception var4) {
         LycanitesMobs.logWarning("", "There was a problem decoding the packet: " + packet + ".");
         var4.printStackTrace();
      }

   }

   public void toBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);
      packet.writeInt(this.entityID);
      packet.writeInt(this.playerReputation);
   }
}
