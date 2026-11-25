package ivorius.reccomplex.network;

import io.netty.buffer.ByteBuf;
import ivorius.reccomplex.item.ItemSyncable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketSyncItem extends PacketEditInventoryItem {
   public NBTTagCompound data;

   public PacketSyncItem() {
   }

   public PacketSyncItem(int inventorySlot, ItemStack stack) {
      super(inventorySlot);
      ItemSyncable itemSyncable = (ItemSyncable)stack.getItem();
      this.data = new NBTTagCompound();
      itemSyncable.writeSyncedNBT(this.data, stack);
   }

   public void fromBytes(ByteBuf buf) {
      super.fromBytes(buf);
      this.data = ByteBufUtils.readTag(buf);
   }

   public void toBytes(ByteBuf buf) {
      super.toBytes(buf);
      ByteBufUtils.writeTag(buf, this.data);
   }
}
