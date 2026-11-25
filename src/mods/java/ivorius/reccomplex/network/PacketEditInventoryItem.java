package ivorius.reccomplex.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketEditInventoryItem implements IMessage {
   private int inventorySlot;

   public PacketEditInventoryItem() {
   }

   public PacketEditInventoryItem(int inventorySlot) {
      this.inventorySlot = inventorySlot;
   }

   public int getInventorySlot() {
      return this.inventorySlot;
   }

   public void setInventorySlot(int inventorySlot) {
      this.inventorySlot = inventorySlot;
   }

   public void fromBytes(ByteBuf buf) {
      this.inventorySlot = buf.readInt();
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.inventorySlot);
   }
}
