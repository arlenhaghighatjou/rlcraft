package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.grapplemod;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import java.util.HashSet;
import java.util.Iterator;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GrappleEndMessage implements IMessage {
   public int entityid;
   public HashSet<Integer> arrowIds;

   public GrappleEndMessage() {
   }

   public GrappleEndMessage(int entityid, HashSet<Integer> arrowIds) {
      this.entityid = entityid;
      this.arrowIds = arrowIds;
   }

   public void fromBytes(ByteBuf buf) {
      this.entityid = buf.readInt();
      int size = buf.readInt();
      this.arrowIds = new HashSet();

      for(int i = 0; i < size; ++i) {
         this.arrowIds.add(Integer.valueOf(buf.readInt()));
      }

   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.entityid);
      buf.writeInt(this.arrowIds.size());
      Iterator var2 = this.arrowIds.iterator();

      while(var2.hasNext()) {
         int id = ((Integer)var2.next()).intValue();
         buf.writeInt(id);
      }

   }

   public static class Handler implements IMessageHandler<GrappleEndMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(GrappleEndMessage message, MessageContext ctx) {
         IThreadListener mainThread = (WorldServer)ctx.getServerHandler().player.world;
         mainThread.addScheduledTask(new GrappleEndMessage.Handler.runner(message, ctx));
         return null;
      }

      public class runner implements Runnable {
         GrappleEndMessage message;
         MessageContext ctx;

         public runner(GrappleEndMessage message, MessageContext ctx) {
            this.message = message;
            this.ctx = ctx;
         }

         public void run() {
            int id = this.message.entityid;
            World w = this.ctx.getServerHandler().player.world;
            grapplemod.receiveGrappleEnd(id, w, this.message.arrowIds);
         }
      }
   }
}
