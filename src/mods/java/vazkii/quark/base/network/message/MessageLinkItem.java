package vazkii.quark.base.network.message;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import vazkii.arl.network.NetworkMessage;
import vazkii.quark.management.feature.LinkItems;

public class MessageLinkItem extends NetworkMessage<MessageLinkItem> {
   public ItemStack stack;

   public MessageLinkItem() {
   }

   public MessageLinkItem(ItemStack stack) {
      this.stack = stack;
   }

   public IMessage handleMessage(MessageContext context) {
      EntityPlayerMP player = context.getServerHandler().player;
      player.getServerWorld().addScheduledTask(() -> {
         LinkItems.linkItem(player, this.stack);
      });
      return null;
   }
}
