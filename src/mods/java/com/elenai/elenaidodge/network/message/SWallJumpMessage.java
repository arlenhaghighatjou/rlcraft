package com.elenai.elenaidodge.network.message;

import com.elenai.elenaidodge.ModConfig;
import com.elenai.elenaidodge.api.WallJumpEvent;
import com.elenai.elenaidodge.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SWallJumpMessage implements IMessage {
   private boolean messageValid = true;

   public SWallJumpMessage() {
   }

   public void fromBytes(ByteBuf buf) {
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      if (this.messageValid) {
         ;
      }
   }

   public static class Handler implements IMessageHandler<SWallJumpMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(SWallJumpMessage message, MessageContext ctx) {
         if (!message.messageValid && ctx.side != Side.SERVER) {
            return null;
         } else {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
               this.processMessage(message, ctx);
            });
            return null;
         }
      }

      void processMessage(SWallJumpMessage message, MessageContext ctx) {
         EntityPlayerMP player = ctx.getServerHandler().player;
         WallJumpEvent.ServerWallJumpEvent event = new WallJumpEvent.ServerWallJumpEvent(ModConfig.common.wallJump.forwardsForce, player, 0);
         if (!MinecraftForge.EVENT_BUS.post(event)) {
            Utils.handleWallJump(event, player);
         }

      }
   }
}
