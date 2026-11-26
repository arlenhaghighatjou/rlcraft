package net.silentchaos512.lib.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class MessageSL<REQ extends MessageSL> implements IMessage, IMessageHandler<REQ, IMessage> {
   public MessageSL() {
   }

   public IMessage handleMessage(MessageContext context) {
      return null;
   }

   public final IMessage onMessage(REQ message, MessageContext context) {
      return message.handleMessage(context);
   }
}
