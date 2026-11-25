package mcjty.lostcities.network;

import io.netty.buffer.ByteBuf;
import mcjty.lostcities.LostCities;
import mcjty.lostcities.dimensions.world.WorldTypeTools;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketReturnProfileToClient implements IMessage {
   private int dimension;
   private String profile;

   public void fromBytes(ByteBuf buf) {
      this.dimension = buf.readInt();
      this.profile = NetworkTools.readString(buf);
   }

   public void toBytes(ByteBuf buf) {
      buf.writeInt(this.dimension);
      NetworkTools.writeString(buf, this.profile);
   }

   public PacketReturnProfileToClient() {
   }

   public PacketReturnProfileToClient(int dimension, String profileName) {
      this.dimension = dimension;
      this.profile = profileName;
   }

   public static class Handler implements IMessageHandler<PacketReturnProfileToClient, IMessage> {
      public Handler() {
      }

      public IMessage onMessage(PacketReturnProfileToClient message, MessageContext ctx) {
         LostCities.proxy.addScheduledTaskClient(() -> {
            this.handle(message, ctx);
         });
         return null;
      }

      private void handle(PacketReturnProfileToClient message, MessageContext ctx) {
         WorldTypeTools.setProfileFromServer(message.dimension, message.profile);
      }
   }
}
