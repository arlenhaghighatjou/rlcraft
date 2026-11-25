package ivorius.reccomplex.network;

import ivorius.reccomplex.item.ItemEventHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class PacketItemEventHandler extends PacketEditInventoryItemHandler<PacketItemEvent> {
   public PacketItemEventHandler() {
   }

   public void affectItem(EntityPlayerMP player, ItemStack stack, PacketItemEvent message) {
      if (stack != null) {
         ItemEventHandler itemEventHandler = (ItemEventHandler)stack.getItem();
         itemEventHandler.onClientEvent(message.context, message.payload, player, stack, message.getInventorySlot());
      }

   }
}
