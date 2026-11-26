package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.vec;
import com.yyon.grapplinghook.controllers.SegmentHandler;
import com.yyon.grapplinghook.entities.grappleArrow;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.util.LinkedList;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GrappleAttachMessage implements IMessage {
   public int id;
   public double x;
   public double y;
   public double z;
   public int controlid;
   public int entityid;
   public BlockPos blockpos;
   public LinkedList<vec> segments;
   public LinkedList<EnumFacing> segmenttopsides;
   public LinkedList<EnumFacing> segmentbottomsides;
   public GrappleCustomization custom;

   public GrappleAttachMessage() {
   }

   public GrappleAttachMessage(int id, double x, double y, double z, int controlid, int entityid, BlockPos blockpos, LinkedList<vec> segments, LinkedList<EnumFacing> segmenttopsides, LinkedList<EnumFacing> segmentbottomsides, GrappleCustomization custom) {
      this.id = id;
      this.x = x;
      this.y = y;
      this.z = z;
      this.controlid = controlid;
      this.entityid = entityid;
      this.blockpos = blockpos;
      this.segments = segments;
      this.segmenttopsides = segmenttopsides;
      this.segmentbottomsides = segmentbottomsides;
      this.custom = custom;
   }

   public void fromBytes(ByteBuf buf) {
      this.id = buf.readInt();
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
      this.controlid = buf.readInt();
      this.entityid = buf.readInt();
      int blockx = buf.readInt();
      int blocky = buf.readInt();
      int blockz = buf.readInt();
      this.blockpos = new BlockPos(blockx, blocky, blockz);
      this.custom = new GrappleCustomization();
      this.custom.readFromBuf(buf);
      int size = buf.readInt();
      this.segments = new LinkedList();
      this.segmentbottomsides = new LinkedList();
      this.segmenttopsides = new LinkedList();
      this.segments.add(new vec(0.0D, 0.0D, 0.0D));
      this.segmentbottomsides.add(null);
      this.segmenttopsides.add(null);

      for(int i = 1; i < size - 1; ++i) {
         this.segments.add(new vec(buf.readDouble(), buf.readDouble(), buf.readDouble()));
         this.segmentbottomsides.add(EnumFacing.getFront(buf.readInt()));
         this.segmenttopsides.add(EnumFacing.getFront(buf.readInt()));
      }

      this.segments.add(new vec(0.0D, 0.0D, 0.0D));
      this.segmentbottomsides.add(null);
      this.segmenttopsides.add(null);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.id);
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeInt(this.controlid);
      buf.writeInt(this.entityid);
      buf.writeInt(this.blockpos.getX());
      buf.writeInt(this.blockpos.getY());
      buf.writeInt(this.blockpos.getZ());
      this.custom.writeToBuf(buf);
      buf.writeInt(this.segments.size());

      for(int i = 1; i < this.segments.size() - 1; ++i) {
         buf.writeDouble(((vec)this.segments.get(i)).x);
         buf.writeDouble(((vec)this.segments.get(i)).y);
         buf.writeDouble(((vec)this.segments.get(i)).z);
         buf.writeInt(((EnumFacing)this.segmentbottomsides.get(i)).getIndex());
         buf.writeInt(((EnumFacing)this.segmenttopsides.get(i)).getIndex());
      }

   }

   public static class Handler implements IMessageHandler<GrappleAttachMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(GrappleAttachMessage message, MessageContext ctx) {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new GrappleAttachMessage.Handler.runner(message, ctx));
         return null;
      }

      public class runner implements Runnable {
         GrappleAttachMessage message;
         MessageContext ctx;

         public runner(GrappleAttachMessage message, MessageContext ctx) {
            this.message = message;
            this.ctx = ctx;
         }

         public void run() {
            World world = Minecraft.getMinecraft().world;
            Entity grapple = world.getEntityByID(this.message.id);
            if (grapple instanceof grappleArrow) {
               ((grappleArrow)grapple).clientAttach(this.message.x, this.message.y, this.message.z);
               SegmentHandler segmenthandler = ((grappleArrow)grapple).segmenthandler;
               segmenthandler.segments = this.message.segments;
               segmenthandler.segmentbottomsides = this.message.segmentbottomsides;
               segmenthandler.segmenttopsides = this.message.segmenttopsides;
               Entity player = world.getEntityByID(this.message.entityid);
               segmenthandler.forceSetPos(new vec(this.message.x, this.message.y, this.message.z), vec.positionvec(player));
            }

            grapplemod.proxy.createControl(this.message.controlid, this.message.id, this.message.entityid, world, new vec(this.message.x, this.message.y, this.message.z), this.message.blockpos, this.message.custom);
         }
      }
   }
}
