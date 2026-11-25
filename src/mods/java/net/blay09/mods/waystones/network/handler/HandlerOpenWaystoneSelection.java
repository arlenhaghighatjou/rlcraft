package net.blay09.mods.waystones.network.handler;

import javax.annotation.Nullable;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.network.NetworkHandler;
import net.blay09.mods.waystones.network.message.MessageOpenWaystoneSelection;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerOpenWaystoneSelection implements IMessageHandler<MessageOpenWaystoneSelection, IMessage> {
   public HandlerOpenWaystoneSelection() {
   }

   @Nullable
   public IMessage onMessage(final MessageOpenWaystoneSelection message, MessageContext ctx) {
      NetworkHandler.getThreadListener(ctx).addScheduledTask(new Runnable() {
         public void run() {
            EntityPlayer player = Minecraft.getMinecraft().player;
            Waystones.proxy.openWaystoneSelection(player, message.getWarpMode(), message.getHand(), message.getWaystone());
         }
      });
      return null;
   }
}
