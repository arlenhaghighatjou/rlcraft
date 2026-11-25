package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.management.feature.FToSwitchItems;

public class MessageSwapItems extends NetworkMessage<MessageSwapItems> {
   public int index;

   public MessageSwapItems() {
   }

   public MessageSwapItems(int index) {
      this.index = index;
   }

   public IMessage handleMessage(MessageContext context) {
      context.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
         FToSwitchItems.switchItems(context.getServerHandler().player, this.index);
      });
      return null;
   }
}
