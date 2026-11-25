package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.base.handler.DropoffHandler;

public class MessageRestock extends NetworkMessage<MessageRestock> {
   public boolean filtered;

   public MessageRestock() {
   }

   public MessageRestock(boolean filtered) {
      this.filtered = filtered;
   }

   public IMessage handleMessage(MessageContext context) {
      context.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
         DropoffHandler.restock(context.getServerHandler().player, this.filtered);
      });
      return null;
   }
}
