package com.lycanitesmobs.core.network;

import com.lycanitesmobs.core.entity.ExtendedEntity;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageEntityPerched implements IMessage {
   public int perchedOnEntityID;
   public int perchedByEntityID;

   public MessageEntityPerched() {
   }

   public MessageEntityPerched(Entity perchedOnEntity, Entity perchedByEntity) {
      this.perchedOnEntityID = perchedOnEntity.getEntityId();
      this.perchedByEntityID = perchedByEntity != null ? perchedByEntity.getEntityId() : 0;
   }

   public static void onMessage(MessageEntityPerched message, MessageContext ctx, EntityPlayer player) {
      World world = player.getEntityWorld();
      Entity perchedOnEntity = world.getEntityByID(message.perchedOnEntityID);
      Entity perchedByEntity = message.perchedByEntityID != 0 ? world.getEntityByID(message.perchedByEntityID) : null;
      if (perchedOnEntity instanceof EntityLivingBase) {
         ExtendedEntity perchedOnEntityExt = ExtendedEntity.getForEntity((EntityLivingBase)perchedOnEntity);
         if (perchedOnEntityExt != null) {
            perchedOnEntityExt.setPerchedByEntity(perchedByEntity);
         }

      }
   }

   public void fromBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);
      this.perchedOnEntityID = packet.readInt();
      this.perchedByEntityID = packet.readInt();
   }

   public void toBytes(ByteBuf buf) {
      PacketBuffer packet = new PacketBuffer(buf);
      packet.writeInt(this.perchedOnEntityID);
      packet.writeInt(this.perchedByEntityID);
   }
}
