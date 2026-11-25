package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.SRPMain;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SRPPacketBiomeChange implements IMessage {
   private BlockPos blockPos;
   private boolean convertToParasite;

   public SRPPacketBiomeChange() {
   }

   public SRPPacketBiomeChange(BlockPos pos, boolean convert) {
      this.blockPos = pos;
      this.convertToParasite = convert;
   }

   public void fromBytes(ByteBuf ByteBuf) {
      this.blockPos = new BlockPos(ByteBuf.readInt(), ByteBuf.readInt(), ByteBuf.readInt());
      this.convertToParasite = ByteBuf.readBoolean();
   }

   public void toBytes(ByteBuf ByteBuf) {
      ByteBuf.writeInt(this.blockPos.getX());
      ByteBuf.writeInt(this.blockPos.getY());
      ByteBuf.writeInt(this.blockPos.getZ());
      ByteBuf.writeBoolean(this.convertToParasite);
   }

   public static class Handler implements IMessageHandler<SRPPacketBiomeChange, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(SRPPacketBiomeChange message, MessageContext ctx) {
         FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
            this.handle(message, ctx);
         });
         return null;
      }

      private void handle(SRPPacketBiomeChange message, MessageContext ctx) {
         SRPMain.proxy.spreadBiome(message.blockPos, message.convertToParasite);
      }
   }
}
