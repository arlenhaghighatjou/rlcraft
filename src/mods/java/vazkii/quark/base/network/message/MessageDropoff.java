package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.base.handler.DropoffHandler;

public class MessageDropoff extends NetworkMessage<MessageDropoff> {
   public boolean smart;
   public boolean useContainer;

   public MessageDropoff() {
   }

   public MessageDropoff(boolean smart, boolean useContainer) {
      this.smart = smart;
      this.useContainer = useContainer;
   }

   public IMessage handleMessage(MessageContext context) {
      context.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
         DropoffHandler.dropoff(context.getServerHandler().player, this.smart, this.useContainer);
      });
      return null;
   }
}
