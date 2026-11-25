package net.blay09.mods.waystones.network.handler;

import javax.annotation.Nullable;
import net.blay09.mods.waystones.PlayerWaystoneHelper;
import net.blay09.mods.waystones.client.ClientWaystones;
import net.blay09.mods.waystones.item.ItemWarpStone;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageWaystones;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerWaystones implements IMessageHandler<MessageWaystones, IMessage> {
   public HandlerWaystones() {
   }

   @Nullable
   public IMessage onMessage(final MessageWaystones message, MessageContext ctx) {
      NetworkHandler.getThreadListener(ctx).addScheduledTask(new Runnable() {
         public void run() {
            ClientWaystones.setKnownWaystones(message.getEntries());
            PlayerWaystoneHelper.store(FMLClientHandler.instance().getClientPlayerEntity(), message.getEntries(), message.getLastFreeWarp(), message.getLastWarpStoneUse());
            ItemWarpStone.lastTimerUpdate = System.currentTimeMillis();
         }
      });
      return null;
   }
}
