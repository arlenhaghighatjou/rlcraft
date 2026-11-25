package invtweaks.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;

public interface ITPacket {
   void readBytes(ByteBuf var1);

   void writeBytes(ByteBuf var1);

   void handle(INetHandler var1);
}
