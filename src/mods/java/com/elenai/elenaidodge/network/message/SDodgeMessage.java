package com.elenai.elenaidodge.network.message;

import com.elenai.elenaidodge.ElenaiDodge;
import com.elenai.elenaidodge.api.DodgeEvent;
import com.elenai.elenaidodge.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class SDodgeMessage implements IMessage {
   private boolean messageValid;
   private String dir;
   private int cooldown;

   public SDodgeMessage() {
      this.messageValid = false;
   }

   public SDodgeMessage(String dir, int cooldown) {
      this.dir = dir;
      this.cooldown = cooldown;
      this.messageValid = true;
   }

   public void fromBytes(ByteBuf buf) {
      try {
         this.dir = ByteBufUtils.readUTF8String(buf);
         this.cooldown = buf.readInt();
      } catch (IndexOutOfBoundsException var3) {
         ElenaiDodge.LOG.error("Error occured whilst networking!", var3);
         return;
      }

      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      if (this.messageValid) {
         ByteBufUtils.writeUTF8String(buf, this.dir);
         buf.writeInt(this.cooldown);
      }
   }

   public static class Handler implements IMessageHandler<SDodgeMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(SDodgeMessage message, MessageContext ctx) {
         if (!message.messageValid && ctx.side != Side.SERVER) {
            return null;
         } else {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
               this.processMessage(message, ctx);
            });
            return null;
         }
      }

      void processMessage(SDodgeMessage message, MessageContext ctx) {
         EntityPlayerMP player = ctx.getServerHandler().player;
         DodgeEvent.ServerDodgeEvent event = new DodgeEvent.ServerDodgeEvent(DodgeEvent.Direction.valueOf(message.dir), Utils.calculateForce(player), player, message.cooldown);
         if (!MinecraftForge.EVENT_BUS.post(event)) {
            Utils.handleDodge(DodgeEvent.Direction.valueOf(message.dir), event, player);
         }

      }
   }
}
