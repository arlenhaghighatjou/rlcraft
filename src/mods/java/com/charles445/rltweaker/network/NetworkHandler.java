package com.charles445.rltweaker.network;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.hook.HookForge;
import com.charles445.rltweaker.util.TriConsumer;
import com.charles445.rltweaker.util.VersionDelimiter;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class NetworkHandler {
   public static boolean serverHasVersioning = false;
   public static VersionDelimiter serverVersion = VersionDelimiter.UNKOWN;
   private static Map<EaglercraftUUID, VersionDelimiter> clients = new ConcurrentHashMap();

   public NetworkHandler() {
   }

   public static void removeClient(EaglercraftUUID id) {
      if (clients.remove(id) != null) {
         RLTweaker.logger.trace("Removing NetworkHandler EaglercraftUUID");
      }

   }

   public static void addClient(EaglercraftUUID id, VersionDelimiter vd) {
      if (clients.put(id, vd) == null) {
         RLTweaker.logger.trace("Adding NetworkHandler EaglercraftUUID");
      }

   }

   public static boolean isVersionAtLeast(int major, int minor, EntityPlayer player) {
      return isVersionAtLeast(major, minor, 0, player.getGameProfile().getId());
   }

   public static boolean isVersionAtLeast(int major, int minor, int patch, EntityPlayer player) {
      return isVersionAtLeast(major, minor, patch, player.getGameProfile().getId());
   }

   public static boolean isVersionAtLeast(int major, int minor, int patch, EaglercraftUUID uuid) {
      VersionDelimiter value = (VersionDelimiter)clients.get(uuid);
      return value != null ? value.isSameOrNewerVersion(major, minor, patch) : false;
   }

   public static void addServerPacketExecutor(String name, TriConsumer<IMessageHandler, IMessage, MessageContext> consumer) {
      HookForge.addServer(name, consumer);
   }

   public static void addServerPacketExecutor(String name, TriConsumer<IMessageHandler, IMessage, MessageContext> consumer, boolean override) {
      HookForge.addServer(name, consumer, override);
   }

   public static void addServerPacketExecutor(String name, HookForge.MessageExecutor executor) {
      HookForge.addServer(name, executor);
   }
}
