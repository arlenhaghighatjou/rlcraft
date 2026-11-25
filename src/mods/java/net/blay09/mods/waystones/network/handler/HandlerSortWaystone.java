package net.blay09.mods.waystones.network.handler;

import javax.annotation.Nullable;
import net.blay09.mods.waystones.PlayerWaystoneData;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageSortWaystone;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerSortWaystone implements IMessageHandler<MessageSortWaystone, IMessage> {
   public HandlerSortWaystone() {
   }

   @Nullable
   public IMessage onMessage(MessageSortWaystone message, MessageContext ctx) {
      NetworkHandler.getThreadListener(ctx).addScheduledTask(() -> {
         PlayerWaystoneData waystoneData = PlayerWaystoneData.fromPlayer(ctx.getServerHandler().player);
         WaystoneEntry[] entries = waystoneData.getWaystones();
         int index = message.getIndex();
         int otherIndex = message.getOtherIndex();
         if (index >= 0 && index < entries.length && otherIndex >= 0 && otherIndex < entries.length) {
            WaystoneEntry swap = entries[index];
            entries[index] = entries[otherIndex];
            entries[otherIndex] = swap;
            waystoneData.store(ctx.getServerHandler().player);
            WaystoneManager.sendPlayerWaystones(ctx.getServerHandler().player);
         }
      });
      return null;
   }
}
