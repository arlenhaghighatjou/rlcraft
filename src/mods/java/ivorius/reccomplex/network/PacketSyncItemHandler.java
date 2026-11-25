package ivorius.reccomplex.network;

import ivorius.reccomplex.item.ItemSyncable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class PacketSyncItemHandler extends PacketEditInventoryItemHandler<PacketSyncItem> {
   public PacketSyncItemHandler() {
   }

   public void affectItem(EntityPlayerMP player, ItemStack stack, PacketSyncItem message) {
      if (stack != null) {
         ItemSyncable itemSyncable = (ItemSyncable)stack.getItem();
         itemSyncable.readSyncedNBT(message.data, stack);
      }

   }
}
