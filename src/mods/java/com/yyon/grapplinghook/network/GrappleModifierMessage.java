package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.blocks.TileEntityGrappleModifier;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GrappleModifierMessage implements IMessage {
   public BlockPos pos;
   public GrappleCustomization custom;

   public GrappleModifierMessage() {
   }

   public GrappleModifierMessage(BlockPos pos, GrappleCustomization custom) {
      this.pos = pos;
      this.custom = custom;
   }

   public void fromBytes(ByteBuf buf) {
      this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
      this.custom = new GrappleCustomization();
      this.custom.readFromBuf(buf);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.pos.getX());
      buf.writeInt(this.pos.getY());
      buf.writeInt(this.pos.getZ());
      this.custom.writeToBuf(buf);
   }

   public static class Handler implements IMessageHandler<GrappleModifierMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(GrappleModifierMessage message, MessageContext ctx) {
         IThreadListener mainThread = (WorldServer)ctx.getServerHandler().player.world;
         mainThread.addScheduledTask(new GrappleModifierMessage.Handler.runner(message, ctx));
         return null;
      }

      public class runner implements Runnable {
         GrappleModifierMessage message;
         MessageContext ctx;

         public runner(GrappleModifierMessage message, MessageContext ctx) {
            this.message = message;
            this.ctx = ctx;
         }

         public void run() {
            World w = this.ctx.getServerHandler().player.world;
            TileEntity ent = w.getTileEntity(this.message.pos);
            if (ent instanceof TileEntityGrappleModifier) {
               ((TileEntityGrappleModifier)ent).setCustomizationServer(this.message.custom);
            }

         }
      }
   }
}
