package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.entity.EntityAmphithere;
import com.github.alexthe666.iceandfire.entity.EntityDeathWorm;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityHippocampus;
import com.github.alexthe666.iceandfire.entity.EntityHippogryph;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageDragonControl extends AbstractMessage<MessageDragonControl> {
   public int dragonId;
   public byte controlState;
   public int armor_type;
   private double posX;
   private double posY;
   private double posZ;

   public MessageDragonControl(int dragonId, byte controlState, double posX, double posY, double posZ) {
      this.dragonId = dragonId;
      this.controlState = controlState;
      this.posX = posX;
      this.posY = posY;
      this.posZ = posZ;
   }

   public MessageDragonControl() {
   }

   public void fromBytes(ByteBuf buf) {
      this.dragonId = buf.readInt();
      this.controlState = buf.readByte();
      this.posX = buf.readDouble();
      this.posY = buf.readDouble();
      this.posZ = buf.readDouble();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.dragonId);
      buf.writeByte(this.controlState);
      buf.writeDouble(this.posX);
      buf.writeDouble(this.posY);
      buf.writeDouble(this.posZ);
   }

   @SideOnly(Side.CLIENT)
   public void onClientReceived(Minecraft client, MessageDragonControl message, EntityPlayer player, MessageContext messageContext) {
   }

   public void onServerReceived(MinecraftServer server, MessageDragonControl message, EntityPlayer player, MessageContext messageContext) {
      Entity entity = player.world.getEntityByID(message.dragonId);
      if (entity != null && entity instanceof EntityDragonBase) {
         EntityDragonBase dragon = (EntityDragonBase)entity;
         if (dragon.isOwner(player)) {
            dragon.setControlState(message.controlState);
         }

         dragon.setPosition(message.posX, message.posY, message.posZ);
      } else if (entity instanceof EntityHippogryph) {
         EntityHippogryph hippo = (EntityHippogryph)entity;
         if (hippo.isOwner(player)) {
            hippo.setControlState(message.controlState);
         }

         hippo.setPosition(message.posX, message.posY, message.posZ);
      } else if (entity instanceof EntityHippocampus) {
         EntityHippocampus hippo = (EntityHippocampus)entity;
         if (hippo.isOwner(player)) {
            hippo.setControlState(message.controlState);
         }

         hippo.setPosition(message.posX, message.posY, message.posZ);
      } else if (entity instanceof EntityDeathWorm) {
         EntityDeathWorm deathworm = (EntityDeathWorm)entity;
         deathworm.setControlState(message.controlState);
         deathworm.setPosition(message.posX, message.posY, message.posZ);
      } else if (entity instanceof EntityAmphithere) {
         EntityAmphithere amphi = (EntityAmphithere)entity;
         if (amphi.isOwner(player)) {
            amphi.setControlState(message.controlState);
         }

         amphi.setPosition(message.posX, message.posY, message.posZ);
      }

   }
}
