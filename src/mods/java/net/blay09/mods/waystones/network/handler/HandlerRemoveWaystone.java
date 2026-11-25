package net.blay09.mods.waystones.network.handler;

import javax.annotation.Nullable;
import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageRemoveWaystone;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerRemoveWaystone implements IMessageHandler<MessageRemoveWaystone, IMessage> {
   public HandlerRemoveWaystone() {
   }

   @Nullable
   public IMessage onMessage(MessageRemoveWaystone message, MessageContext ctx) {
      NetworkHandler.getThreadListener(ctx).addScheduledTask(() -> {
         PlayerWaystoneData waystoneData = PlayerWaystoneData.fromPlayer(ctx.getServerHandler().player);
         WaystoneEntry[] entries = waystoneData.getWaystones();
         int index = message.getIndex();
         if (index >= 0 && index < entries.length) {
            WaystoneManager.removePlayerWaystone(ctx.getServerHandler().player, entries[index]);
            WaystoneManager.sendPlayerWaystones(ctx.getServerHandler().player);
         }
      });
      return null;
   }
}
