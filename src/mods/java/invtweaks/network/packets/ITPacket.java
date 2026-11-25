package invtweaks.network.packets;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.network.INetHandler;

public interface ITPacket {
   void readBytes(ByteBuf var1);

   void writeBytes(ByteBuf var1);

   void handle(INetHandler var1);
}
