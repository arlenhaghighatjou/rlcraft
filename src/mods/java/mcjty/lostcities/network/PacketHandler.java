package mcjty.lostcities.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
   private static int ID = 12;
   private static int packetId = 0;
   public static SimpleNetworkWrapper INSTANCE = null;

   public static int nextPacketID() {
      return packetId++;
   }

   public PacketHandler() {
   }

   public static int nextID() {
      return ID++;
   }

   public static void registerMessages(String channelName) {
      INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
      registerMessages();
   }

   public static void registerMessages() {
      INSTANCE.registerMessage(new PacketRequestProfile.Handler(), PacketRequestProfile.class, PacketRequestProfile::new, nextID(), Side.SERVER);
      INSTANCE.registerMessage(new PacketReturnProfileToClient.Handler(), PacketReturnProfileToClient.class, PacketReturnProfileToClient::new, nextID(), Side.CLIENT);
   }
}
