package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.controllers.SegmentHandler;
import com.yyon.grapplinghook.entities.grappleArrow;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SegmentMessage implements IMessage {
   public int id;
   public boolean add;
   public int index;
   public vec pos;
   public EnumFacing topfacing;
   public EnumFacing bottomfacing;

   public SegmentMessage() {
   }

   public SegmentMessage(int id, boolean add, int index, vec pos, EnumFacing topfacing, EnumFacing bottomfacing) {
      this.id = id;
      this.add = add;
      this.index = index;
      this.pos = pos;
      this.topfacing = topfacing;
      this.bottomfacing = bottomfacing;
   }

   public void fromBytes(ByteBuf buf) {
      this.id = buf.readInt();
      this.add = buf.readBoolean();
      this.index = buf.readInt();
      this.pos = new vec(buf.readDouble(), buf.readDouble(), buf.readDouble());
      this.topfacing = EnumFacing.getFront(buf.readInt());
      this.bottomfacing = EnumFacing.getFront(buf.readInt());
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.id);
      buf.writeBoolean(this.add);
      buf.writeInt(this.index);
      buf.writeDouble(this.pos.x);
      buf.writeDouble(this.pos.y);
      buf.writeDouble(this.pos.z);
      buf.writeInt(this.topfacing.getIndex());
      buf.writeInt(this.bottomfacing.getIndex());
   }

   public static class Handler implements IMessageHandler<SegmentMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(SegmentMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new SegmentMessage.Handler.runner(message, ctx));
         return null;
      }

      public class runner implements Runnable {
         SegmentMessage message;
         MessageContext ctx;

         public runner(SegmentMessage message, MessageContext ctx) {
            this.message = message;
            this.ctx = ctx;
         }

         public void run() {
            World world = Minecraft.getMinecraft().world;
            Entity grapple = world.getEntityByID(this.message.id);
            if (grapple != null) {
               if (grapple instanceof grappleArrow) {
                  SegmentHandler segmenthandler = ((grappleArrow)grapple).segmenthandler;
                  if (this.message.add) {
                     segmenthandler.actuallyaddsegment(this.message.index, this.message.pos, this.message.bottomfacing, this.message.topfacing);
                  } else {
                     segmenthandler.removesegment(this.message.index);
                  }
               }

            }
         }
      }
   }
}
