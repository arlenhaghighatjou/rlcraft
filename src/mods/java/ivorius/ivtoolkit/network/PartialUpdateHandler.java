package ivorius.ivtoolkit.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;

public interface PartialUpdateHandler {
   void writeUpdateData(ByteBuf var1, String var2, Object... var3);

   void readUpdateData(ByteBuf var1, String var2);
}
