package reborncore.common.network;

import java.io.IOException;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface INetworkPacket<T> {
   void writeData(ExtendedPacketBuffer var1) throws IOException;

   void readData(ExtendedPacketBuffer var1) throws IOException;

   /** @deprecated */
   @Deprecated
   void processData(T var1, MessageContext var2);
}
