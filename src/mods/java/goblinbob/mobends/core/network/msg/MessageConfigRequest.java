package goblinbob.mobends.core.network.msg;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageConfigRequest implements IMessage {
   public MessageConfigRequest() {
   }

   public void fromBytes(ByteBuf buf) {
   }

   public void toBytes(ByteBuf buf) {
   }

   public static class Handler implements IMessageHandler<MessageConfigRequest, MessageConfigResponse> {
      public Handler() {
      }

      public MessageConfigResponse onMessage(MessageConfigRequest message, MessageContext ctx) {
         return new MessageConfigResponse();
      }
   }
}
