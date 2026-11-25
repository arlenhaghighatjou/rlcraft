package com.elenai.elenaidodge.network.message;

import com.elenai.elenaidodge.ModConfig;
import com.elenai.elenaidodge.util.ClientStorage;
import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class CInitPlayerMessage implements IMessage {
   private boolean messageValid = true;

   public CInitPlayerMessage() {
   }

   public void fromBytes(ByteBuf buf) {
      this.messageValid = true;
   }

   public void toBytes(ByteBuf buf) {
      if (this.messageValid) {
         ;
      }
   }

   public static class Handler implements IMessageHandler<CInitPlayerMessage, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(CInitPlayerMessage message, MessageContext ctx) {
         if (!message.messageValid && ctx.side != Side.CLIENT) {
            return null;
         } else {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
               this.processMessage(message, ctx);
            });
            return null;
         }
      }

      void processMessage(CInitPlayerMessage message, MessageContext ctx) {
         if (ModConfig.client.hud.tutorial && !Loader.isModLoaded("reskillable")) {
            ClientStorage.shownTutorial = false;
            ClientStorage.tutorialDodges = 0.0D;
         }

      }
   }
}
