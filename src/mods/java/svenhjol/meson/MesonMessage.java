package svenhjol.meson;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class MesonMessage<REQ extends MesonMessage> implements IMessage, IMessageHandler<REQ, IMessage> {
   public MesonMessage() {
   }

   public IMessage handle(MessageContext context) {
      return null;
   }

   public IMessage onMessage(REQ message, MessageContext context) {
      return message.handle(context);
   }
}
