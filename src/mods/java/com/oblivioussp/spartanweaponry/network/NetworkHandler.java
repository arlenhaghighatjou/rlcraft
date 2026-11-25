package com.oblivioussp.spartanweaponry.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {
   protected static SimpleNetworkWrapper instance;
   protected static int currentId = 0;

   public NetworkHandler() {
   }

   public static void init() {
      instance = NetworkRegistry.INSTANCE.newSimpleChannel("spartanweaponry".toLowerCase());
      instance.registerMessage(PacketLongReachAttack.class, PacketLongReachAttack.class, getNextPacketId(), Side.SERVER);
      instance.registerMessage(PacketKeyHandle.class, PacketKeyHandle.class, getNextPacketId(), Side.SERVER);
   }

   public static int getNextPacketId() {
      int id = currentId++;
      return id;
   }

   public static void sendPacketToAll(PacketBase packet) {
      instance.sendToAll(packet);
   }

   public static void sendPacketTo(PacketBase packet, EntityPlayerMP player) {
      instance.sendTo(packet, player);
   }

   public static void sendPacketToAllAround(PacketBase packet, TargetPoint point) {
      instance.sendToAllAround(packet, point);
   }

   public static void sendPacketToDimension(PacketBase packet, int dimensionId) {
      instance.sendToDimension(packet, dimensionId);
   }

   public static void sendPacketToServer(PacketBase packet) {
      instance.sendToServer(packet);
   }
}
