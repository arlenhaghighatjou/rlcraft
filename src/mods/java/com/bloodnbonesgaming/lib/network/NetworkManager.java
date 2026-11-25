package com.bloodnbonesgaming.lib.network;

import com.bloodnbonesgaming.lib.exception.MessageAlreadyRegisteredException;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gnu.trove.map.TByteObjectMap;
import gnu.trove.map.TObjectByteMap;
import gnu.trove.map.hash.TByteObjectHashMap;
import gnu.trove.map.hash.TObjectByteHashMap;
import java.util.BitSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkManager {
   public static final NetworkManager INSTANCE = new NetworkManager();
   final Map<Side, TObjectByteMap<Class<? extends ConsumableMessage>>> discriminators = Maps.newEnumMap(Side.class);
   final Map<Side, TByteObjectMap<Class<? extends ConsumableMessage>>> discriminatorsR = Maps.newEnumMap(Side.class);
   private final BNBSimpleNetworkWrapper serverToClient = new BNBSimpleNetworkWrapper("BNBServerToClient", Side.CLIENT);
   private final BNBSimpleNetworkWrapper clientToServer = new BNBSimpleNetworkWrapper("BNBClientToServer", Side.SERVER);
   private final Map<Side, BitSet> indicesMap = Maps.newEnumMap(Side.class);
   final Map<Side, Set<MessageConsumer>> consumers = Maps.newEnumMap(Side.class);

   private NetworkManager() {
      this.indicesMap.put(Side.CLIENT, new BitSet(256));
      this.indicesMap.put(Side.SERVER, new BitSet(256));
      this.discriminators.put(Side.SERVER, new TObjectByteHashMap());
      this.discriminators.put(Side.CLIENT, new TObjectByteHashMap());
      this.discriminatorsR.put(Side.SERVER, new TByteObjectHashMap());
      this.discriminatorsR.put(Side.CLIENT, new TByteObjectHashMap());
      this.consumers.put(Side.SERVER, Sets.newHashSet());
      this.consumers.put(Side.CLIENT, Sets.newHashSet());
   }

   public byte registerMessage(Side side, Class<? extends ConsumableMessage> messageClass) throws MessageAlreadyRegisteredException {
      byte disc = (byte)((BitSet)this.indicesMap.get(side)).nextClearBit(0);
      if (disc >= 0 && disc <= 127) {
         if (side == Side.SERVER) {
            this.clientToServer.registerMessage(messageClass, disc, side);
         } else {
            this.serverToClient.registerMessage(messageClass, disc, side);
         }

         ((BitSet)this.indicesMap.get(side)).set(disc);
         ((TObjectByteMap)this.discriminators.get(side)).put(messageClass, disc);
         return disc;
      } else {
         throw new RuntimeException(String.format("No more discriminator bytes left on side %s!", side));
      }
   }

   public byte registerMessage(Class<? extends ConsumableMessage> messageClass) throws MessageAlreadyRegisteredException {
      return this.registerMessage(FMLCommonHandler.instance().getEffectiveSide(), messageClass);
   }

   public byte findDiscriminator(Side side, Class<? extends ConsumableMessage> messageClass) {
      TObjectByteMap map = (TObjectByteMap)this.discriminators.get(side);
      byte disc = map.get(messageClass);
      return disc == map.getNoEntryValue() ? -1 : disc;
   }

   public byte findDiscriminator(Class<? extends ConsumableMessage> messageClass) {
      return this.findDiscriminator(FMLCommonHandler.instance().getEffectiveSide(), messageClass);
   }

   public Class<?> findClass(Side side, byte discriminator) {
      return !this.isDiscriminatorInUse(side, discriminator) ? null : (Class)((TByteObjectMap)this.discriminatorsR.get(side)).get(discriminator);
   }

   public Class<?> findClass(byte discriminator) {
      return this.findClass(FMLCommonHandler.instance().getEffectiveSide(), discriminator);
   }

   public boolean isDiscriminatorInUse(Side side, byte discriminator) {
      return ((BitSet)this.indicesMap.get(side)).get(discriminator);
   }

   public void registerConsumer(Side side, MessageConsumer consumer) {
      ((Set)this.consumers.get(side)).add(consumer);
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

   public Packet toPacket(IMessage message) {
      return this.serverToClient.getPacketFrom(message);
   }
}
