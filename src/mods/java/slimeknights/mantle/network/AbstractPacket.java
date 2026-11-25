package slimeknights.mantle.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class AbstractPacket implements IMessage {
   public AbstractPacket() {
   }

   public abstract IMessage handleClient(NetHandlerPlayClient var1);

   public abstract IMessage handleServer(NetHandlerPlayServer var1);

   protected void writePos(BlockPos pos, ByteBuf buf) {
      buf.writeInt(pos.getX());
      buf.writeInt(pos.getY());
      buf.writeInt(pos.getZ());
   }

   protected BlockPos readPos(ByteBuf buf) {
      int x = buf.readInt();
      int y = buf.readInt();
      int z = buf.readInt();
      return new BlockPos(x, y, z);
   }
}
