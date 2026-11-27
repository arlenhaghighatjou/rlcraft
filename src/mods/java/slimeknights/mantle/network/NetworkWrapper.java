package slimeknights.mantle.network;

import java.util.function.Supplier;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkWrapper {
   public final SimpleNetworkWrapper network;
   protected final NetworkWrapper.AbstactPacketHandler handler;
   private int id = 0;

   public NetworkWrapper(String channelName) {
      this.network = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
      this.handler = new NetworkWrapper.AbstactPacketHandler();
   }

   public <T extends AbstractPacket> void registerPacket(Class<T> packetClazz, Supplier<T> factory) {
      this.registerPacketClient(packetClazz, factory);
      this.registerPacketServer(packetClazz, factory);
   }

   public <T extends AbstractPacket> void registerPacketClient(Class<T> packetClazz, Supplier<T> factory) {
      this.registerPacketImpl(packetClazz, factory, Side.CLIENT);
   }

   public <T extends AbstractPacket> void registerPacketServer(Class<T> packetClazz, Supplier<T> factory) {
      this.registerPacketImpl(packetClazz, factory, Side.SERVER);
   }

   private <T extends AbstractPacket> void registerPacketImpl(Class<T> packetClazz, Supplier<T> factory, Side side) {
      this.network.registerMessage((IMessageHandler<T, IMessage>)(IMessageHandler)this.handler, packetClazz, factory, this.id++, side);
   }

   public static class AbstactPacketHandler implements IMessageHandler<AbstractPacket, IMessage> {
      public AbstactPacketHandler() {
      }

      public IMessage onMessage(AbstractPacket packet, MessageContext ctx) {
         return ctx.side == Side.SERVER ? packet.handleServer(ctx.getServerHandler()) : packet.handleClient(ctx.getClientHandler());
      }
   }
}
