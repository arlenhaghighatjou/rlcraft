package com.charles445.rltweaker.network;

import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.VersionDelimiter;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageUpdateAttackYaw implements IMessage {
   private float attackedAtYaw;
   public static final VersionDelimiter VERSION = VersionDelimiter.parse("0.4.0");

   public MessageUpdateAttackYaw() {
   }

   public MessageUpdateAttackYaw(EntityLivingBase entity) {
      this.attackedAtYaw = entity.attackedAtYaw;
   }

   public void fromBytes(ByteBuf buf) {
      this.attackedAtYaw = buf.readFloat();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeFloat(this.attackedAtYaw);
   }

   public static class Handler implements IMessageHandler<MessageUpdateAttackYaw, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(MessageUpdateAttackYaw message, MessageContext ctx) {
         if (ctx.side == Side.CLIENT) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
               fromMessage(message);
            });
         }

         return null;
      }

      @SideOnly(Side.CLIENT)
      public static void fromMessage(MessageUpdateAttackYaw message) {
         if (ModConfig.server.minecraft.damageTilt) {
            Minecraft.getMinecraft().player.attackedAtYaw = message.attackedAtYaw;
         }
      }
   }
}
