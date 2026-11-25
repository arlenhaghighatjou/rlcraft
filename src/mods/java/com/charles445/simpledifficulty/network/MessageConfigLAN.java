package com.charles445.simpledifficulty.network;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.config.ModConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class MessageConfigLAN implements IMessage {
   public MessageConfigLAN() {
   }

   public void fromBytes(ByteBuf buf) {
   }

   public void toBytes(ByteBuf buf) {
   }

   public static class Handler implements IMessageHandler<MessageConfigLAN, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(MessageConfigLAN message, MessageContext ctx) {
         if (ctx.side == Side.SERVER) {
            EntityPlayerMP sender = ctx.getServerHandler().player;
            if (sender != null && FMLCommonHandler.instance().getSide().isClient()) {
               EntityPlayer receiver = SimpleDifficulty.proxy.getClientMinecraftPlayer();
               if (receiver == null) {
                  SimpleDifficulty.logger.error("Client's player was null on physical client side!");
                  return null;
               }

               if (sender.getUniqueID().equals(receiver.getUniqueID())) {
                  sender.getServerWorld().addScheduledTask(() -> {
                     ModConfig.sendServerConfigToAllPlayers();
                  });
               }
            }
         }

         return null;
      }
   }
}
