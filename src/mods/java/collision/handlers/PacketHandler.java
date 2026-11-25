package collision.handlers;

import collision.packets.PacketCollisionS;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
   public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("collisiondamage");

   public PacketHandler() {
   }

   public static void registerPacket() {
      INSTANCE.registerMessage(PacketCollisionS.CollisionMessageHandler.class, PacketCollisionS.class, 1, Side.SERVER);
   }
}
