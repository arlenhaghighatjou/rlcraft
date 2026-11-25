package rustic.common.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageDismountChair implements IMessage {
   public MessageDismountChair() {
   }

   public void fromBytes(ByteBuf buf) {
   }

   public void toBytes(ByteBuf buf) {
   }

   public static class MessageHolder implements IMessageHandler<MessageDismountChair, IMessage> {
      public MessageHolder() {
      }

      public IMessage onMessage(MessageDismountChair message, MessageContext ctx) {
         EntityPlayerMP player = ctx.getServerHandler().player;
         player.getServerWorld().addScheduledTask(() -> {
            player.dismountRidingEntity();
         });
         return null;
      }
   }
}
