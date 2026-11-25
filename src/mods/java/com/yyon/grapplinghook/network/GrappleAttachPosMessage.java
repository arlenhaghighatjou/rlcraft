package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.entities.grappleArrow;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GrappleAttachPosMessage implements IMessage {
   public int id;
   public double x;
   public double y;
   public double z;

   public GrappleAttachPosMessage() {
   }

   public GrappleAttachPosMessage(int id, double x, double y, double z) {
      this.id = id;
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public void fromBytes(ByteBuf buf) {
      this.id = buf.readInt();
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.id);
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
   }

   public static class Handler implements IMessageHandler<GrappleAttachPosMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(GrappleAttachPosMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new GrappleAttachPosMessage.Handler.runner(message, ctx));
         return null;
      }

      public class runner implements Runnable {
         GrappleAttachPosMessage message;
         MessageContext ctx;

         public runner(GrappleAttachPosMessage message, MessageContext ctx) {
            this.message = message;
            this.ctx = ctx;
         }

         public void run() {
            World world = Minecraft.getMinecraft().world;
            Entity grapple = world.getEntityByID(this.message.id);
            if (grapple instanceof grappleArrow) {
               ((grappleArrow)grapple).setAttachPos(this.message.x, this.message.y, this.message.z);
            }

         }
      }
   }
}
