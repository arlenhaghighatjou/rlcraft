package ivorius.ivtoolkit.network;

import io.netty.buffer.ByteBuf;

public interface PartialUpdateHandler {
   void writeUpdateData(ByteBuf var1, String var2, Object... var3);

   void readUpdateData(ByteBuf var1, String var2);
}
