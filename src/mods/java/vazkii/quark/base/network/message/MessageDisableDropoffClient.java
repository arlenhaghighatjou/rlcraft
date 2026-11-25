package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.management.feature.StoreToChests;

public class MessageDisableDropoffClient extends NetworkMessage<MessageDisableDropoffClient> {
   public MessageDisableDropoffClient() {
   }

   public IMessage handleMessage(MessageContext context) {
      StoreToChests.clientDisabled = true;
      return null;
   }
}
