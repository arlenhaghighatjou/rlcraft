package ivorius.reccomplex.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import ivorius.ivtoolkit.network.IvPacketHelper;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketOpenGui implements IMessage {
   public int windowId;
   public String modId;
   public int modGuiId;
   public ByteBuf data;

   public PacketOpenGui() {
   }

   public PacketOpenGui(int windowId, String modId, int modGuiId, ByteBuf data) {
      this.windowId = windowId;
      this.modId = modId;
      this.modGuiId = modGuiId;
      this.data = data;
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.windowId);
      ByteBufUtils.writeUTF8String(buf, this.modId);
      buf.writeInt(this.modGuiId);
      IvPacketHelper.writeByteBuffer(buf, this.data);
   }

   public void fromBytes(ByteBuf buf) {
      this.windowId = buf.readInt();
      this.modId = ByteBufUtils.readUTF8String(buf);
      this.modGuiId = buf.readInt();
      this.data = IvPacketHelper.readByteBuffer(buf);
   }
}
