package net.blay09.mods.waystones.network.handler;

import javax.annotation.Nullable;
import net.blay09.mods.waystones.network.message.MessageConfig;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HandlerConfig implements IMessageHandler<MessageConfig, IMessage> {
   public HandlerConfig() {
   }

   @Nullable
   public IMessage onMessage(MessageConfig message, MessageContext ctx) {
      return null;
   }
}
