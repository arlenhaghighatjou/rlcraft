package com.charles445.rltweaker.network;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.util.ReflectUtil;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageUpdateDismountStatus implements IMessage {
   public MessageUpdateDismountStatus() {
   }

   public void fromBytes(ByteBuf buf) {
   }

   public void toBytes(ByteBuf buf) {
   }

   public static class Handler implements IMessageHandler<MessageUpdateDismountStatus, IMessage> {
      public static Field f_ridingEntity;
      public static Method m_removePassenger;

      public Handler() {
      }

      public IMessage onMessage(MessageUpdateDismountStatus message, MessageContext ctx) {
         if (ctx.side == Side.CLIENT) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
               execute();
            });
         }

         return null;
      }

      @SideOnly(Side.CLIENT)
      public static void execute() {
         EntityPlayer player = Minecraft.getMinecraft().player;
         if (player != null) {
            player.dismountRidingEntity();
            if (f_ridingEntity != null && m_removePassenger != null) {
               if (player.getRidingEntity() != null) {
                  Entity entity = player.getRidingEntity();

                  try {
                     f_ridingEntity.set(player, (Object)null);
                     m_removePassenger.invoke(entity, player);
                  } catch (Exception var3) {
                     ;
                  }
               }

            }
         }
      }

      static {
         try {
            f_ridingEntity = ReflectUtil.findFieldAny(Entity.class, "ridingEntity", "ridingEntity");
            m_removePassenger = ReflectUtil.findMethodAny(Entity.class, "removePassenger", "removePassenger", Entity.class);
         } catch (Exception var1) {
            RLTweaker.logger.error("Couldn't setup ridingEntity clientside");
         }

      }
   }
}
