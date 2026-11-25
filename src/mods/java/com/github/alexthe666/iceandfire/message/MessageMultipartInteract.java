package com.github.alexthe666.iceandfire.message;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageMultipartInteract extends AbstractMessage<MessageMultipartInteract> {
   public int creatureID;
   public float dmg;

   public MessageMultipartInteract(int creatureID, float dmg) {
      this.creatureID = creatureID;
      this.dmg = dmg;
   }

   public MessageMultipartInteract() {
   }

   public void fromBytes(ByteBuf buf) {
      this.creatureID = buf.readInt();
      this.dmg = buf.readFloat();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.creatureID);
      buf.writeFloat(this.dmg);
   }

   @SideOnly(Side.CLIENT)
   public void onClientReceived(Minecraft client, MessageMultipartInteract message, EntityPlayer player, MessageContext messageContext) {
      Entity entity = player.world.getEntityByID(message.creatureID);
      if (entity != null && entity instanceof EntityLivingBase) {
         EntityLivingBase mob = (EntityLivingBase)entity;
         if (message.dmg > 0.0F) {
            mob.attackEntityFrom(DamageSource.causeMobDamage(player), this.dmg);
         } else {
            mob.processInitialInteract(player, EnumHand.MAIN_HAND);
         }
      }

   }

   public void onServerReceived(MinecraftServer server, MessageMultipartInteract message, EntityPlayer player, MessageContext messageContext) {
      Entity entity = player.world.getEntityByID(message.creatureID);
      if (entity != null && entity instanceof EntityLivingBase) {
         EntityLivingBase mob = (EntityLivingBase)entity;
         if (message.dmg > 0.0F) {
            mob.attackEntityFrom(DamageSource.causeMobDamage(player), this.dmg);
         } else {
            mob.processInitialInteract(player, EnumHand.MAIN_HAND);
         }
      }

   }
}
