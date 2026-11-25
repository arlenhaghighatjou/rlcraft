package reborncore.common.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;
import java.util.zip.CRC32;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import reborncore.RebornCore;

public class NetworkManager {
   public static HashMap<Class<? extends INetworkPacket>, SimpleNetworkWrapper> packetWrapperMap = new HashMap();
   public static HashMap<String, SimpleNetworkWrapper> packageWrapperMap = new HashMap();
   private static HashMap<SimpleNetworkWrapper, NetworkManager.IntStore> wrapperIdList = new HashMap();
   public static ArrayList<NetworkManager.PacketDetails> packetList = new ArrayList();

   public NetworkManager() {
   }

   public static void load() {
      MinecraftForge.EVENT_BUS.post(new RegisterPacketEvent());
   }

   public static void sendToServer(INetworkPacket<?> packet) {
      checkPacket(packet);
      getWrapperForPacket(packet.getClass()).sendToServer(new PacketWrapper(packet));
   }

   public static void sendToAllAround(INetworkPacket<?> packet, TargetPoint point) {
      checkPacket(packet);
      getWrapperForPacket(packet.getClass()).sendToAllAround(new PacketWrapper(packet), point);
   }

   public static void sendToAll(INetworkPacket<?> packet) {
      checkPacket(packet);
      getWrapperForPacket(packet.getClass()).sendToAll(new PacketWrapper(packet));
   }

   public static void sendToPlayer(INetworkPacket<?> packet, EntityPlayerMP playerMP) {
      checkPacket(packet);
      getWrapperForPacket(packet.getClass()).sendTo(new PacketWrapper(packet), playerMP);
   }

   public static void sendToWorld(INetworkPacket<?> packet, World world) {
      checkPacket(packet);
      getWrapperForPacket(packet.getClass()).sendToDimension(new PacketWrapper(packet), world.provider.getDimension());
   }

   public static void checkPacket(INetworkPacket<?> packet) {
      if (getPacketDetails(packet.getClass()) == null) {
         throw new RuntimeException("Packet " + packet.getClass().getName() + " has not been registered");
      }
   }

   public static NetworkManager.PacketDetails getPacketDetails(Class<? extends INetworkPacket> clazz) {
      return (NetworkManager.PacketDetails)packetList.stream().filter((packetDetails) -> {
         return packetDetails.packetClass.equals(clazz);
      }).findAny().orElse((Object)null);
   }

   public static SimpleNetworkWrapper getWrapperForPacket(Class<? extends INetworkPacket> packetClass) {
      return !packetWrapperMap.containsKey(packetClass) ? null : (SimpleNetworkWrapper)packetWrapperMap.get(packetClass);
   }

   public static SimpleNetworkWrapper createOrGetNetworkWrapper(Class<? extends INetworkPacket> packetClass) {
      String wrapperName = getWrapperName(packetClass);
      if (packageWrapperMap.containsKey(wrapperName)) {
         return (SimpleNetworkWrapper)packageWrapperMap.get(wrapperName);
      } else {
         SimpleNetworkWrapper newNetworkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(wrapperName);
         RebornCore.logHelper.info("Created new network wrapper " + wrapperName);
         packageWrapperMap.put(wrapperName, newNetworkWrapper);
         return newNetworkWrapper;
      }
   }

   public static String getWrapperName(Class<? extends INetworkPacket> packetClass) {
      String packageName = packetClass.getCanonicalName().substring(0, packetClass.getCanonicalName().lastIndexOf("."));
      CRC32 crc = new CRC32();
      crc.update(packageName.getBytes());
      return "rc&" + packageName.substring(0, 11) + "&" + Long.toString(crc.getValue()).substring(0, 5);
   }

   public static NetworkManager.PacketDetails registerPacket(Class<? extends INetworkPacket> packetClass, Side side) {
      SimpleNetworkWrapper wrapper = createOrGetNetworkWrapper(packetClass);
      int id = getNextIDForWrapper(wrapper);
      wrapper.registerMessage(PacketWrapper.PacketWrapperHandler.class, PacketWrapper.class, id, side);
      packetWrapperMap.put(packetClass, wrapper);
      RebornCore.logHelper.info("Registed packet to " + getWrapperName(packetClass) + " side: " + side + " id:" + id);
      NetworkManager.PacketDetails packetDetails = new NetworkManager.PacketDetails(packetClass, id, wrapper);
      packetList.add(packetDetails);
      return packetDetails;
   }

   public static int getNextIDForWrapper(SimpleNetworkWrapper networkWrapper) {
      if (wrapperIdList.containsKey(networkWrapper)) {
         ++((NetworkManager.IntStore)wrapperIdList.get(networkWrapper)).id;
         return ((NetworkManager.IntStore)wrapperIdList.get(networkWrapper)).id;
      } else {
         wrapperIdList.put(networkWrapper, new NetworkManager.IntStore());
         return 0;
      }
   }

   private static class IntStore {
      int id;

      private IntStore() {
         this.id = 0;
      }
   }

   public static class PacketDetails {
      public Class<? extends INetworkPacket> packetClass;
      public int id;
      SimpleNetworkWrapper networkWrapper;

      public PacketDetails(Class<? extends INetworkPacket> packetClass, int id, SimpleNetworkWrapper networkWrapper) {
         this.packetClass = packetClass;
         this.id = id;
         this.networkWrapper = networkWrapper;
      }
   }
}
