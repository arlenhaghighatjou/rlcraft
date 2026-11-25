package reborncore.common.network;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.lang3.Validate;

public class PacketWrapper implements IMessage {
   INetworkPacket packet;

   public PacketWrapper(INetworkPacket packet) {
      this.packet = packet;
   }

   public PacketWrapper() {
   }

   public void fromBytes(ByteBuf buf) {
      try {
         ExtendedPacketBuffer packetBuffer = new ExtendedPacketBuffer(buf);
         String name = packetBuffer.readString(packetBuffer.readInt());
         this.packet = (INetworkPacket)Class.forName(name).newInstance();
      } catch (IllegalAccessException | ClassNotFoundException | InstantiationException var5) {
         var5.printStackTrace();
      }

      try {
         this.packet.readData(new ExtendedPacketBuffer(buf));
      } catch (IOException var4) {
         var4.printStackTrace();
      }

   }

   public void toBytes(ByteBuf buf) {
      Validate.notNull(this.packet);
      ExtendedPacketBuffer packetBuffer = new ExtendedPacketBuffer(buf);
      packetBuffer.writeInt(this.packet.getClass().getCanonicalName().length());
      packetBuffer.writeString(this.packet.getClass().getCanonicalName());

      try {
         this.packet.writeData(new ExtendedPacketBuffer(buf));
      } catch (IOException var4) {
         var4.printStackTrace();
      }

   }

   public static class PacketWrapperHandler implements IMessageHandler<PacketWrapper, IMessage> {
      public PacketWrapperHandler() {
      }

      public IMessage onMessage(PacketWrapper message, MessageContext ctx) {
         message.packet.processData(message.packet, ctx);
         return null;
      }
   }
}
