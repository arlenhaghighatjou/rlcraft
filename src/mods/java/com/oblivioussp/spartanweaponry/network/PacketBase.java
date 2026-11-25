package com.oblivioussp.spartanweaponry.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class PacketBase<REQ extends IMessage> implements IMessage, IMessageHandler<REQ, REQ> {
   public PacketBase() {
   }

   public REQ onMessage(final REQ message, final MessageContext ctx) {
      if (ctx.side.isServer()) {
         IThreadListener mainThread = ctx.getServerHandler().player.getServer();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               PacketBase.this.handleServerSide(message, ctx.getServerHandler().player);
            }
         });
      } else {
         IThreadListener mainThread = Minecraft.getMinecraft();
         mainThread.addScheduledTask(new Runnable() {
            public void run() {
               PacketBase.this.handleClientSide(message, Minecraft.getMinecraft().player);
            }
         });
      }

      return null;
   }

   public abstract void handleClientSide(REQ var1, EntityPlayer var2);

   public abstract void handleServerSide(REQ var1, EntityPlayerMP var2);
}
