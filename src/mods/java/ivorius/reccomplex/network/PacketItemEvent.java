package ivorius.reccomplex.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import ivorius.ivtoolkit.network.IvPacketHelper;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketItemEvent extends PacketEditInventoryItem {
   public String context;
   public ByteBuf payload;

   public PacketItemEvent() {
   }

   public PacketItemEvent(int inventorySlot, ByteBuf payload, String context) {
      super(inventorySlot);
      this.payload = payload;
      this.context = context;
   }

   public void fromBytes(ByteBuf buf) {
      super.fromBytes(buf);
      this.context = ByteBufUtils.readUTF8String(buf);
      this.payload = IvPacketHelper.readByteBuffer(buf);
   }

   public void toBytes(ByteBuf buf) {
      super.toBytes(buf);
      ByteBufUtils.writeUTF8String(buf, this.context);
      IvPacketHelper.writeByteBuffer(buf, this.payload);
   }
}
