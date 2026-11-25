package vazkii.quark.base.network.message;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.management.feature.FavoriteItems;

public class MessageFavoriteItem extends NetworkMessage<MessageFavoriteItem> {
   public int index;

   public MessageFavoriteItem() {
   }

   public MessageFavoriteItem(int index) {
      this.index = index;
   }

   public IMessage handleMessage(MessageContext context) {
      FavoriteItems.favoriteItem(context.getServerHandler().player, this.index);
      return null;
   }
}
