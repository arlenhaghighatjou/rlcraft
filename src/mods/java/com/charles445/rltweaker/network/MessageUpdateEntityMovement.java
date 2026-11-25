package com.charles445.rltweaker.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageUpdateEntityMovement implements IMessage {
   private int id;
   private double x;
   private double y;
   private double z;
   private double motionX;
   private double motionY;
   private double motionZ;

   public MessageUpdateEntityMovement() {
   }

   public MessageUpdateEntityMovement(Entity entity) {
      this.id = entity.getEntityId();
      this.x = entity.posX;
      this.y = entity.posY;
      this.z = entity.posZ;
      this.motionX = entity.motionX;
      this.motionY = entity.motionY;
      this.motionZ = entity.motionZ;
   }

   public void fromBytes(ByteBuf buf) {
      this.id = buf.readInt();
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
      this.motionX = buf.readDouble();
      this.motionY = buf.readDouble();
      this.motionZ = buf.readDouble();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.id);
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeDouble(this.motionX);
      buf.writeDouble(this.motionY);
      buf.writeDouble(this.motionZ);
   }

   public static class Handler implements IMessageHandler<MessageUpdateEntityMovement, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(MessageUpdateEntityMovement message, MessageContext ctx) {
         if (ctx.side == Side.CLIENT) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
               TaskScheduler.addClientTask(message, 0L);
            });
         }

         return null;
      }

      @SideOnly(Side.CLIENT)
      public static void fromMessage(MessageUpdateEntityMovement message) {
         World world = Minecraft.getMinecraft().world;
         if (world != null) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (player != null) {
               Entity entity = world.getEntityByID(message.id);
               if (entity != null) {
                  entity.posX = message.x;
                  entity.posY = message.y;
                  entity.posZ = message.z;
                  entity.motionX = message.motionX;
                  entity.motionY = message.motionY;
                  entity.motionZ = message.motionZ;
               }

            }
         }
      }
   }
}
