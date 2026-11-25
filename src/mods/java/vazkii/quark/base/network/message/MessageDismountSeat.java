package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;

public class MessageDismountSeat extends NetworkMessage<MessageDismountSeat> {
   public MessageDismountSeat() {
   }

   public IMessage handleMessage(MessageContext context) {
      context.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
         context.getServerHandler().player.dismountRidingEntity();
      });
      return null;
   }
}
