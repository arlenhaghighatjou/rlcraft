package com.bloodnbonesgaming.lib.network;

import com.bloodnbonesgaming.lib.exception.MessageAlreadyRegisteredException;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkManager {
   public static final NetworkManager INSTANCE = new NetworkManager();
   private final Map<Side, Map<Class<? extends ConsumableMessage>, Byte>> discriminators = new EnumMap<>(Side.class);
   private final Map<Side, Map<Byte, Class<? extends ConsumableMessage>>> classes = new EnumMap<>(Side.class);
   private final Map<Side, Set<MessageConsumer>> consumers = new EnumMap<>(Side.class);
   private final SimpleNetworkWrapper serverToClient = NetworkRegistry.INSTANCE.newSimpleChannel("BNBServerToClient");
   private final SimpleNetworkWrapper clientToServer = NetworkRegistry.INSTANCE.newSimpleChannel("BNBClientToServer");

   private NetworkManager() {
      for (Side side : Side.values()) {
         this.discriminators.put(side, new LinkedHashMap<>());
         this.classes.put(side, new LinkedHashMap<>());
         this.consumers.put(side, new LinkedHashSet<>());
      }
   }

   public <T extends ConsumableMessage> byte registerMessage(Side side, Class<T> type, Supplier<T> factory) {
      Map<Class<? extends ConsumableMessage>, Byte> registered = this.discriminators.get(side);
      if (registered.containsKey(type)) {
         throw new MessageAlreadyRegisteredException();
      }
      if (registered.size() > Byte.MAX_VALUE) {
         throw new IllegalStateException("No discriminator bytes remain on side " + side);
      }
      byte id = (byte)registered.size();
      SimpleNetworkWrapper channel = side == Side.SERVER ? this.clientToServer : this.serverToClient;
      channel.registerMessage((message, context) -> {
         this.dispatch(message, id, context);
         return null;
      }, type, factory, id, side);
      registered.put(type, id);
      this.classes.get(side).put(id, type);
      return id;
   }

   public <T extends ConsumableMessage> byte registerMessage(Class<T> type, Supplier<T> factory) {
      return this.registerMessage(FMLCommonHandler.instance().getEffectiveSide(), type, factory);
   }

   public byte findDiscriminator(Side side, Class<? extends ConsumableMessage> type) {
      return this.discriminators.get(side).getOrDefault(type, (byte)-1);
   }

   public byte findDiscriminator(Class<? extends ConsumableMessage> type) {
      return this.findDiscriminator(FMLCommonHandler.instance().getEffectiveSide(), type);
   }

   public Class<?> findClass(Side side, byte id) {
      return this.classes.get(side).get(id);
   }

   public Class<?> findClass(byte id) {
      return this.findClass(FMLCommonHandler.instance().getEffectiveSide(), id);
   }

   public boolean isDiscriminatorInUse(Side side, byte id) {
      return this.classes.get(side).containsKey(id);
   }

   public void registerConsumer(Side side, MessageConsumer consumer) {
      this.consumers.get(side).add(consumer);
   }

   private void dispatch(ConsumableMessage message, byte id, MessageContext context) {
      if (message.isConsumed()) {
         return;
      }
      BNBMessageContext bnbContext = new BNBMessageContext(context.netHandler, context.side);
      Set<IMessage> replies = new LinkedHashSet<>();
      for (MessageConsumer consumer : this.consumers.get(context.side)) {
         IMessage reply = consumer.consume(message, id, bnbContext);
         if (reply != null) {
            replies.add(reply);
         }
         if (message.isConsumed()) {
            break;
         }
      }
      for (IMessage reply : replies) {
         if (context.side == Side.CLIENT) {
            this.sendToServer(reply);
         } else {
            this.sendToClient(reply, context.getServerHandler().player);
         }
      }
   }

   public void sendToClient(IMessage message, EntityPlayerMP player) {
      this.serverToClient.sendTo(message, player);
   }

   public void sendToAllClientsAround(IMessage message, TargetPoint point) {
      this.serverToClient.sendToAllAround(message, point);
   }

   public void sendToAllClients(IMessage message) {
      this.serverToClient.sendToAll(message);
   }

   public void sendToDimension(IMessage message, int dimensionId) {
      this.serverToClient.sendToDimension(message, dimensionId);
   }

   public void sendToServer(IMessage message) {
      this.clientToServer.sendToServer(message);
   }

   public Packet<?> toPacket(IMessage message) {
      return this.serverToClient.getPacketFrom(message);
   }
}
