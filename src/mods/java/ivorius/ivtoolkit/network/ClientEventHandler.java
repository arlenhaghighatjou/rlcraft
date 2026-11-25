package ivorius.ivtoolkit.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public interface ClientEventHandler {
   void assembleClientEvent(ByteBuf var1, String var2, Object... var3);

   void onClientEvent(ByteBuf var1, String var2, EntityPlayerMP var3);
}
