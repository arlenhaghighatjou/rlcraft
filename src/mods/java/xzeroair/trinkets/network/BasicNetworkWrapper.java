package xzeroair.trinkets.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class BasicNetworkWrapper {
   public final SimpleNetworkWrapper network;
   protected final BasicNetworkWrapper.PacketHandler handler;
   private int id = 0;

   public BasicNetworkWrapper(String channelName) {
      this.network = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
      this.handler = new BasicNetworkWrapper.PacketHandler();
   }

   public void registerPacket(Class<? extends BasicPacket> packetClazz) {
      this.registerPacketClient(packetClazz);
      this.registerPacketServer(packetClazz);
   }

   public void registerPacketClient(Class<? extends BasicPacket> packetClazz) {
      this.registerPacketImpl(packetClazz, Side.CLIENT);
   }

   public void registerPacketServer(Class<? extends BasicPacket> packetClazz) {
      this.registerPacketImpl(packetClazz, Side.SERVER);
   }

   private void registerPacketImpl(Class<? extends BasicPacket> packetClazz, Side side) {
      this.network.registerMessage(this.handler, packetClazz, this.id++, side);
   }

   public static class PacketHandler implements IMessageHandler<BasicPacket, IMessage> {
      public PacketHandler() {
      }

      public IMessage onMessage(BasicPacket message, MessageContext ctx) {
         return ctx.side == Side.SERVER ? message.handleServer(ctx.getServerHandler()) : message.handleClient(ctx.getClientHandler());
      }
   }
}
